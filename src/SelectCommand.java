/**
 * select commad
 *
 * @author lily
 * */
public class SelectCommand extends Command
{
    private String[] input;

    public SelectCommand(String[] input) { this.input = input; }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        int fromPosition = findFromPosition();
        if (fromPosition == -1) { return false; }
        if (fromPosition != input.length - 2 &&
                !input[fromPosition+2].toUpperCase().equals("WHERE")) {
            return false;
        }
        //parsing the WildAttribList is right or not
        int count = 0;
        for (int i = 1; i < fromPosition; i++) {
            if (input[1].equals("*")) { break; }
            count++;
            if (count % 2 == 1 && input[i].equals(",")) { return false; }
            if (count % 2 == 0 && !input[i].equals(",")) { return false; }
        }
        return true;
    }

    /** Determine whether it is a simple query (no condition) */
    public boolean isSimpleSelect()
    {
        if (input[input.length-2].toUpperCase().equals("FROM")) {
            return true;
        }
        return false;
    }

    /** Get the column number that meets the condition */
    public int[] getColNum(String selectCol, String firstLine)
    {
        String[] selectList;
        if (selectCol == null) {
            selectList = firstLine.split("\t");
        } else {
            selectList = selectCol.split("\t");
        }
        String[] attributeList = firstLine.split("\t");
        int[] selectPosition = new int[selectList.length];
        int k = 0;
        for (int i = 0; i < attributeList.length; i++) {
            for (int j = 0; j < selectList.length; j++) {
                if (attributeList[i].equals(selectList[j])) {
                    selectPosition[k++] = i;
                }
            }
        }
        //If no attribute is found, set the first one to -1
        if (k != selectList.length) { selectPosition[0] = -1; }
        return selectPosition;
    }

    /** Find the position of from in the statement */
    public int findFromPosition()
    {
        for (int i = 1; i < input.length; i++) {
            if (input[i].toUpperCase().equals("FROM")) {
                return i;
            }
        }
        return -1;
    }

    /** Perform select operation */
    public String selectOperation(String tablePath)
    {
        Table curtable = new Table(tablePath);
        Tool tool = new Tool();
        String selectColumn;
        if (input[1].equals("*")) { selectColumn = null; }
        else { selectColumn = tool.cleanString(input, 1, findFromPosition()); }
        //Read the first row of the table
        String firstLine = curtable.readFirstLine(tablePath);
        //Read all contents of table
        String allLine = curtable.readAllLine(tablePath);
        String[][] content = curtable.seperateTo2DArray(allLine);
        //The column number corresponding to the selected attributes
        int[] columnNum = getColNum(selectColumn, firstLine);
        if (columnNum[0] == -1) { return "ERROR: Attribute does not exist"; }
        if (isSimpleSelect()) {
            return simpleSelect(tablePath, content, columnNum);
        }
        return complicatedSelect(tablePath, content, columnNum);
    }

    /** Simple query (no condition) */
    public String simpleSelect(String tablePath, String[][] content, int[] columnNum)
    {
        Table curtable = new Table(tablePath);
        //Get the number of table rows
        int lineNumber = curtable.readLineNum(tablePath);
        int[] rowNum = new int[lineNumber];
        for (int i = 0; i < lineNumber; i++) {
            rowNum[i] = i;
        }
        return printSelect(rowNum, columnNum, content);
    }

    /** Query with condition */
    public String complicatedSelect(String tablePath, String[][] content, int[] columnNum)
    {
        //Judging the condition
        Condition condition = new Condition(input, findFromPosition()+3);
        String information = condition.interpretCondition(tablePath,content);
        if (information != null ) { return information; }
        int[] rowNum= condition.getRowNumber(content);
        return printSelect(rowNum, columnNum, content);
    }

    /** According to the given line number, column number,
     * print out the query results */
    public String printSelect(int[] rowNum, int[] columnNum, String[][] content)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for (int i = 0; i < columnNum.length; i++) {
            sb.append(content[0][columnNum[i]]).append("\t");
        }
        sb.append("\n");
        for (int i = 0; i < rowNum.length; i++) {
            if (rowNum[i] == 0 || rowNum[i] >= content.length) { continue; }
            for (int j = 0; j < columnNum.length; j++) {
                if (columnNum[j] >= content[i].length) { continue; }
                sb.append(content[rowNum[i]][columnNum[j]]).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
