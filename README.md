[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a237011c288a4a4e9d7462094be6a082)](https://www.codacy.com/app/navicore/iot-server-demo?utm_source=github.com&utm_medium=referral&utm_content=navicore/iot-server-demo&utm_campaign=badger)
[![Build Status](https://travis-ci.org/navicore/iot-server-demo.svg?branch=master)](https://travis-ci.org/navicore/iot-server-demo)

IOT Server Demo
---

## A POC Experiment
### don't take this code too seriously
---

A system of Akka actors that maintain device, location, and fleet state.

A system of sliding windows maintains all the observations for a location by
observation name (ie: water_level or oil_temp, etc...).  The currently active
window is available as it is being populated - ie: you can see all prior
10-minute windows as if they were tumbled with no overlap, but the current
window is always the most recent 10 minutes (rounded to step time) -
overlapping the prior window by step-time until it is complete and the next one
is started.

All current assessments at the device level are available from the device
actor.

All current assessments at the location level are available from the location
actor for 24 hours.

The windowing code is based on Software Mill's excellent [post about Akka
Streams and windowing].

### To Run

* [run Kafka with Docker]
    docker run -d --name my-kafka -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=`ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'` --env ADVERTISED_PORT=9092 spotify/kafka
* [run Cassandra with Docker]
    docker run -p 9042:9042 --name my-cassandra -d cassandra:3.11
* or run redis with Docker:
    docker run -p 6379:6379 --name my-redis -d redis
    export REDIS_HOST=`ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'`
* `sbt run`
* see `examples/get.rest` for API usage examples
* see `application.conf` for config and ENV VAR details

### Features
  * Device twins and location twins can be inspected via an HTTP API
  * Device observations - telemetry - can be posted via HTTP
  * Observations are processed in Kafka with Akka Streams
    * End-to-end backpressure
    * Resume supervision strategy
    * Kafka offset persistence
  * Sliding windows with watermarking

### Entities
  * Observation - a named time-stamped measurement
  * Device - a source of observations
  * Location - a collection of devices
  * Fleet - a collection of locations and fleets
  * Assessment - a processed observation or collection of observations

### TODO
  * ~~Actor persistence and serialization~~
  * ~~Cluster deployment with sharding and discovery~~
  * Move Kafka offset persistence to after the windows processing
  * AUTH
  * HTTP2
  * Let observations reference devices by meaningful names (IOT devices won't like predefined UUIDs)
  * Parameterize the window size, step, and watermarking settings

[post about Akka Streams and windowing]: https://softwaremill.com/windowing-data-in-akka-streams/
[run Kafka with Docker]: https://gist.github.com/navicore/017c6ab1d735596cecc2732e2faaa0dd
[run Cassandra with Docker]: https://gist.github.com/navicore/6116395a56224d608b979053efae5981

