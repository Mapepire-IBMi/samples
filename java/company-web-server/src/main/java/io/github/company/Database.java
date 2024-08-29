package io.github.company;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.mapepire_ibmi.Pool;
import io.github.mapepire_ibmi.types.DaemonServer;
import io.github.mapepire_ibmi.types.PoolOptions;
import io.github.mapepire_ibmi.types.QueryOptions;
import io.github.mapepire_ibmi.types.QueryResult;
import io.github.mapepire_ibmi.types.ServerTraceLevel;

public class Database {
    private static Pool pool;

    public static void connect(String host, int port, String user, String password) throws Exception {
        DaemonServer creds = new DaemonServer(host, port, user, password, true, "");
        pool = new Pool(new PoolOptions(creds, 5, 1));
        pool.waitForJob().get();
        pool.init().get();
    }

    public static boolean getReadyJob() {
        return pool.getReadyJob() != null;
    }

    public static <T> CompletableFuture<QueryResult<T>> execute(String sql) throws Exception {
        return pool.execute(sql);
    }

    public static <T> CompletableFuture<QueryResult<T>> prepareAndExecute(String sql, List<Object> parameters)
            throws Exception {
        return pool.execute(sql, new QueryOptions(false, false, parameters));
    }

    public static void setTraceLevel(ServerTraceLevel level) throws Exception {
        pool.getReadyJob().setTraceLevel(level).get();
    }

    public static void disconnect() {
        pool.end();
    }
}
