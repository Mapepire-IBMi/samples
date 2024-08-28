# Company Web Server

Jetty company web server to manage departments, employees, and sales

## Setup

1. Ensure the [Mapepire Server](https://mapepire-ibmi.github.io/guides/sysadmin) is installed and running on the IBM i.

2. Create the `SAMPLE` schema on the IBM i:

    ```sql
    QSYS.CREATE_SQL_SAMPLE('sample')
    ```

3. Clone the repository and build the application:

    ```sh
    git clone https://github.com/Mapepire-IBMi/samples.git
    cd java/company-web-server
    mvn clean package
    ```

4. Start the company web server:

    ```sh
    cd target
    java -jar company-web-server-1.0-SNAPSHOT.jar
    ```

5. Start a web server to launch the Swagger UI:

    ```sh
    npm install -g http-server
    cd swagger
    http-server --cors
    ```

6. Access the Swagger UI at http://localhost:8080 in the browser.

## Usage

1. Connect to a database using the `/connect` endpoint.

2. Toggle database channel data tracing using the `tracing` endpoint.

3. Experiment with the various endpoints:

    | Method | Endpoint                | Description                                               |
    | ------ | ----------------------- | --------------------------------------------------------- |
    | `GET`  | `/departments`          | Get a list of departments                                 |
    | `GET`  | `/departments/{deptNo}` | Get a department by department number                     |
    | `GET`  | `/employees`            | Get a list of employees                                   |
    | `GET`  | `/employees/{empNo}`    | Get an employee by employee number                        |
    | `GET`  | `/sales`                | Get a list of sales                                       |
    | `GET`  | `/sales/{salesPerson}`  | Get a list of sales associated with a sales person        |

4. Disconnect from the database using the `/disconnect` endpoint.
