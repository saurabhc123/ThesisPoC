sbt package
# Environment
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.20 1 environment > output/environment_google_CNN.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.55 0.20 1 environment > output/environment_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.43 0.30 1 environment > output/environment_local_LR.log
