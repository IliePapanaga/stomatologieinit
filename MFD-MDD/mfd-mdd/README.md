# How to Run the App with Docker

## Prerequisites

Ensure that you have the following installed on your machine:

1. **Node.js** (v8.17.0)
2. **npm** (v6.13.4)
3. **Docker** (Latest version)
4. **Maven** (To build the Java-based components)

## Steps to Run the App

### 1. **Set up the Frontend (Web UI)**

First, you need to install the necessary dependencies for the frontend (`web-ui`).

- Navigate to the `web-ui` directory:

- Install the required Node.js dependencies:

    Since the project uses **Node.js v8.17.0** and **npm v6.13.4**, ensure you're using the correct versions of Node.js and npm before running the command.


- Once dependencies are installed, build the Web UI:

    ```bash
    mvn clean install
    ```

### 2. **Set up the Backend (Server)**

Next, you need to build the backend server, which is based on **Spring Boot**.

- Navigate to the `server` directory:

- Run the following Maven command to build the server. This command will use a Docker-based MySQL profile:

    ```bash
    mvn clean install -Dspring.profiles.active=mysql-local-docker -DskipTests=true -U -Pskip-it-based-on-maven.test.skip,ui
    ```

    - `-Dspring.profiles.active=mysql-local-docker`: This activates the MySQL Docker profile.
    - `-DskipTests=true`: This skips the unit tests for a faster build.
    - `-U`: Forces Maven to update snapshots and releases.
    - `-Pskip-it-based-on-maven.test.skip`: This skips integration tests.


