package io.github.simple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.mapapire.Pool;
import io.github.mapapire.Query;
import io.github.mapapire.SqlJob;
import io.github.mapapire.types.DaemonServer;
import io.github.mapapire.types.JDBCOptions;
import io.github.mapapire.types.QueryOptions;
import io.github.mapapire.types.QueryResult;
import io.github.mapapire.types.jdbcOptions.Naming;

public final class App {
    public static void main(String[] args) throws Exception {
        if (args[0] == "--sql") {
            QueryResult<Object> result = runSqlDemo();
            outputResults(result);
        } else if (args[0] == "--prepareStatement") {
            QueryResult<Object> result = prepareStatementDemo();
            outputResults(result);
        } else if (args[0] == "--clCommand") {
            QueryResult<Object> result = clCommandDemo();
            outputResults(result);
        } else if (args[0] == "--paginatingResults") {
            QueryResult<Object> result = paginatingResultsDemo();
            outputResults(result);
        } else if (args[0] == "--pooling") {
            QueryResult<Object> result = poolingDemo();
            outputResults(result);
        } else if (args[0] == "--jdbcOptions") {
            QueryResult<Object> result = jdbcOptionsDemo();
            outputResults(result);
        } else {
            System.out.println("Invalid argument");
        }
    }

    private static DaemonServer getDaemonServer() throws IOException {
        // Load config properties
        Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Unable to find config.properties");
            }
            properties.load(input);
        }

        // Retrieve credentials
        String host = properties.getProperty("IBMI_HOST");
        String user = properties.getProperty("IBMI_USER");
        String password = properties.getProperty("IBMI_PASSWORD");
        int port = Integer.parseInt(properties.getProperty("IBMI_PORT"));

        return new DaemonServer(host, port, user, password, true, "");
    }

    private static QueryResult<Object> runSqlDemo() throws Exception {
        // Create a single job and connect
        DaemonServer creds = getDaemonServer();
        SqlJob job = new SqlJob();
        job.connect(creds).get();

        // Initialize and execute query
        Query<Object> query = job.query("SELECT * FROM SAMPLE.DEPARTMENT");
        QueryResult<Object> result = query.execute(3).get();

        // Close query and job
        query.close().get();
        job.close();

        return result;
    }

    private static QueryResult<Object> prepareStatementDemo() throws Exception {
        // Create a single job and connect
        DaemonServer creds = getDaemonServer();
        SqlJob job = new SqlJob();
        job.connect(creds).get();

        // Initialize and execute query
        QueryOptions options = new QueryOptions(false, false,
                Arrays.asList("TABLE_NAME", "LONG_COMMENT", "CONSTRAINT_NAME"));
        Query<Object> query = job.query("SELECT * FROM SAMPLE.SYSCOLUMNS WHERE COLUMN_NAME IN (?, ?, ?)", options);
        QueryResult<Object> result = query.execute(30).get();

        // Close query and job
        query.close().get();
        job.close();

        return result;
    }

    private static QueryResult<Object> clCommandDemo() throws Exception {
        // Create a single job and connect
        DaemonServer creds = getDaemonServer();
        SqlJob job = new SqlJob();
        job.connect(creds).get();

        // Initialize and execute query
        Query<Object> query = job.clCommand("CRTLIB LIB(MYLIB1) TEXT('My cool library')");
        QueryResult<Object> result = query.execute().get();

        // Close query and job
        query.close().get();
        job.close();

        return result;
    }

    private static QueryResult<Object> paginatingResultsDemo() throws Exception {
        // Create a single job and connect
        DaemonServer creds = getDaemonServer();
        SqlJob job = new SqlJob();
        job.connect(creds).get();

        // Execute query and fetch 10 rows
        Query<Object> query = job.query("SELECT * FROM SAMPLE.DEPARTMENT");
        QueryResult<Object> result = query.execute(10).get();

        // Continuously fetch 50 more rows until all all rows have been returned
        while (!result.getIsDone()) {
            result = query.fetchMore(50).get();
        }

        // Close query and job
        query.close().get();
        job.close();

        return result;
    }

    private static QueryResult<Object> poolingDemo() throws Exception {
        // Create a pool with a max size of 5 and starting size of 3
        DaemonServer creds = getDaemonServer();
        PoolOptions poolOptions = new PoolOptions(creds, 5, 3);
        Pool pool = new Pool(poolOptions);
        pool.init().get();

        // Initialize and execute query
        QueryOptions options = new QueryOptions(false, false,
                Arrays.asList("TABLE_NAME", "LONG_COMMENT", "CONSTRAINT_NAME"));
        Query<Object> query = pool.query("SELECT * FROM SAMPLE.SYSCOLUMNS WHERE COLUMN_NAME IN (?, ?, ?)", options);
        QueryResult<Object> result = query.execute(30).get();

        // Close query and job
        query.close().get();
        pool.end();

        return result;
    }

    private static QueryResult<Object> jdbcOptionsDemo() throws Exception {
        // Set JDBC options
        JDBCOptions options = new JDBCOptions();
        options.setNaming(Naming.SQL);
        options.setLibraries(Arrays.asList("SAMPLE"));

        // Create a single job and connect
        DaemonServer creds = getDaemonServer();
        SqlJob job = new SqlJob(options);
        job.connect(creds).get();

        // Initialize and execute query
        Query<Object> query = job.query("SELECT * FROM DEPARTMENT");
        QueryResult<Object> result = query.execute(3).get();

        // Close query and job
        query.close().get();
        job.close();

        return result;
    }

    private static void outputResults(QueryResult<Object> result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String jsonString = mapper.writeValueAsString(result);
        System.out.println(jsonString);
    }
}
