/**
 * Delete Command
 *
 * @author lily
 * */
public class DeleteCommand extends Command
{
    private String[] input;
    /** According to the BNF rule, condition starts from the fourth string */
    final static char START = 4;

    public DeleteCommand(String[] input) { this.input = input; }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        if (!input[0].toUpperCase().equals("DELETE")) { return false; }
        if (!input[1].toUpperCase().equals("FROM")) { return false; }
        if (!input[3].toUpperCase().equals("WHERE")) { return false; }
        return true;
    }

    public String deleteOperation(String tablePath)
    {
        Table curtable = new Table(tablePath);
        String allLine = curtable.readAllLine(tablePath);
        String[][] content = curtable.seperateTo2DArray(allLine);
        Condition condition = new Condition(input, START);
        String information = condition.interpretCondition(tablePath,content);
        if (information != null ) { return information; }
        int[] rowNum= condition.getRowNumber(content);
        return deleteAndWrite(content, rowNum, curtable);
    }

    /** Delete the corresponding row and write it back to the table */
    public String deleteAndWrite(String[][] content, int[] rowNum, Table curtable)
    {
        StringBuffer sb = new StringBuffer();
        //Delete the corresponding line
        for (int i = 0; i < content.length; i++) {
            if (!isDeleteIndex(i, rowNum)) {
                for (int j = 0; j < content[i].length; j++) {
                    sb.append(content[i][j]);
                    if (j != content[i].length -1 ) { sb.append("\t"); }
                }
                sb.append("\n");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        //Write the deleted content back to the table
        curtable.writeToTable(curtable.getName(), sb.toString(),false);
        return "OK";
    }

    /** Determine if a row is a row to be deleted */
    private boolean isDeleteIndex(int index, int[] rowNum)
    {
        for (int i = 0; i < rowNum.length; i++) {
            if (index == rowNum[i]) { return true; }
        }
        return false;
    }

}
