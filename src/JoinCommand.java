import java.util.*;
/**
 * join Command
 *
 * @author lily
 * */
public class JoinCommand extends Command
{
    private String[] input;

    public JoinCommand(String[] input) { this.input = input; }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        if (input.length != 8) { return false; }
        if (!input[0].toUpperCase().equals("JOIN")) { return false; }
        if (!input[2].toUpperCase().equals("AND")) { return false; }
        if (!input[4].toUpperCase().equals("ON")) { return false; }
        if (!input[6].toUpperCase().equals("AND")) { return false; }
        return true;
    }

    /** Return the result of joining two tables */
    public String joinOperation(String tablePath1, String tablePath2)
    {
        Table curtable = new Table(tablePath1);
        int firstColumn = curtable.findAttributePos(tablePath1, input[5]);
        int secondColumn = curtable.findAttributePos(tablePath2, input[7]);
        String[][] content1 = curtable.seperateTo2DArray(curtable.readAllLine(tablePath1));
        String[][] content2 = curtable.seperateTo2DArray(curtable.readAllLine(tablePath2));
        String firstLine1 = curtable.readFirstLine(tablePath1);
        String firstLine2 = curtable.readFirstLine(tablePath2);
        Map<Integer, Integer> map;
        List<Map.Entry<Integer, Integer>> sortIds;
        StringBuffer sb;
        if (content1.length >= content2.length ) {
            //Create a connection pair of two tables
            map = createMap(content1, content2, firstColumn, secondColumn);
            //The first table is key, so sort by key
            sortIds = sort(map, "key");
            sb = insertFirstLine(firstLine1, firstLine2);
            //flag is "key" means the first table is the key value in the map
            sb.append(insertContent(sortIds, content1, content2, "key"));
        } else {
            map = createMap(content2, content1, secondColumn, firstColumn);
            //The first table is value, so sort by value
            sortIds = sort(map, "value");
            sb = insertFirstLine(firstLine1, firstLine2);
            //flag is "value" means the first table is the value in the map
            sb.append(insertContent(sortIds, content1, content2, "value"));
        }
        return sb.toString();
    }

    /** Create a connection pair of two tables */
    public Map<Integer, Integer> createMap(String[][] content1, String[][] content2, int firstColumn, int secondColumn)
    {
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 1; i < content1.length; i++) {
            for (int j = 1; j < content2.length; j++) {
                if (content1[i][firstColumn].equals(content2[j][secondColumn])) {
                    map.put(i, j);
                }
            }
        }
        return map;
    }

    /** Insert first row -- attribute name */
    private StringBuffer insertFirstLine( String firstLine1, String firstLine2)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\nid\t");
        //Process the name of the first line
        firstLine1 = dealFirstLine(input[1], firstLine1);
        firstLine2 = dealFirstLine(input[3], firstLine2);
        sb.append(firstLine1+firstLine2);
        sb.append("\n");
        return sb;
    }

    /** Process the name of the first line */
    private String dealFirstLine(String tableName, String firtLine)
    {
        String[] lineList = firtLine.split("\t");
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < lineList.length; i++) {
            sb.append(tableName).append(".").append(lineList[i]);
            sb.append("\t");
        }
        return sb.toString();
    }

    /** Sort by flag */
    public List<Map.Entry<Integer, Integer>> sort(Map<Integer, Integer> map, String flag)
    {
        List<Map.Entry<Integer, Integer>> sortIds = new ArrayList<>(map.entrySet());
        //Sort by key or value from small to large
        Collections.sort(sortIds, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                if (flag.equals("value")) { return (o1.getValue() - o2.getValue()); }
                else { return (o1.getKey() - o2.getKey()); }
            }
        });
        return sortIds;
    }

    /** change String[] to String */
    private String list2String(String[] lineList)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < lineList.length; i++) {
            sb.append(lineList[i]);
            sb.append("\t");
        }
        return sb.toString();
    }

    /** flag is "key" means the first table is the key in the map */
    private String insertContent(List<Map.Entry<Integer, Integer>> infoIds,
                                 String[][] content1, String[][] content2, String flag)
    {
        StringBuffer sb = new StringBuffer();
        int count = 1;
        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<Integer, Integer> id = infoIds.get(i);
            sb.append(count++);
            sb.append("\t");
            if (flag.equals("key")) {
                sb.append(list2String(content1[id.getKey()]));
                sb.append(list2String(content2[id.getValue()]));
            } else {
                sb.append(list2String(content1[id.getValue()]));
                sb.append(list2String(content2[id.getKey()]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
