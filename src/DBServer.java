import java.io.*;
import java.net.*;

class DBServer
{
    final static char EOT = 4;
    private Database currentdb;

    public static void main(String args[]) { new DBServer(8888); }

    public DBServer(int portNumber)
    {
        try {
            currentdb = new Database();
            System.out.println("Server Listening");
            ServerSocket ss = new ServerSocket(portNumber);
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while(true) acceptNextConnection(in, out);
        } catch(IOException ioe) { System.err.println(ioe); }
    }

    private void acceptNextConnection(BufferedReader in, BufferedWriter out)
    {
        Tool tool = new Tool();
        try {
            String line = in.readLine();
            if (line != null) {
                if ( !tool.checkSemicolon(line)) {
                    line = "ERROR: Semi colon missing at end of line";
                } else { line = processNextCommand(line); }
                Table table = new Table();
                line = table.printTable(line);
                out.write("Server response: " + line + "\n");
                out.write(EOT);
                out.write("\n");
                out.flush();
            }
        } catch(IOException ioe) { System.err.println(ioe); }
    }

    private String processNextCommand(String line)
    {
        line = deleteSpace(line);
        Processor processor = new Processor(currentdb);
        if(processor.setInput(line) == false) { return "ERROR: Invalid query"; }
        String firstKey = line.split(" ")[0].toUpperCase();
        if (firstKey.equals("CREATE")) { return processor.create(); }
        if (firstKey.equals("USE")) { return processor.use(); }
        if (firstKey.equals("DROP")) { return processor.drop(); }
        if (firstKey.equals("ALTER")) { return processor.alter(); }
        if (firstKey.equals("INSERT")) { return processor.insert(); }
        if (firstKey.equals("SELECT")) { return processor.select(); }
        if (firstKey.equals("UPDATE")) { return processor.update(); }
        if (firstKey.equals("DELETE")) { return processor.delete(); }
        if (firstKey.equals("JOIN")) { return processor.join(); }
        return "ERROR: error input!";
    }

    private String deleteSpace(String line)
    {
        StringBuffer sb = new StringBuffer();
        int i = -1;
        while(line.charAt(++i)==' ');
        for (; i < line.length(); i++) { sb.append(line.charAt(i)); }
        return sb.toString();
    }


}
