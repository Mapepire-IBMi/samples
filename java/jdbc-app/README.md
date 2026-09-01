# JDBC App

Simple demo application of using the [Mapepire JDBC driver](https://github.com/Mapepire-IBMi/mapepire-jdbc) to access Db2 for i through the standard `java.sql` API.

## Setup

1. Ensure the [Mapepire Server](https://mapepire-ibmi.github.io/guides/sysadmin) is installed and running on the IBM i.

2. Clone and install the [Mapepire JDBC driver](https://github.com/Mapepire-IBMi/mapepire-jdbc) into your local Maven repository. It is not yet published to Maven Central, so it must be built locally:

    ```sh
    git clone https://github.com/Mapepire-IBMi/mapepire-jdbc.git
    cd mapepire-jdbc
    mvn install -DskipTests
    ```

3. Clone this repository:

    ```sh
    git clone https://github.com/Mapepire-IBMi/samples.git
    cd java/jdbc-app
    ```

4. Copy and fill out the configuration properties:

    ```sh
    cp src/main/resources/config.properties.sample src/main/resources/config.properties
    ```

5. Build the application:

    ```sh
    mvn clean package
    ```

## Usage

1. Run the application:

    ```sh
    cd target
    java -jar jdbc-app-1.0-SNAPSHOT-jar-with-dependencies.jar <demo-type>
    ```

    Replace `<demo-type>` in the above command with one of the demo types below:

    | Demo Type              | Description                                                     |
    | ----------------------- | ---------------------------------------------------------------- |
    | `--sql`                 | Execute a SQL query with a `Statement`                           |
    | `--preparedStatement`   | Execute a parameterized SQL query with a `PreparedStatement`      |
    | `--clCommand`           | Execute a CL command via `CALL QSYS2.QCMDEXC(...)`                |

> [!NOTE]
> The Mapepire JDBC driver is still a work in progress. `CallableStatement`, `ResultSet.getMetaData()`, and commitment control (`setAutoCommit`/`commit`/`rollback` actually taking effect) are not yet fully supported, so this sample sticks to the parts of the JDBC API that work reliably today.
