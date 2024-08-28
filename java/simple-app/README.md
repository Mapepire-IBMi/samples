# Simple App

Simple demo application of using the Mapepire Java client SDK

## Setup

1. Ensure the [Mapepire Server](https://mapepire-ibmi.github.io/guides/sysadmin) is installed and running on the IBM i.

2. Clone the repository and build the application:

    ```sh
    git clone https://github.com/Mapepire-IBMi/samples.git
    cd java/simple-app
    mvn clean package
    ```

## Usage

1. Run the application:

    ```sh
    cd target
    java -jar simple-app-1.0-SNAPSHOT.jar <demo-type>
    ```

> [!NOTE]
> Replace `<demo-type>` in the above command with one of the demo types listed at the bottom of the page

| Demo Type             | Description                                               |
| --------------------- | --------------------------------------------------------- |
| `--sql`               | Execute a simple SQL query                                |
| `--prepareStatement`  | Prepare SQL statement with parameters and execute         |
| `--clCommand`         | Execute a simple CL command                               |
| `--paginatingResults` | Fetch specific number of rows and paginate results        |
| `--pooling`           | Execute a simple SQL query from a job pool                |
| `--jdbcOptions`       | Execute a simple SQL query with a job having JDBC options |