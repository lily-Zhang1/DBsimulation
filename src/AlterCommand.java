/**
 * alter commad
 *
 * @author lily
 * */
public class AlterCommand extends Command
{
    private String[] input;

    public AlterCommand(String[] input) { this.input = input; }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        if (input.length != 5) { return false; }
        if (!input[1].toUpperCase().equals("TABLE")) { return false; }
        if (!input[3].toUpperCase().equals("ADD")
                && !input[3].toUpperCase().equals("DROP")) {
            return false;
        }
        return true;
    }

    /** Get the type of alter as add or drop */
    public String alterType()
    {
        if (input[3].toUpperCase().equals("ADD")) { return "ADD"; }
        if (input[3].toUpperCase().equals("DROP")) { return "DROP"; }
        return null;
    }

    /** Add an Attribution at the end */
    public String addOperation(String tablePath)
    {
        Table curtable = new Table(tablePath);
        StringBuffer sb = new StringBuffer();
        //If the table is empty, the increment is property is written directly to the table
        if ( curtable.checkEmpty(tablePath) ) {
            sb.append(input[4]+"\n");
        } else {
            //Find the location of the attribute, if -1 means the attribute does not exist,
            //otherwise the attribute already exists, an error is reported
            int position = curtable.findAttributePos(tablePath, input[4]);
            if (position != -1) {
                return "ERROR: Attribution ["+input[4]+"] has been existed!";
            }
            //Read the content of the table and store it in the string
            String content = curtable.readAllLine(tablePath);
            //Split the contents of table by row and store as a string array
            String[] lineList = content.split("\n");
            int count = 0;
            //Add the attribute to the last position of the first line,
            //the other lines are filled with spaces
            for (int i = 0; i < lineList.length; i++) {
                sb.append(lineList[i]);
                if (lineList[i].charAt(lineList[i].length()-1) != '\t')
                { sb.append("\t"); }
                if (++count == 1) { sb.append(input[4]); }
                else { sb.append(" "); }
                sb.append("\n");
            }
        }
        curtable.writeToTable(tablePath, sb.toString(), false);
        return "OK";
    }

    /** Delete an Attribution */
    public String dropOperation(String tablePath)
    {
        Table curtable = new Table(tablePath);
        if ( curtable.checkEmpty(tablePath) ) {
            return "ERROR: Table ["+input[2]+"] is empty!"; }
        if ( input[4].equals("id")) { return "ERROR: You can't drop id!"; }
        //Find the location of the attribute, if -1 means the attribute does not exist,
        //an error is reported
        int position = curtable.findAttributePos(tablePath, input[4]);
        if ( position == -1) {
            return "ERROR: Attribution ["+input[4]+"] does not exist"; }
        //Read the content of the table and store it in a string, and split the string
        // into a two-dimensional array
        String content = curtable.readAllLine(tablePath);
        String[][] lineList = curtable.seperateTo2DArray(content);
        StringBuffer sb = new StringBuffer();
        //Delete the corresponding column in each row of data
        for ( int i = 0; i < lineList.length; i++) {
            for (int j = 0; j < lineList[i].length; j++) {
                if (j == position) { continue; }
                sb.append(lineList[i][j]);
                if (j != lineList[i].length-1 && j!= position-1) { sb.append("\t"); }
            }
            sb.append("\n");
        }
        curtable.writeToTable(tablePath, sb.toString(), false);
        return "OK";
    }
}
