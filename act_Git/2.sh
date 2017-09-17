#!/bin/bash
cd downloads
typeset -i sum
sum=0
for ((i = 1; i <= 34; i++))
do
	printf "%d: " $i
	cd $i
	let "zy = `ls -l | wc -l`"
	printf "  %s\n"  $zy
	let "sum = sum + $zy"
	cd ..
	done
echo $sum
