#!/bin/bash
compile(){
javac -d bin src/*.java
echo "class files compiled"
}

run(){
java -cp ./bin Test
echo "Launch complete"
}

compile
run
