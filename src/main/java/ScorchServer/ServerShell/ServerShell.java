/*
  The Scorch Server Shell (console)
*/

package ScorchServer.ServerShell;

import java.io.*;
import java.util.*;
import java.net.Socket;

import ScorchServer.ScorchServer;
import ScorchServer.ServerShell.commands.*;

public class ServerShell implements Runnable {
    public final List<shellCommand> commands = List.of(
            new addkg(),
            new boot(),
            new desync(),
            new help(),
            new lg(),
            new passwd(),
            new say(),
            new shout(),
            new shutdown(),
            new whois()
    );

    private final Hashtable<String, shellCommand> commandsHash = new Hashtable<>();
    private ScorchServer owner = null;
    private final BufferedReader in;
    private final PrintWriter out;
    private Socket socket = null;

    public ServerShell(ScorchServer ss) {
        super();

        this.owner = ss;
        //take over standart input and output
        in = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(System.out, true);
    }

    //alternative constructor to use for spawning by RemoteServerShell
    public ServerShell(BufferedReader i, PrintWriter o, Socket s) {
        super();

        this.socket = s;
        this.in = i;
        this.out = o;
    }

    //provides common interface for output of all commands
    public void print(String msg) {
        out.print(msg);
        out.flush();
    }

    //provides common interface for output of all commands
    public void println(String msg) {
        out.println(msg);
    }

    private boolean executeCommand(String cmd) {
        String command = null;
        Vector<String> v = new Vector<>();
        shellCommand commandMethod;

        if (cmd == null) return true;

        String separators = " ";
        StringTokenizer st = new StringTokenizer(cmd, separators);

        if (st.hasMoreTokens())
            command = st.nextToken();

        if (command == null)
            return true;

        if ("quit".equals(command))
            return false;

        //get the rest of arguments
        while (st.hasMoreTokens())
            v.addElement(st.nextToken());

        commandMethod = commandsHash.get(command);

        try {
            if (commandMethod == null)
                println(" Command " + command + " not found.\n");
            else
                commandMethod.run(v, this);
        } catch (Exception e) {
            println("invoke failed: " + e);
        }

        return true;
    }

    private void loadCommands() {
        print("Loading commands ");

        for (shellCommand cmd : commands) {
            commandsHash.put(cmd.getClass().getName().split("\\.")[3], cmd);
        }
        println(" Done.\n");
    }

    public void run() {
        //load the available commands into the hashtable
        loadCommands();

        String prompt = "ServerShell-> ";
        print(prompt);
        try {

            while (this.executeCommand(in.readLine()))
                print(prompt);

        } catch (Exception e) {
            System.err.println("Shell error: " + e);
        }

        println("\nExiting... Goodbye");
        disconnect();
    }

    private void disconnect() {
        try {
            in.close();
            out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.err.println("Shell: disconnect failed " + e);
        }
    }

    public void shutdownServer() {
        owner.shutdown();
    }
}
