package io.github.simple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.mapepire_ibmi.Pool;
import io.github.mapepire_ibmi.Query;
import io.github.mapepire_ibmi.SqlJob;
import io.github.mapepire_ibmi.types.DaemonServer;
import io.github.mapepire_ibmi.types.JDBCOptions;
import io.github.mapepire_ibmi.types.PoolOptions;
import io.github.mapepire_ibmi.types.QueryOptions;
import io.github.mapepire_ibmi.types.QueryResult;
import io.github.mapepire_ibmi.types.jdbcOptions.Naming;

public final class App {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("No argument provided");
            return;
        }

        QueryResult<Object> result;
        switch (args[0]) {
            case "--sql":
                result = runSqlDemo();
                outputResults(result);
                break;
            case "--prepareStatement":
                result = prepareStatementDemo();
                outputResults(result);
                break;
            case "--clCommand":
                result = clCommandDemo();
                outputResults(result);
                break;
            case "--paginatingResults":
                result = paginatingResultsDemo();
                outputResults(result);
                break;
            case "--pooling":
                result = poolingDemo();
                outputResults(result);
                break;
            case "--jdbcOptions":
                result = jdbcOptionsDemo();
                outputResults(result);
                break;
            default:
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
        Query query = job.query("SELECT * FROM SAMPLE.DEPARTMENT");
        QueryResult<Object> result = query.execute().get();

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
        QueryOptions options = new QueryOptions(false, false, Arrays.asList("A00"));
        Query query = job.query("SELECT * FROM SAMPLE.DEPARTMENT WHERE ADMRDEPT = ?", options);
        QueryResult<Object> result = query.execute().get();

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
        Query query = job.clCommand("CRTLIB LIB(MYLIB1) TEXT('My cool library')");
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
        Query query = job.query("SELECT * FROM SAMPLE.EMPLOYEE");
        QueryResult<Object> result = query.execute(10).get();

        // Continuously fetch 10 more rows until all all rows have been returned
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
        Query query = pool.query("SELECT * FROM SAMPLE.DEPARTMENT");
        QueryResult<Object> result = query.execute().get();

        // Close query and pool
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
        Query query = job.query("SELECT * FROM SALES");
        QueryResult<Object> result = query.execute().get();

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
