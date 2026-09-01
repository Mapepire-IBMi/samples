package io.github.jdbc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import io.github.mapepire_ibmi.MapepireDriver;

public final class App {
    // Column names selected by each demo query, used to print results since
    // ResultSet.getMetaData() is not yet implemented by the driver.
    private static final String[] TABLE_COLUMNS = { "TABLE_SCHEMA", "TABLE_NAME" };

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("No argument provided");
            return;
        }

        // Register the Mapepire JDBC driver
        DriverManager.registerDriver(new MapepireDriver());

        switch (args[0]) {
            case "--sql":
                sqlDemo();
                break;
            case "--preparedStatement":
                preparedStatementDemo();
                break;
            case "--clCommand":
                clCommandDemo();
                break;
            default:
                System.out.println("Invalid argument");
        }
    }

    private static Connection getConnection() throws IOException, SQLException {
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
        String port = properties.getProperty("IBMI_PORT");
        String user = properties.getProperty("IBMI_USER");
        String password = properties.getProperty("IBMI_PASSWORD");

        // Build the connection URL and connect
        String url = String.format("jdbc:mapepire://%s:%s", host, port);
        Properties connectionProperties = new Properties();
        connectionProperties.put("USER", user);
        connectionProperties.put("PASSWORD", password);

        return DriverManager.getConnection(url, connectionProperties);
    }

    private static void sqlDemo() throws Exception {
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement
                        .executeQuery("SELECT TABLE_SCHEMA, TABLE_NAME FROM QSYS2.SYSTABLES "
                                + "FETCH FIRST 5 ROWS ONLY")) {
            printResultSet(resultSet, TABLE_COLUMNS);
        }
    }

    private static void preparedStatementDemo() throws Exception {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT TABLE_SCHEMA, TABLE_NAME FROM QSYS2.SYSTABLES "
                                + "WHERE TABLE_SCHEMA = ? FETCH FIRST 5 ROWS ONLY")) {
            statement.setString(1, "QSYS2");
            try (ResultSet resultSet = statement.executeQuery()) {
                printResultSet(resultSet, TABLE_COLUMNS);
            }
        }
    }

    private static void clCommandDemo() throws Exception {
        // CallableStatement is not yet implemented by the driver, so CL commands are
        // run as a literal CALL statement through a plain Statement instead.
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("CALL QSYS2.QCMDEXC('CHGCURLIB CURLIB(*CRTDFT)')");
            System.out.println("CL command executed successfully");
        }
    }

    private static void printResultSet(ResultSet resultSet, String[] columns) throws SQLException {
        while (resultSet.next()) {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    row.append(", ");
                }
                row.append(columns[i]).append("=").append(resultSet.getString(columns[i]));
            }
            System.out.println(row);
        }
    }
}
