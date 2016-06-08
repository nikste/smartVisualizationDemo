# Flink
## custom Flink installation
- ~>git clone https://github.com/nikste/flink 
- ~>git checkout Flink-2522_Scala_shell_streaming_download_linux
- build as usual:
 - ~>mvn clean install -DskipTests

# Zeppelin
## custom Zeppelin installation
- ~>git clone https://github.com/nikste/incubator-zeppelin
- ~>git checkout visualizationDemo
- you need maven-3.x for installation
- ~>mvn clean install -DskipTests
- if it gets stuck with zeppelin-web
- comment <plugin><groupId>com.github.eirslett</groupId>..</plugin> (frontent-maven-plugin) to disable npm, bower, grunt in zeppelin-web/pom.xml
- manually execute:
- {zeppelin-root}/zeppelin-web~>./node/npm install
- {zeppelin-root}/zeppelin-web~>./bower install
- {zeppelin-root}/zeppelin-web~>./grunt build
- continue with
- ~>mvn install -DskipTests 
- or ~>mvn install -DskipTests -rf :zeppelin-web

## get notebooks and twitter data
- ~> git clone https://github.com/nikste/zeppelin-notebooks
- ~> git checkout visualizationDemo
- copy contents of notebook folder to {zeppelin-root}/notebooks/
- this also contains a sample of the twitter data (1000 tweets as json in data folder)

# RabbitMQ
- install rabbitmq message protocol
##install web socket for javascript rabbitmq connection:
- https://www.rabbitmq.com/web-stomp.html 

##for web interface
- ~>sudo rabbitmq-plugins enable rabbitmq_management
- access webfrontend at: http://localhost:15672 (user:guest,pw:guest)


# Data generator:
- ~>git clone https://github.com/nikste/smartVisualizationDemo
- ~>change pathname to twitter data file in : demo-data-generator/src/main/java/org/demo/connections/TwitterStreamFromFile.java


# Usage:
- Go to rabbitMQ web interface
- manually create queue: "mikeQueue", select transient (queue for js messages)
- start data generator (TwitterStreamFromFile.java)
- start zeppelin-daemon:
- {zeppelin-root}/bin/~>zeppelin-daemon.sh start
- select notebook compile and run


# Next steps (proposition)
## contributions to open source projects
- update to flink code to stable version (e.g. 1.0) (change code in streaming-contrib, unblocking buffer ? )
- update Zeppelin stuff to work with streaming (rewrite interpreter class to work with streamin scala shell in stable flink version)
- extend zeppelin interpreter to communicate kill message to Flink-job-manager (these three steps might be enough for the deliverable for streamline)

## demo paper
- find better way to send messages from map
- find a nicer usecase
- find statistics or examples how this will help and how much it will help.






# Old readme (for spring dashboard stuff, please ignore)
## smartVisualizationDemo

prerequisites:
rabbit-mq, apache-flink, spring

consists of three parts:

## data loading module
sends data messages (tweets from file) over rabbit-mq messaging queue

## flink module
receives control messages from rabbit-mq and data stream from rabbit-mq

## visualization module
frontend which sends control messages to backend (flink module)
