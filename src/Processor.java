import java.util.ArrayList;
/**
 * Processing of user input
 *
 * @author lily
 * */
public class Processor {
    private String[] input;
    private Database currentdb;

    public Processor(Database currentdb)
    {
        this.currentdb = currentdb;
    }

    public boolean setInput(String line)
    {
        input = seperateToArray(line);
        if (input == null) { return false; }
        return true;
    }

    /** make the string into string[]，and delet； */
    private String[] seperateToArray(String line)
    {
        String[] inputList = line.split(" ");
        StringBuffer sb = new StringBuffer();
        ArrayList<String> array = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < inputList.length; i++) {
            for (int j = 0; j < inputList[i].length(); j++) {
                char ch = inputList[i].charAt(j);
                if (ch == ',' || ch == '(' || ch == ')') {
                    if (count % 2 == 1) { return null; }
                    sb = addAndRefreshSb(sb, array, ch);
                }
                else if (ch == '\'') { count++; }
                else if (ch != ';'){ sb.append(ch); }
            }
            if (count % 2 == 1) {
                sb.append(" ");
                continue;
            }
            sb = addAndRefreshSb(sb, array, ' ');
        }
        String[] strArray = array.toArray(new String[0]);
        return strArray;
    }

    /** Add the content and character ch in StringBuffer to array,
     * and refresh StringBuffer at the same time */
    public StringBuffer addAndRefreshSb(StringBuffer sb, ArrayList<String> array, char ch)
    {
        if (sb.length() != 0) {
            array.add(sb.toString());
            sb = new StringBuffer();
        }
        if (ch != ' ') { array.add(Character.toString(ch)); }
        return sb;
    }

    /** Process the command beginning with use */
    public String use()
    {
        UseCommand useCom = new UseCommand(input);
        if (!useCom.parser()) {
            return "ERROR: Invalid query";
        }
        String dbName = input[1];
        String dbPath = useCom.getDbPath(dbName);
        if (!currentdb.searchDb(dbPath)) {
            return "ERROR: Unknown database: "+dbName;
        }
        currentdb.setName(dbName);
        return "OK";
    }

    /** Process the command beginning with create */
    public String create()
    {
        CreateCommand createCom = new CreateCommand(input);
        if (createCom.getCreateType().equals("DATABASE")) {
            if (!createCom.parserDb()) { return "ERROR: Invalid query"; }
            return createCom.createDb(currentdb);
        }
        else if (createCom.getCreateType().equals("TABLE")) {
            if (!createCom.parserTb()) { return "ERROR: Invalid query"; }
            return createCom.createTb(currentdb);
        }
        return "ERROR: Invalid query";
    }

    /** Process the command beginning with drop */
    public String drop()
    {
        DropCommand dropCom = new DropCommand(input);
        if (!dropCom.parser()) { return "ERROR: Invalid query"; }
        if (dropCom.getDropType().equals("DATABASE")) {
            return dropCom.dropDb(currentdb);
        }
        else if (dropCom.getDropType().equals("TABLE")) {
            return dropCom.dropTb(currentdb);
        }
        return "ERROR: Invalid query";
    }

    /** Process the command beginning with alter */
    public String alter()
    {
        AlterCommand alterCom = new AlterCommand(input);
        if ( !alterCom.parser() ) { return "ERROR: Invalid query"; }
        String tablePath = alterCom.getTbPath(currentdb, input[2]);
        if ( tablePath == null ) {
            return "ERROR: You should use a database firstly.";
        }
        Table curtable = new Table(tablePath);
        if ( !curtable.searchTable(tablePath) ) {
            return "ERROR: Table ["+input[2]+"] does not exist";
        }
        if ( alterCom.alterType().equals("ADD") ) {
            return alterCom.addOperation(tablePath);
        }
        if ( alterCom.alterType().equals("DROP") ) {
            return alterCom.dropOperation(tablePath);
        }
        return "ERROR: Invalid query";
    }

    /** Process the commands beginning with insert */
    public String insert()
    {
        InsertCommand insertCom = new InsertCommand(input);
        if (!insertCom.parser()) { return "ERROR: Invalid query"; }
        String tablePath = insertCom.getTbPath(currentdb, input[2]);
        if (tablePath == null) { return "ERROR: You should use a database firstly."; }
        Table curtable = new Table(tablePath);
        if (!curtable.searchTable(tablePath)) {
            return "ERROR: Table ["+input[2]+"] isn't existed!";
        }
        return insertCom.insertOperation(tablePath);
    }

    /** Process the command beginning with select */
    public String select()
    {
        SelectCommand selectCom = new SelectCommand(input);
        if (!selectCom.parser()) { return "ERROR: Invalid query"; }
        int fromPosition = selectCom.findFromPosition();
        String tablePath = selectCom.getTbPath(currentdb, input[fromPosition+1]);
        if (tablePath == null) { return "ERROR: You should use a database firstly."; }
        Table curtable = new Table(tablePath);
        if (!curtable.searchTable(tablePath)) {
            return "ERROR: Table ["+input[fromPosition+1]+"] does not exist"; }
        if (curtable.checkEmpty(tablePath)) {
            return "ERROR: Table ["+input[fromPosition+1]+"] is empty!"; }
        return selectCom.selectOperation(tablePath);
    }

    /** Process the commands beginning with update */
    public String update()
    {
        UpdateCommand updateCom = new UpdateCommand(input);

        if (!updateCom.parser()) { return "ERROR: Invalid query"; }
        String tablePath = updateCom.getTbPath(currentdb, input[1]);
        if (tablePath == null) { return "ERROR: You should use a database firstly."; }
        Table curtable = new Table(tablePath);
        if (!curtable.searchTable(tablePath)) {
            return "ERROR: Table ["+input[1]+"] does not existed"; }
        if (curtable.checkEmpty(tablePath)) {
            return "ERROR: Table ["+input[1]+"] is empty!"; }
        return updateCom.updateOperation(tablePath);
    }

    /** Process the commands beginning with delete */
    public String delete()
    {
        DeleteCommand deleteCom = new DeleteCommand(input);
        if (!deleteCom.parser()) { return "ERROR: Invalid query"; }
        String tablePath = deleteCom.getTbPath(currentdb, input[2]);
        if (tablePath == null) { return "ERROR: You should use a database firstly."; }
        Table curtable = new Table(tablePath);
        if (!curtable.searchTable(tablePath)) {
            return "ERROR: Table ["+input[2]+"] does not existed"; }
        if (curtable.checkEmpty(tablePath)) {
            return "ERROR: Table ["+input[2]+"] is empty!"; }
        return deleteCom.deleteOperation(tablePath);
    }

    /** Processing the commands beginning with join */
    public String join()
    {
        JoinCommand joinCom = new JoinCommand(input);
        if (!joinCom.parser()) { return "ERROR: Invalid query"; }
        String tablePath1 = joinCom.getTbPath(currentdb, input[1]);
        String tablePath2 = joinCom.getTbPath(currentdb, input[3]);
        if (tablePath1 == null || tablePath2 == null) {
            return "ERROR: You should use a database firstly.";
        }
        Table curtable = new Table(tablePath1);
        if ( !curtable.searchTable(tablePath1)) {
            return "ERROR: Table ["+input[1]+"] isn't existed!";
        }
        if ( !curtable.searchTable(tablePath2)) {
            return "ERROR: Table ["+input[3]+"] isn't existed!";
        }
        if (curtable.checkEmpty(tablePath1)) {
            return "ERROR: Table ["+input[1]+"] is empty!"; }
        if (curtable.checkEmpty(tablePath2)) {
            return "ERROR: Table ["+input[3]+"] is empty!"; }
        int firstColumn = curtable.findAttributePos(tablePath1, input[5]);
        int secondColumn = curtable.findAttributePos(tablePath2, input[7]);
        if (firstColumn == -1 ) {
            return "ERROR: Attribute ["+input[5]+"] does not exist!"; }
        if (secondColumn == -1) {
            return "ERROR: Attribute ["+input[7]+"] does not exist!"; }
        return joinCom.joinOperation(tablePath1, tablePath2);
    }

}
