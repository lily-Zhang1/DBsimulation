import java.io.*;
import java.util.Scanner;
/**
 *
 * @author lily
 * */

public class Table
{
    private String name;
    final static char SPACE = 2;

    public Table() {  }

    public Table(String tbname) { name = tbname; }

    public String getName() { return name; }

    /** Check if a table exists */
    public boolean searchTable(String tablePath)
    {
        File fileToOpen = new File(tablePath);
        if(fileToOpen.exists()) {
            return true;
        }
        return false;
    }

    /** See if a table is empty */
    public boolean checkEmpty(String tablePath)
    {
        File file = new File(tablePath);
        if(file.exists() && file.length() == 0) {
            return true;
        }
        return false;
    }

    /** Read the first line of the table */
    public String readFirstLine(String tablePath)
    {
        if (searchTable(tablePath)) {
            try {
                File fileToOpen = new File(tablePath);
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                String firstLine = buffReader.readLine();
                buffReader.close();
                return firstLine;
            } catch (IOException ioe) { System.err.println(ioe); }
        }
        return "ERROR: Table does not exist";
    }

    /** Read all the contents of the table */
    public String readAllLine(String tablePath)
    {
        StringBuffer sb = new StringBuffer();
        if (searchTable(tablePath)) {
            try {
                File fileToOpen = new File(tablePath);
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                String line;
                while((line = buffReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                buffReader.close();
                return sb.toString();
            } catch (IOException ioe) { System.err.println(ioe); }
        }
        return "ERROR: Table does not exist";
    }

    /** Split the string into a two-dimensional array */
    public String[][] seperateTo2DArray(String content)
    {
        String[] str1 = content.split("\n");
        String[][] str2 = new String[str1.length][];
        for (int i = 0; i < str1.length; i++) {
            String[] temp = str1[i].split("\t");
            str2[i] = new String[temp.length];
            for (int j = 0; j < temp.length; j++) {
                str2[i][j] = temp[j];
            }
        }
        return str2;
    }

    /** find the number of line of the attribute */
    public int findAttributePos(String tablePath, String attributeName)
    {
        String firstLine = readFirstLine(tablePath);
        String[] attributeList = firstLine.split("\t");
        return findAttributePos(attributeList, attributeName);
    }

    /** find the number of line of the attribute */
    public int findAttributePos(String[] attributeList, String attributeName)
    {
        for (int i = 0; i < attributeList.length; i ++) {
            if (attributeList[i].equals(attributeName)) {
                return i;
            }
        }
        return -1;
    }

    /** Read the number of rows of table */
    public int readLineNum(String tablePath)
    {
        int count = 0;
        if (searchTable(tablePath)) {
            try {
                File fileToOpen = new File(tablePath);
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                while((buffReader.readLine()) != null) {
                    count++;
                }
                buffReader.close();
            } catch (IOException ioe) { System.err.println(ioe); }
        }
        return count;
    }

    /** Write to the table, flag equal to false for overwrite,
     * otherwise write for additional */
    public void writeToTable(String tablePath, String str, boolean flag)
    {
        try {
            File fileToOpen = new File(tablePath);
            FileWriter writer = new FileWriter(fileToOpen, flag);
            if (str.charAt(str.length()-1) != '\n') {
                writer.write( str + "\n");
            } else { writer.write(str); }
            writer.flush();
            writer.close();
        } catch(IOException ioe) { System.err.println(ioe); }
    }

    /** Get the current last id */
    public int getLastId(File fileToOpen)
    {
        FileReader reader = null;
        try { reader = new FileReader(fileToOpen);
        } catch (IOException ioe) { System.err.println(ioe); }
        Scanner sc = new Scanner(reader);
        String firstLine = sc.nextLine();
        String line = firstLine;
        String lastLine = null;
        do {
            if(!sc.hasNextLine()){
                lastLine = line;
            }
        } while((sc.hasNextLine() && (line=sc.nextLine())!=null
                && line!="" && line != "\n"));
        sc.close();
        if (lastLine.equals(firstLine) && lastLine.charAt(0) == 'i') {
            return 0;
        } else {
            String[] str = lastLine.split("\t");
            return Integer.parseInt(str[0]);
        }
    }

    /** Get the longest string length */
    private int longestContent(String[][] content)
    {
        int theLongest = 0;
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                if (content[i][j].length() > theLongest) {
                    theLongest = content[i][j].length();
                }
            }
        }
        return theLongest;
    }

    /** Print table */
    public String printTable(String printContent)
    {
        String[][] content = seperateTo2DArray(printContent);
        int theLongest = longestContent(content) + SPACE;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                sb.append(content[i][j]);
                int length = content[i][j].length();
                while(length++ < theLongest) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
