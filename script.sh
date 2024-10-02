#!/bin/bash
compile(){
javac -d bin src/up/mi/minisgbd/DBConfig.java
echo "class files compiled"
}

run(){
java -cp ./bin DBConfig
echo "Launch complete"
}

compile
run
