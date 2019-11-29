#!/bin/bash

mkdir -p result
for mode in "waiter" "beb" "asym"; do
	for i in {3..15}; do  
		node phil.js $mode $i  | grep 'time' | awk '{print $2 " " $4}' | sort | tee result/$mode$i
	done
done

for i in {3..15}; do  
		node phil.js naive $i  | grep 'time' | awk '{print $2 " " $4}' | sort | tee result/naive$i &
		sleep 3
		killall node
		node phil.js naive $i  | grep 'time' | awk '{print $2 " " $4}' | sort | tee -a result/naive$i &
		sleep 3
		killall node
		node phil.js naive $i  | grep 'time' | awk '{print $2 " " $4}' | sort | tee -a result/naive$i &
		sleep 3
		killall node
		cat result/naive$i | sort | tee result/naive$i
done