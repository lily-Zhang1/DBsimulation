import java.io.File;
/**
 * Drop Command
 *
 * @author lily
 * */
public class DropCommand extends Command
{
    private String[] input;

    public DropCommand(String[] input)
    {
        this.input = input;
    }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        if (input.length != 3) { return false; }
        return true;
    }

    /** Get the type to be deleted */
    public String getDropType()
    {
        if (input[1].toUpperCase().equals("DATABASE")) {
            return "DATABASE";
        }
        else if (input[1].toUpperCase().equals("TABLE")) {
            return "TABLE";
        }
        return null;
    }

    /** Delete the database */
    public String dropDb(Database currentdb)
    {
        String dbPath = getDbPath(input[2]);
        if ( !currentdb.searchDb(dbPath) ) {
            return "ERROR: Database ["+input[2]+"] isn't existed";
        }
        File dbFolder = new File(dbPath);
        deleteDir(dbFolder);
        //If the deleted database is the current database, set the database name to null
        if (input[2].equals(currentdb.getName())) {
            currentdb.setName(null);
        }
        return "OK";
    }

    /** Remove folders and files */
    public void deleteDir(File dbfolder)
    {
        if (dbfolder.isDirectory()) {
            for (File f: dbfolder.listFiles()) {
                deleteDir(f);
            }
        }
        dbfolder.delete();
    }

    /** Delete table */
    public String dropTb(Database currentdb)
    {
        String tablePath = getTbPath(currentdb, input[2]);
        if (tablePath == null) {
            return "ERROR: You should use a database firstly.";
        }
        Table curtable = new Table(tablePath);
        if (!curtable.searchTable(tablePath)) {
            return "ERROR: Table ["+input[2]+"] isn't existed!";
        }
        File fileToOpen = new File(tablePath);
        fileToOpen.delete();
        return "OK";
    }

}
