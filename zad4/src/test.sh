#!/bin/bash

mkdir -p result
echo 'ThreadNumber,Time' | tee result/result.csv
javac Mandelbrot.java

for i in {1..200}; do 
	java -ea Mandelbrot $i false | tail -1 | tee -a result/result.csv # run with assertions, append to csv last line of the output and do not draw the result 
done

find . -name \*.class -type f -delete