import java.io.File;
/**
 * insert Command
 *
 * @author lily
 * */
public class InsertCommand extends Command
{
    private String[] input;
    /** According to the BNF rule, the contents of the
     *  inserted table starts from the fifth string */
    final static char START = 5;

    public InsertCommand(String[] input) { this.input = input; }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        if(!input[1].toUpperCase().equals("INTO")
                || !input[3].toUpperCase().equals("VALUES")
                || !input[4].equals("(")
                || !input[input.length-1].equals(")")) {
            return false;
        }
        //parseing the valueList is right or not
        int count = 0;
        for (int i = START; i < input.length-2; i++) {
            count++;
            if (count % 2 == 1 && input[i].equals(",")) { return false; }
            if (count % 2 == 0 && !input[i].equals(",")) { return false; }
        }
        return true;
    }

    /** Insert a row of data */
    public String insertOperation(String tablePath)
    {
        Table curtable = new Table(tablePath);
        File fileToOpen = new File(tablePath);
        StringBuffer sb = new StringBuffer();
        Tool tool = new Tool();
        String str = tool.cleanString(input, START, input.length-1);
        //If the file is empty, the attrbute line inserted
        if ( curtable.checkEmpty(tablePath) ) {
            sb.append("id").append("\t");
            sb.append(str);
            curtable.writeToTable(tablePath, sb.toString(), false);
        }
        //Otherwise insert the data row
        else {
            int id = curtable.getLastId(fileToOpen) + 1;
            sb.append(id).append("\t");;
            sb.append(str);
            curtable.writeToTable(tablePath, sb.toString(), true);
        }
        return "OK";
    }

}
