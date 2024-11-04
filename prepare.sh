#!/bin/bash

# Define the JDK version and URL
JDK_VERSION="21"
JDK_ARCHIVE="amazon-corretto-${JDK_VERSION}-x64-linux-jdk.tar.gz"
JDK_URL="https://corretto.aws/downloads/latest/${JDK_ARCHIVE}"

# Define the expected directory name after extraction
JDK_DIR="jdk-${JDK_VERSION}"

# Check if the JDK directory already exists
if [ ! -d "$JDK_DIR" ]; then
    echo "OpenJDK ${JDK_VERSION} not found in the current directory."

    # Check if the archive is already downloaded
    if [ ! -f "$JDK_ARCHIVE" ]; then
        echo "Downloading OpenJDK ${JDK_VERSION}..."
        curl -LO --retry 3 "$JDK_URL" || { echo "Download failed"; rm -f "$JDK_ARCHIVE"; exit 1; }
    fi

    # Verify the archive format before extracting
    if ! tar -tzf "$JDK_ARCHIVE" > /dev/null 2>&1; then
        echo "Downloaded file is not a valid tar.gz archive."
        # rm -f "$JDK_ARCHIVE"
        exit 1
    fi

    # Extract the JDK
    echo "Extracting OpenJDK ${JDK_VERSION}..."
    mkdir -p "$JDK_DIR"
    tar -xzf "$JDK_ARCHIVE" -C "$JDK_DIR" --strip-components=1 || { echo "Extraction failed"; rm -f "$JDK_ARCHIVE"; exit 1; }
    echo "OpenJDK ${JDK_VERSION} extracted successfully."
    rm -f "$JDK_ARCHIVE"
    echo "Deleted the archive file."
else
    echo "OpenJDK ${JDK_VERSION} is already present."
fi

# Set JAVA_HOME to the current path of the extracted JDK
export JAVA_HOME="$(pwd)/$JDK_DIR"
echo "JAVA_HOME is set to $JAVA_HOME"

# Optionally add JAVA_HOME/bin to PATH
export PATH="$JAVA_HOME/bin:$PATH"
echo "Updated PATH with JAVA_HOME/bin"

# Verify installation
java -version || { echo "Failed to verify Java installation"; exit 1; }

# Set project files permission
chmod -R 755 . || { echo "Failed to update project files permission"; }
stat -c "%A %a %n" *
echo "Updated project files permission."

# Compile with gradle wrapper if gradle is present
if [ -f "gradlew" ]; then
    echo "Gradle wrapper found."
    ./gradlew --version
    echo "Compiling with Gradle wrapper..."
    ./gradlew clean build --warning-mode none || { echo "Gradle build failed"; exit 1; }
else
    echo "Gradle wrapper not found."
fi

# Generate log files
echo "Generating log files..."
mkdir -p data
echo "Created data directory."
echo "Generating log files..."
java -cp build/libs/logs-statistics-1.0-SNAPSHOT.jar app.LogGenerator

echo "Project is ready to run."

echo "Running the tests..."
./gradlew test --warning-mode none || { echo "Tests failed"; exit 1; } else { echo "Ready to run!"; }

java -jar build/libs/logs-statistics-1.0-SNAPSHOT.jar --help

# Run the application
#echo "Running the application..."
#jar tf build/libs/logs-statistics-1.0-SNAPSHOT.jar
#java -cp build/libs/logs-statistics-1.0-SNAPSHOT.jar app.Main
#java -jar build/libs/logs-statistics-1.0-SNAPSHOT.jar --folder=./data
