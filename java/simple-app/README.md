# Simple App

Simple demo application of using the Mapepire Java client SDK

## Setup

1. Ensure the [Mapepire Server](https://mapepire-ibmi.github.io/guides/sysadmin) is installed and running on the IBM i.

2. Clone the repository:

    ```sh
    git clone https://github.com/Mapepire-IBMi/samples.git
    cd java/simple-app
    ```

3. Copy and fill out the configuration properties:

    ```sh
    cp src/main/resources/config.properties.sample src/main/resources/config.properties
    ```

4. Build the application

    ```sh
    mvn clean package
    ```

## Usage

1. Run the application:

    ```sh
    cd target
    java -jar simple-app-1.0-SNAPSHOT-jar-with-dependencies.jar <demo-type>
    ```

    Replace `<demo-type>` in the above command with one of the demo types below:

    | Demo Type             | Description                                          |
    | --------------------- | ---------------------------------------------------- |
    | `--sql`               | Execute a SQL query                                  |
    | `--prepareStatement`  | Execute a prepared SQL query with parameters         |
    | `--clCommand`         | Execute a CL command                                 |
    | `--paginatingResults` | Fetch a specific number of rows and paginate results |
    | `--pooling`           | Execute a SQL query from a job pool                  |
    | `--jdbcOptions`       | Execute a SQL query with a job having JDBC options   |
