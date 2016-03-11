# smartVisualizationDemo

prerequisites:
rabbit-mq, apache-flink, spring

consists of three parts:

## data loading module
sends data messages (tweets from file) over rabbit-mq messaging queue

## flink module
receives control messages from rabbit-mq and data stream from rabbit-mq

## visualization module
frontend which sends control messages to backend (flink module)
