#sbt package
# Greece
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.53 0.10 1 greece > output/greece_google_CNN.log
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.53 0.10 1 greece > output/greece_google_LR.log
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.50 0.20 1 greece > output/greece_local_LR.log
