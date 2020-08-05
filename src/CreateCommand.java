import java.io.File;
import java.io.IOException;
/**
 * Create Command
 *
 * @author lily
 * */
public class CreateCommand extends Command
{
    private String[] input;
    /** According to the BNF rule, AttributeList starts from the fourth string */
    final static char START = 4;

    public CreateCommand(String[] input) { this.input = input; }

    /** judge the type is database or table */
    public String getCreateType()
    {
        if (input[1].toUpperCase().equals("DATABASE")) {
            return "DATABASE";
        }
        else if (input[1].toUpperCase().equals("TABLE")) {
            return "TABLE";
        }
        return null;
    }

    /** Parsing whether the syntax for creating the database is correct */
    public boolean parserDb()
    {
        if (input.length != 3) { return false; }
        return true;
    }

    /** Parsing the syntax of creating table is correct */
    public boolean parserTb()
    {
        if (input.length < 3) { return false; }
        if (input.length > 3) {
            if (!input[3].equals("(") || !input[input.length-1].equals(")")){
                return false;
            }
            //parseing the AttributeList is right or not
            int count = 0;
            for (int i = START; i < input.length-2; i++) {
                count++;
                if (count % 2 == 1 && input[i].equals(",")) { return false; }
                if (count % 2 == 0 && !input[i].equals(",")) { return false; }
            }
        }
        return true;
    }

    /** Set up Attribution */
    public void setAttributeList(String tablePath)
    {
        Table curtable = new Table(tablePath);
        StringBuffer sb = new StringBuffer("id" + "\t");
        Tool tool = new Tool();
        String str = tool.cleanString(input, START, input.length-1);
        sb.append(str);
        //false means overwrite
        curtable.writeToTable(tablePath, sb.toString(), false);
    }

    /** create database */
    public String createDb(Database currentdb)
    {
        String dbPath = getDbPath(input[2]);
        if (currentdb.searchDb(dbPath)) {
            return "ERROR: Database ["+input[2]+"] has been existed";
        }
        File dbFolder = new File(dbPath);
        dbFolder.mkdir();
        return "OK";
    }

    /** Create a table in the current database */
    public String createTb(Database currentdb)
    {
        String tablePath = getTbPath(currentdb, input[2]);
        if (tablePath == null) {
            return "ERROR: You should use a database firstly.";
        }
        Table curtable = new Table(tablePath);
        if (curtable.searchTable(tablePath)) {
            return "ERROR: Table ["+input[2]+"] has been existed";
        }
        try {
            File fileToOpen = new File(tablePath);
            if (input.length == 3) {
                fileToOpen.createNewFile();
                return "OK";
            }
            fileToOpen.createNewFile();
            setAttributeList(tablePath);
        } catch (IOException e) { System.err.println(e); }
        return "OK";
    }

}
