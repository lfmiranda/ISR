#!/bin/bash

for i in */out_files/*/*Fitness.csv; do
  echo $i

  sort -n --field-separator=',' --key=1 $i > /tmp/aux_luis; cat /tmp/aux_luis > $i;
  cut -d ',' -f 2- $i > /tmp/aux_luis; cat /tmp/aux_luis > $i;
done
