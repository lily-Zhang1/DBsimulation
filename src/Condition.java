import java.util.ArrayList;
import java.util.Stack;
/**
 * Process the condition
 *
 * @author lily
 * */
public class Condition
{
    private String[] input;
    private String[] condition;
    private int startPosition;

    public Condition(String[] input, int start)
    {
        this.input = input;
        this.startPosition = start;
        this.condition = separateCondition();
    }

    /** Only return the content of the condition part */
    public String[] separateCondition()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = startPosition; i < input.length; i++) {
            sb.append(input[i]).append("-");
        }
        return sb.toString().split("-");
    }

    /** Analyze whether the condition statement is illegal */
    public String interpretCondition(String tablePath, String[][] content)
    {
        if (!parser()) { return "ERROR: Invalid query"; }
        if (parseOperator(tablePath) != null) { return parseOperator(tablePath); }
        int[] rowNum= getRowNumber(content);
        if (rowNum == null) {
            return "ERROR: Attribute cannot be converted to number"; }
        if (rowNum[0] == 0) {
            return "No row satisfies the condition."; }
        return null;
    }

    /** Parsing grammar is correct */
    public boolean parser()
    {
        if (!parseBrackets() || !parseOperatorNum()) {
            return false;
        }
        return true;
    }

    /** Parsing whether the parentheses match */
    public boolean parseBrackets()
    {
        int countBrackets = 0;
        for (int i = 0; i < condition.length; i++) {
            if (condition[i].equals("(")) {
                countBrackets++;
            }
            else if (condition[i].equals(")")) {
                countBrackets--;
            }
        }
        if (countBrackets == 0) { return true; }
        return false;
    }

    /** Check if the number of operators is correct */
    public boolean parseOperatorNum()
    {
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < condition.length; i++) {
            if(condition[i].equals(")")) {
                stack.pop();
                stack.pop();
                stack.pop();
                stack.pop();
                stack.push(" ");
                continue;
            }
            stack.push(condition[i]);
        }
        if (stack.size() == 3 ) { return true; }
        return false;
    }

    /** Determine whether the corresponding operand behind Operator is correct
     * and whether the previous attribute exists */
    public String parseOperator(String tablePath)
    {
        Table curtable = new Table(tablePath);
        Tool tool = new Tool();
        for (int i = 0; i < condition.length; i++) {
            if (isNumOperator(condition[i])) {
                if (!tool.isAllDigit(condition[i+1])) {
                    return "ERROR: Number expected";
                }
            }
            if (isLikeOperator(condition[i])) {
                if (!tool.isAlphabetic(condition[i+1])) {
                    return "ERROR: String expected";
                }
            }
            if (isNumOperator(condition[i]) || isLikeOperator(condition[i])
                    || isEqualOperator(condition[i])) {
                if (curtable.findAttributePos(tablePath, condition[i-1]) == -1) {
                    return "ERROR: Attribute ["+condition[i-1]+"] does not exist";
                }
            }
            if (condition[i].equals("=")) {
                return "ERROR: error operator";
            }
        }
        return null;
    }

    /** Get a list of row numbers in content that meet the conditions */
    public int[] getRowNumber(String[][] content)
    {
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < condition.length; i++) {
            if(condition[i].equals(")")) {
                ArrayList<Integer> result = dealSingleCond(content, stack);
                if (result == null) { return null; }
                //Push "(" out of the stack
                stack.pop();
                String str = arrayToString(result);
                //Push the result of the first condition into the stack
                stack.push(str);
                continue;
            }
            stack.push(condition[i]);
        }
        ArrayList<Integer> result = dealSingleCond(content, stack);
        if (result == null) { return null; }
        if (result.size() == 0) { result.add(0); }
        return string2Int(result.toString());
    }

    /** Process a single condition and return an array of
     * line numbers that satisfy the condition */
    public ArrayList<Integer> dealSingleCond(String[][] content, Stack<String> stack)
    {
        String oper3 = stack.pop();
        String oper2 = stack.pop();
        String oper1 = stack.pop();
        Table table = new Table();
        //Find the column number where the Attribute is located
        int position = table.findAttributePos(content[0], oper1);
        ArrayList<Integer> result = new ArrayList<>();
        //When the operator is >,> =, <, <=
        if (isNumOperator(oper2)) {
            for (int j = 1; j < content.length; j++) {
                int re = conditionCompare(oper2, content[j][position], oper3, j);
                if (re == -1) { return null; }
                if (re != 0) { result.add(re); }
            }
        }
        //When the operator is == or! =
        else if (isEqualOperator(oper2)) {
            for (int j = 1; j < content.length; j++) {
                int re = conditionEqual(oper2, content[j][position], oper3, j);
                if (re != 0) { result.add(re); }
            }
        }
        //When the operator is LIKE
        else if (isLikeOperator(oper2)) {
            for (int j = 1; j < content.length; j++) {
                int re = conditionLike(oper2, content[j][position], oper3, j);
                if (re != 0) { result.add(re); }
            }
        }
        else if (oper2.toUpperCase().equals("OR")) {
            result = conditionOr(oper1, oper3);
        }
        else if (oper2.toUpperCase().equals("AND")) {
            result = conditionAnd(oper1, oper3);
        }
        return result;
    }

    /** When the operator is AND, return an array of satisfied line numbers */
    public ArrayList<Integer> conditionAnd(String leftStr, String rightStr)
    {
        int[] left = string2Int(leftStr);
        int[] right = string2Int(rightStr);
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < left.length; i++) {
            for (int j = 0; j < right.length; j++) {
                if (left[i] == right[j] && left[i] != 0) {
                    result.add(right[j]);
                }
            }
        }
        return result;
    }

    /** When the operator is OR, return an array of satisfied row numbers */
    public ArrayList<Integer> conditionOr(String leftStr, String rightStr)
    {
        int[] left = string2Int(leftStr);
        int[] right = string2Int(rightStr);
        ArrayList<Integer> result = new ArrayList<>();
        if (left != null) {
            for (int i = 0; i < left.length; i++) {
                if (left[i] == 0) { continue; }
                result.add(left[i]);
            }
        }
        if (right != null) {
            for (int j = 0; j < right.length; j++) {
                if (right[j] == 0) { continue; }
                if (!numberExisted(result,right[j])) {
                    result.add(right[j]);
                }
            }
        }
        return result;
    }

    /** Determine whether a row meets the < <= > >= condition */
    public int conditionCompare(String operator, String leftStr, String rightStr, int rowNum)
    {
        Tool tool = new Tool();
        if (!tool.isAllDigit(leftStr)) { return -1; }
        Float left = Float.parseFloat(leftStr);
        Float right = Float.parseFloat(rightStr);
        if (operator.equals(">")) {
            if (left > right) { return rowNum; }
        }
        if (operator.equals(">=")) {
            if (left >= right) { return rowNum; }
        }
        if (operator.equals("<")) {
            if (left < right) { return rowNum; }
        }
        if (operator.equals("<=")) {
            if (left <= right) { return rowNum; }
        }
        return 0;
    }

    /** Determine whether a row meets the LIKE condition */
    public int conditionLike(String operator, String leftStr, String rightStr, int rowNum)
    {
        if (operator.toUpperCase().equals("LIKE")) {
            if (leftStr.contains(rightStr)) { return rowNum; }
        }
        return 0;
    }

    /** Determine if a line meets the ==! = Condition */
    public int conditionEqual(String operator, String leftStr, String rightStr, int rowNum)
    {
        if (operator.equals("==")) {
            if (leftStr.equals(rightStr)) { return rowNum; }
        }
        if (operator.equals("!=")) {
            if (!leftStr.equals(rightStr)) { return rowNum; }
        }
        return 0;
    }

    /** judge if a number whether is exist in your array */
    public boolean numberExisted(ArrayList<Integer> result, int number)
    {
        for (Integer i : result) {
            if (i == number) { return true; }
        }
        return false;
    }

    /** make string type into int[] */
    public int[] string2Int(String string)
    {
        if (string == null || string.equals("")) { return null; }
        String[] str;
        if (string.contains(",")) { str = string.split(","); }
        else { str = string.split("\t"); }
        int[] result = new int[str.length];
        for (int i = 0; i < str.length; i++) {
            Tool tool = new Tool();
            result[i] = Integer.parseInt(tool.leftDigit(str[i]));
        }
        return result;
    }

    /** make array type into string type */
    public String arrayToString(ArrayList<Integer> array)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.size(); i++) {
            sb.append(array.get(i));
            if (i != array.size() - 1) { sb.append(","); }
        }
        return sb.toString();
    }

    /** judge if the operator is the sigals likes: > >= < <= */
    public boolean isNumOperator(String operator)
    {
        if (operator.equals(">") || operator.equals("<")
        || operator.equals(">=") || operator.equals("<=")) {
            return true;
        }
        return false;
    }

    /** judge if the operator is: LIKE */
    public boolean isLikeOperator(String operator)
    {
        if (operator.toUpperCase().equals("LIKE")) { return true; }
        return false;
    }

    /** judge if the operator is the sigals like: == != */
    public boolean isEqualOperator(String operator)
    {
        if (operator.equals("==") || operator.equals("!=")) { return true; }
        return false;
    }

}
