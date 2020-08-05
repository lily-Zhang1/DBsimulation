import java.io.File;
/**
 * Command class
 * super class of other commad class
 *
 * @author lily
 * */
public class Command
{
    /** find the path of your database */
    public String getDbPath(String dbName)
    {
        String dbPath = "." + File.separator + dbName;
        return dbPath;
    }

    /** find the path of your table */
    public String getTbPath(Database currentdb, String tbName)
    {
        if (currentdb.getName() == null) { return null; }
        String tablePath = "." + File.separator + currentdb.getName()
                + File.separator + tbName + ".txt";
        return tablePath;
    }
}
