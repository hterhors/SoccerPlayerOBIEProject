#!/bin/bash
for i in {1..10}
do
echo "random $i/10"
java -XX:+UseG1GC -XX:+UseStringDeduplication -Xmx64g -jar OBIESoccerPlayerActiveLearning.jar randomResults random > randomLog$i


done

for i in {1..10}
do
echo "entropy $i/10"
java -XX:+UseG1GC -XX:+UseStringDeduplication -Xmx64g -jar OBIESoccerPlayerActiveLearning.jar entropyResults entropy > randomLog$i


done


for i in {1..10}
do
echo "objective $i/10"
java -XX:+UseG1GC -XX:+UseStringDeduplication -Xmx64g -jar OBIESoccerPlayerActiveLearning.jar objectiveResults objective > randomLog$i


done
