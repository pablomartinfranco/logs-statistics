# logs-statistics
<!-- ![Gophers](./assets/images/gophers.png "Gophers") -->
**Java Command-Line Tool for Log File Aggregation**<br>
logs-statistics is a CLI sample program for aggregating log files.

## Setup and Prerequisites

The project only needs Java 21 to build and run.

The Linux bash script **prepare.sh** will download the open-jdk-21 runtime if not present, into the project root, set JAVA_HOME environment variable temporally, and run build tasks from the Gradle wrapper.

From the project root run the following command:

```bash
$ source prepare.sh
```

Now the application can run locally from the Gradle wrapper tasks or from the Java command line.

### Java command line

After successful build, run the application with the following command:

Process logs in ./data folder with single thread runner.

```bash
$ java -jar build/libs/logs-statistics.jar --folder=./data
```

Invokes the help menu.

```bash
$ java -jar build/libs/logs-statistics.jar --help
```

Process logs in ./data folder with fibers runner.

```bash
$ java -jar build/libs/logs-statistics.jar --folder=./data --runner=fibers
```

Process logs in ./data folder with parallel runner.

```bash
$ java -jar build/libs/logs-statistics.jar --folder=./data --runner=parallel
```

Generates log files in ./data folder at project root.

```bash
$ java -cp build/libs/logs-statistics.jar app.LogGenerator 20 1000
```

### Gradle wrapper

From project root:

Clean, build and run tests.

```bash
$ ./gradlew clean build
```

Generate log files in ./data folder at project root.
Data folder needs to be empty before running the generator task.

```bash
$ ./gradlew generator -Pparam1=20 -Pparam2=1000

```

With no parameters will default to 50 files with 1000 lines each.

```bash
$ ./gradlew generator
```

Run locally.

```bash
$ ./gradlew run -Pfolder=./data
```

See `build.gradle` for further commands.

## Why Java

If this would have been an automating script for some sys-admin task, I would have probably chosen Python or Bash, but since it is a command line tool for log file aggregation, I chose Java as a study case for concurrency and as an opportunity to test the new built in Java virtual threads that looks promising.

It may seem overkill to use Java for a simple task like this, but the performance and portability of the JVM, with the benefits of type safety, the garbage collector and static analysis, makes it a solid choice for quality grade software.

This solution could be easily containerized and deployed as a microservice, making the JVM a strong choice for the task.

The main challenges I see in deploying Java applications are the runtime size, memory footprint, and the larger binary sizes compared to those compiled in Go.

Recently, many Java developers have been transitioning to .NET Core C# for its modern features and cleaner syntax. However, I believe the JVM is evolving to stay competitive..

## Considerations

The intention of this project was mostly to test different aproaches to concurrency in Java 21, using the new virtual threads API (user light threads), old kernel threads and the traditional single thread executor.

At the end there wasn't that big of a difference until an amount of files in the magnitud of 1000.

The single thread aproach showed to be easier to implement and debug, but the fibers and parallel aproaches showed to be more efficient in terms of time and resource consumption.

What I think makes mostly the diference is the non blocking I/O API of the built in nio package, that allows to read files in a non blocking way.