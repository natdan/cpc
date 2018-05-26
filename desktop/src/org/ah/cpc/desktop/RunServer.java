package org.ah.cpc.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RunServer {

    private static int port;

    public static void main(String[] args) throws Exception {
        File path;
        if (args.length > 0) {
            path = new File(args[0]);
        } else {
            File here = new File("");
            File absoluteFile = here.getAbsoluteFile();
            String hereName = absoluteFile.getName();
            if (hereName.endsWith("-localweb")) {
                String fixedName = hereName.substring(0, hereName.length() - 9);
                path = new File(absoluteFile.getParentFile(), fixedName + "-html/war");
            } else if (hereName.endsWith("-desktop")) {
                String fixedName = hereName.substring(0, hereName.length() - 8);
                path = new File(absoluteFile.getParentFile(), fixedName + "-html/war");
            } else if (hereName.equals("desktop")) {
                String fixedName = hereName.substring(0, hereName.length() - 7);
                path = new File(absoluteFile.getParentFile(), fixedName + "html/war");
            } else {
                throw new RuntimeException("This project must be called same as main + -localweb or -desktop; got " + hereName);
            }
        }

        File gamesDir = new File(path.getParentFile().getParentFile(), "games");

        startServer(path, gamesDir, 8088);
    }

    private static void startServer(File path, File gamesDir, int p) throws IOException {
        port = p;
        System.out.println("Starting server, path: " + path.getPath());
        System.out.println("  on url http://localhost:" + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ServeFilesHandler(path, gamesDir));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class ServeFilesHandler implements HttpHandler {

        private File dir;
        private File gamesDir;

        public ServeFilesHandler(File dir, File gamesDir) {
            this.dir = dir;
            this.gamesDir = gamesDir;
        }

        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            File file = null;
            if (path.startsWith("/games/")) {
                File gameDir;
                String gamePath = path.substring(7);
                if ("".equals(gamePath)) {
                    fileNotFound(exchange, path);
                    return;
                }
                int i = gamePath.indexOf('/');
                if (i >= 0) {
                    gameDir = new File(gamesDir, gamePath.substring(0, i));
                    gamePath = gamePath.substring(i + 1);
                } else {
                    redirectTo(exchange, path + "/index.html");
                    return;
                }

                if (!gameDir.exists()) {
                    fileNotFound(exchange, gameDir.getAbsolutePath());
                    return;
                }

                if ("".equals(gamePath)) {
                    gamePath = "index.html";
                }

                if (gamePath.startsWith("assets/")) {
                    if (gamePath.equals("assets/assets.txt")) {
                        String assets = collectAssets(gameDir, new File(gamesDir, "data"));

                        exchange.sendResponseHeaders(200, assets.length());
                        exchange.getResponseBody().write(assets.getBytes());
                        exchange.getResponseBody().close();

                        return;
                    } else if (gamePath.startsWith("assets/data/")) {
                        file = new File(gamesDir, gamePath.substring(7));
                    } else {
                        file = new File(gameDir, gamePath.substring(7));
                    }

                    if (!file.exists()) {
                        fileNotFound(exchange, gameDir.getAbsolutePath());
                    } else {
                        sendFile(exchange, file);
                    }
                } else {
                    file = new File(dir, gamePath);

                    if (!file.exists()) {
                        fileNotFound(exchange, file.getAbsolutePath());
                    } else if (file.isDirectory()) {
                        fileNotFound(exchange, file.getAbsolutePath());
                    }
                    sendFile(exchange, file);
                }

            } else {
                file = new File(dir, path);
            }

            if (!file.exists()) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            } else if (file.isDirectory()) {
                File index = new File(file, "index.html");
                if (!index.exists()) {
                    StringWriter res = new StringWriter();
                    res.append("<html><body><ul>\n");
                    for (String f : file.list()) {
                        res.append("<li><a href=\"" + makePath(path, f) + "\">" + makePath(path, f) + "</a></li>\n");
                    }
                    res.append("</ul></body></html>\n");
                    String response = res.toString();
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();
                } else {
                    sendFile(exchange, index);
                }
            } else {
                sendFile(exchange, file);
            }
        }

        private void redirectTo(HttpExchange exchange, String path) throws IOException {
            exchange.getResponseHeaders().add("Location"," http://localhost:" + port + path);
            exchange.sendResponseHeaders(302, 0);
            exchange.getResponseBody().close();
        }

        private void fileNotFound(HttpExchange exchange, String file) throws IOException {
            System.err.println("File not found: " + file);
            StringWriter out = new StringWriter();
            out.write("<html><body>Cannot find ");
            out.write(file);
            out.write(" </body></html>");
            String body = out.toString();

            exchange.sendResponseHeaders(404, body.length());
            exchange.getResponseBody().write(body.getBytes());
            exchange.getResponseBody().close();
        }

        private void sendFile(HttpExchange exchange, File file) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", mimeType(file.getName()));
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            FileInputStream fis = new FileInputStream(file);
            try {
                byte[] buffer = new byte[10240];
                int r = fis.read(buffer);
                while (r > 0) {
                    os.write(buffer, 0, r);
                    r = fis.read(buffer);
                }
            } finally {
                fis.close();
            }
            os.close();
        }

        private String makePath(String path, String file) {
            if (path.startsWith("/")) {
                return path.substring(1) + file;
            } else {
                return path + file;
            }
        }
    }


    private static String collectAssets(File gameDir, File dataDir) {
        StringWriter out = new StringWriter();
        PrintWriter a = new PrintWriter(out);

        processFile(a, "data", dataDir);
        processFile(a, "", gameDir);

        return out.toString();
    }

    private static void processFile(PrintWriter a, String prefix, File dir) {
        String p = "";
        if (!prefix.equals("")) {
            a.println("d:" + prefix + ":0:application/unknown");
            p = prefix + "/";
        }
        for (File f : dir.listFiles()) {
            if (!f.getName().startsWith(".")) {
                if (f.isDirectory()) {
                    processFile(a, p + f.getName(), f);
                } else {
                    String n = f.getName();
                    if (n.endsWith(".jpg") || n.endsWith(".jpeg")) {
                        a.println("i:" + p + n + ":" + f.length() + ":" + mimeType(n));
                    } else if (n.endsWith(".png")) {
                        a.println("i:" + p + n + ":" + f.length() + ":" + mimeType(n));
                    } else if (n.endsWith(".wav")) {
                        a.println("i:" + p + n + ":" + f.length() + ":" + mimeType(n));
                    } else if (n.endsWith(".mp3")) {
                        a.println("i:" + p + n + ":" + f.length() + ":" + mimeType(n));
                    } else if (n.endsWith(".txt") || n.endsWith(".py")) {
                        a.println("t:" + p + n + ":" + f.length() + ":" + mimeType(n));
                    } else {
                        a.println("b:" + p + n + ":" + f.length() + ":" + mimeType(n));
                    }
                }
            }
        }
    }

    private static String mimeType(String n) {
        if (n.endsWith(".jpg") || n.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (n.endsWith(".png")) {
            return "image/png";
        } else if (n.endsWith(".wav")) {
            return "audio/x-wav";
        } else if (n.endsWith(".mp3")) {
            return "audio/mpeg3";
        } else if (n.endsWith(".txt") || n.endsWith(".py")) {
            return "text/plain";
        } else if (n.endsWith(".html") || n.endsWith(".htm")) {
            return "text/html";
        } else {
            return "application/unknown";
        }
    }
}
