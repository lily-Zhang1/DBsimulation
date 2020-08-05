import java.io.*;
/**
 *
 * @author lily
 * */
public class Database
{
    private String name;

    public void setName(String dbname) { name = dbname; }

    public String getName() { return name; }

    /** searching a database is existed or not */
    public boolean searchDb(String dbPath)
    {
        File documentFolder = new File(dbPath);
        if (!documentFolder.exists() || !documentFolder.isDirectory()) {
            return false;
        }
        return true;
    }

}
