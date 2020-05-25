package scorch;

import ScorchServer.ScorchServer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ScorchMain {
    public static void main(String[] args) {
        final Map<String, String> params = new HashMap<>();
        params.put("gameWidth", "640");
        params.put("gameHeight", "480");
        params.put("gameServer", "localhost");
        params.put("port", "4242");
        params.put("leaveURL", "http://localhost");
        params.put("helpURL", "http://localhost");
        params.put("banners", null);

        int idx = 0;
        while (idx < args.length) {
            if (args[idx].startsWith("--")) {
                if (args[idx].equals("--server")) {
                    String[] serverArgs = new String[args.length - idx - 1];
                    System.arraycopy(args, idx, serverArgs, 0, serverArgs.length);

                    ScorchServer.main(serverArgs);
                    return;
                }

                if (args[idx].equals("--help")) {
                    System.out.println("To start the server, run with --server.\nTo configure the game, use the following config keys.\nDefault settings listed here:\n");
                    for (String key : params.keySet()) {
                        System.out.println("--" + key + " " + params.get(key));
                    }
                    System.exit(0);
                }
                params.put(args[idx].substring(2), args[idx + 1]);
                idx++;
            }
            idx++;
        }

        JFrame frame = new JFrame("Schorched Earth 2000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 1));

        ScorchApplet applet = new ScorchApplet() {
            @Override
            public String getParameter(String name) {
                System.out.println(name);
                return params.getOrDefault(name, "");
            }

            @Override
            public URL getDocumentBase() {
                try {
                    return new URL("http", params.get("gameServer"), Integer.parseInt(params.get("port")), "bar");
                } catch (Exception e) {
                    System.err.println("Malformed server URL: " + e);
                    System.exit(1);
                    return null;
                }
            }

            @Override
            public URL getCodeBase() {
                return getDocumentBase();
            }
        };

        frame.add(applet);
        frame.setPreferredSize(
                new Dimension(
                        Integer.parseInt(params.get("gameWidth")),
                        Integer.parseInt(params.get("gameHeight"))
                )
        );
        frame.pack();

        applet.init();
        applet.start();

        frame.setVisible(true);
    }
}
