sbt package
# ebola
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.20 1 ebola 1 > output/ebola1_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.20 1 ebola 2 > output/ebola2_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.20 1 ebola 3 > output/ebola3_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.55 0.20 1 ebola 1 > output/ebola1_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.55 0.20 1 ebola 2 > output/ebola2_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.55 0.20 1 ebola 3 > output/ebola3_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.45 0.30 1 ebola 1 > output/ebola1_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.45 0.20 1 ebola 2 > output/ebola2_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.45 0.30 1 ebola 3 > output/ebola3_local_LR.log