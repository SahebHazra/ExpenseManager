package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {
    private static BlockingQueue<Connection> pool;
    private static String dbUrl;
    private static String user;
    private static String password;
    private static int poolSize = 5;

    public static synchronized void init(String url, String user_, String password_, int size) {
        if (pool != null)
            return;
        dbUrl = url;
        user = user_;
        password = password_;
        poolSize = size > 0 ? size : poolSize;
        pool = new LinkedBlockingQueue<>(poolSize);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // driver may not be present at compile time
        }
        for (int i = 0; i < poolSize; i++) {
            try {
                Connection c = DriverManager.getConnection(dbUrl, user, password);
                pool.offer(c);
            } catch (Exception e) {
                // ignore connection creation errors; pool may be partially filled
            }
        }
    }

    public static Connection getConnection() throws Exception {
        if (pool == null)
            throw new IllegalStateException("ConnectionPool not initialized");
        Connection c = pool.poll();
        if (c == null) {
            // try to create a new connection
            return DriverManager.getConnection(dbUrl, user, password);
        }
        return c;
    }

    public static void releaseConnection(Connection c) {
        if (c == null)
            return;
        if (pool == null)
            try {
                c.close();
            } catch (Exception e) {
            }
        else {
            try {
                if (!c.isClosed()) {
                    pool.offer(c);
                } else {
                    // replace closed connection
                    try {
                        Connection nc = DriverManager.getConnection(dbUrl, user, password);
                        pool.offer(nc);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
                try {
                    c.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    public static synchronized void shutdown() {
        if (pool == null)
            return;
        for (Connection c : pool) {
            try {
                c.close();
            } catch (Exception e) {
            }
        }
        pool.clear();
        pool = null;
    }
}
