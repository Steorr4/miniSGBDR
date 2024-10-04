#!/bin/bash
compile(){
javac -d bin src/Test.java src/DBConfig.java src/DiskManager.java src/PageId.java 
echo "class files compiled"
}

run(){
java -cp ./bin Test
echo "Launch complete"
}

compile
run
