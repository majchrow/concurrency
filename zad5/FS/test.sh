#!/bin/bash

mkdir -p result

for i in {1..100}; do 
	node async.js | tail -1 | tee -a result/async.txt # run with assertions, append to csv last line of the output and do not draw the result 
done

awk '{ total += $3 } END { print total/NR }' result/async.txt | tee result/async_mean.txt 

for i in {1..100}; do 
	node sync.js | tail -1 | tee -a result/sync.txt # run with assertions, append to csv last line of the output and do not draw the result 
done

awk '{ total += $3 } END { print total/NR }' result/sync.txt | tee result/sync_mean.txt  

rm result/sync.txt result/async.txt