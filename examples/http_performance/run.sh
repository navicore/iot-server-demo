#!/usr/bin/env bash

SERVER="http://localhost:8080"

ab -v 0 -g graphs/plot.tsv -t 60 -c 10 -p observation.json -T "Application/json" "${SERVER}/iot/observation"

