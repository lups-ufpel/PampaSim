#!/usr/bin/env bash
set -e

JAVA_MIN="21"
MAVEN_MIN="3.9.9"

check_java() {
    if ! command -v java >/dev/null 2>&1; then
        echo "Error: Java not found. Please install Java ${JAVA_MIN}+."
        exit 1
    fi

    JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_MAJOR=$(echo "$JAVA_VER" | cut -d. -f1)

    if [ "$JAVA_MAJOR" -lt "$JAVA_MIN" ]; then
        echo "Java $JAVA_VER found, but Java ${JAVA_MIN}+ is required."
        exit 1
    fi
    echo "Java $JAVA_VER OK"
}

check_maven() {
    if ! command -v mvn >/dev/null 2>&1; then
        echo "Error: Maven not found. Please install Maven ${MAVEN_MIN}+."
        exit 1
    fi

    MAVEN_VER=$(mvn -v | awk '/Apache Maven/ {print $3}')
    if [ "$(printf '%s\n' "$MAVEN_MIN" "$MAVEN_VER" | sort -V | head -n1)" != "$MAVEN_MIN" ]; then
        echo "Maven $MAVEN_VER found, but Maven ${MAVEN_MIN}+ is required."
        exit 1
    fi
    echo "Maven $MAVEN_VER OK"
}

build() {
    check_java
    check_maven
    mvn clean install
}

run() {
    (cd sim && mvn javafx:run)
}

# --- Main dispatcher ---
case "$1" in
    build)
        build
        ;;
    run)
        run
        ;;
    "" )
        build
        run
        ;;
    *)
        echo "Usage: $0 {build|run}"
        echo "If no argument is provided, both build and run are executed."
        exit 1
        ;;
esac

