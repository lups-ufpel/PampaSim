#!/bin/sh
set -euC
# use our detailed JVM Flight Recorder profile
# with extended stack depth to capture data we
# can later read on the Java Mission Control
# flamegraph --- that's how we do profiling
java -XX:StartFlightRecording:filename=recording.jfr,settings=detailed.jfc \
     -XX:FlightRecorderOptions=stackdepth=256 \
     -jar sim/target/sim-1.0.jar
