#!/bin/bash
compile(){
javac -cp "lib/*" -d bin src/fr/upc/mi/bdda/*/*.java
echo "class files compiled"
}

run(){
java -cp ./bin Test
echo "Launch complete"
}

compile
run
