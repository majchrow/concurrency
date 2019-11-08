#!/bin/bash

echo 'Bufsize,Prod_or_kons,Size,PK_config,Is_fair,Randomization,Time' | tee result/result.csv
javac Main.java

for randomization in "Unequal" "Equal"; do
  for buffsize in 1000 10000; do
    for pk_config in 100 1000; do
      for is_fair in "true" "false"; do
				java -ea Main $buffsize $pk_config $is_fair $randomization | tee -a result/result.csv # run with assertions and append to csv the output
			done
		done
	done    
done

find . -name \*.class -type f -delete