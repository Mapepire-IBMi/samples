package io.github.company;

import java.util.concurrent.CompletableFuture;

// import io.github.mapapire.Pool;
import io.github.mapapire.SqlJob;
// import io.github.mapapire.PoolOptions;
import io.github.mapapire.types.DaemonServer;
import io.github.mapapire.types.QueryResult;

public class Database {
    // private static Pool pool;
    private static SqlJob job;

    public static void connect(String host, int port, String user, String password) throws Exception {
        DaemonServer creds = new DaemonServer(host, port, user, password, true, "");
        // pool = new Pool(new PoolOptions(creds, 5, 1));
        // pool.waitForJob().get();
        // pool.init().get();

        job = new SqlJob();
        job.connect(creds).get();
    }

    public static boolean isConnected() {
        return job != null;
    }

    public static <T> CompletableFuture<QueryResult<T>> query(String sql) throws Exception {
        // return pool.execute(sql);
        return job.execute(sql);
    }

    public static void disconnect() {
        // pool.end();
        job.close();
    }
}