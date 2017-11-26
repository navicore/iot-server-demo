Akka HTTP Kafka Demo
---

## A POC Experiment
### don't take this code too seriously

A system of Akka actors that maintain device, location, and fleet state.

A system of sliding windows maintains all the observations for a location by
observation name (ie: water_level or oil_temp, etc...).  The currently active
window is available as it is being populated - ie: you can see all prior 10-minute
windows as if they were tumbled with no overlap, but the current window is always
the most recent 10 minutes - overlapping the prior window until is is complete
and the next one is started.

The windowing code is based on Software Mill's great [post about Akka Streams and windowing].

### Features
  * Device twins and location twins can be inspected via an HTTP API
  * Device observations - telemetry - can be posted via HTTP
  * Observations are processed in Kafka with Akka Streams
    * End-to-end backpressure
    * Resume supervision strategy
    * Kafka offset persistence
  * Sliding windows with watermarking

### Entities
  * Observation - a named measure of something
  * Device - a source of observations
  * Location - a collection of devices
  * Fleet - a collection of locations (tbd)

### TODO
  * Actor Persistence
  * Cluster deployment with sharding and discovery
  * Move Kafka offset persistence to after the windows processing
  * AUTH
  * HTTP2

[post about Akka Streams and windowing]: https://softwaremill.com/windowing-data-in-akka-streams/

