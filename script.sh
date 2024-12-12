#!/bin/bash

compile() {
    javac -cp "lib/*" -d bin src/fr/upc/mi/bdda/*/*.java src/SGBD.java
    if [ $? -ne 0 ]; then
        echo "Compilation failed"
        exit 1
    fi
    echo "Compilation successful"
}

run() {
    java -cp "bin:lib/*" SGBD "src/config.json"
    if [ $? -ne 0 ]; then
        echo "Execution failed"
        exit 1
    fi
    echo "Execution successful"
}

compile
run