#!/bin/bash
compile(){
javac -cp "lib/*" -d bin src/fr/upc/mi/bdda/*/*.java src/Main.java
echo "class files compiled"
}

run(){
java -cp "bin:lib/*" Main
echo "Launch complete"
}

compile
run
