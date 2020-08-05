import java.util.HashMap;
/**
 * update command
 *
 * @author lily
 * */
public class UpdateCommand extends Command
{
    private String[] input;

    public UpdateCommand(String[] input) { this.input = input; }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        int findPosition = findWherePosition();
        if (!input[0].toUpperCase().equals("UPDATE")
                || !input[2].toUpperCase().equals("SET") ) { return false; }
        if (findPosition == 0) { return false; }
        //parsing the NameValueList is right or not
        if (findPosition-3 < 3) { return false; }
        if (findPosition-3 == 3 ) {
            if (input[4].equals("=")) {return true;}
            return false;
        }
        if ((findPosition-2) % 4 != 0) { return false; }
        for (int i = 3; i < findPosition; i++) {
            if (!input[++i].equals("=")) { return false; }
            i++;
            if (++i < findPosition && !input[i].equals(",")) {return false;}
        }
        return true;
    }

    /** Find the keyword where is the index */
    public int findWherePosition()
    {
        for (int i = 0; i < input.length; i++) {
            if (input[i].toUpperCase().equals("WHERE")) { return i; }
        }
        return 0;
    }

    /** Perform update operation */
    public String updateOperation(String tablePath)
    {
        Table curtable = new Table(tablePath);
        String allLine = curtable.readAllLine(tablePath);
        //Convert the contents of the table into a two-dimensional array
        String[][] content = curtable.seperateTo2DArray(allLine);
        Condition condition = new Condition(input, findWherePosition()+1);
        String information = condition.interpretCondition(tablePath,content);
        if (information != null ) { return information; }
        int[] rowNum= condition.getRowNumber(content);
        //Get the content to be updated, key is the column number to be updated,
        // and value is the updated content
        HashMap<Integer, String> updateContent = updateContent(tablePath);
        if (updateContent == null ) {
            return "ERROR: Attribute ["+input[3]+"] does not exist."; }
        return updateAndWrite(content,rowNum,updateContent,curtable);
    }

    /** split NameValuePair */
    private String[] splitNameValuePair()
    {
        //Get the NameValuePair that needs to be updated
        StringBuffer sb1 = new StringBuffer();
        for (int i = 3; i < findWherePosition(); i++) {
            sb1.append(input[i]);
        }
        //Split each NameValuePair
        String[] str1 = sb1.toString().split(",");
        StringBuffer sb2 = new StringBuffer();
        //Split and store each NameValuePair in the map
        for (int i = 0; i < str1.length; i++) {
            String[] str2 = str1[i].split("=");
            for (int j = 0; j < str2.length; j++) {
                sb2.append(str2[j]).append("-");
            }
        }
        return sb2.toString().split("-");
    }

    /** Find the updated content and save the column number
     * and corresponding updated content to the map */
    public HashMap<Integer, String> updateContent(String tablePath)
    {
        Table curTable = new Table(tablePath);
        HashMap<Integer, String> map = new HashMap<>();
        String[] result = splitNameValuePair();
        for (int i = 0; i < result.length; i++) {
            //Find the column number corresponding to name and save it as the key
            int key = curTable.findAttributePos(tablePath, result[i]);
            if (key == -1) { return null; }
            //Store the value corresponding to the key
            map.put(key, result[++i]);
        }
        return map;
    }

    /** Update the corresponding content and write it back to the table */
    public String updateAndWrite(String[][] content, int[] rowNum,
                               HashMap<Integer,String> updateContent, Table curtable)
    {
        for (int i = 0; i < rowNum.length; i++) {
            for (Integer j : updateContent.keySet()) {
                content[rowNum[i]][j] = updateContent.get(j);
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                sb.append(content[i][j]).append("\t");
            }
            sb.append("\n");
        }
        curtable.writeToTable(curtable.getName(), sb.toString(),false);
        return "OK";
    }

}
