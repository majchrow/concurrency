#!/bin/bash

echo 'Bufsize,Prod_or_kons,Size,PK_config,Is_fair,Randomization,Time' > ./result.csv
javac ./Main.java

for buffsize in 1000 10000; do
	for prod_or_kons in "Prod" "Cons"; do
		for pk_config in 100 1000; do
			for is_fair in "true" "false"; do
				for randomization in "Equal", "Unequal"; do
					java -ea ./Main buffsize prod_or_kons pk_config is_fair randomization | tee -a ./result.csv
    			done
			done
		done
    done
done

rm *.class Buff
find . -name \*.class -type f -delete