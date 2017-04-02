sbt package
# Winter Storm
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 1 winterstorm 1 > output/winterstorm1_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 1 winterstorm 2 > output/winterstorm2_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 1 winterstorm 3 > output/winterstorm3_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 2 winterstorm 1 > output/winterstorm1_google_CNN_bigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 2 winterstorm 2 > output/winterstorm2_google_CNN_bigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 2 winterstorm 3 > output/winterstorm3_google_CNN_bigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 3 winterstorm 1 > output/winterstorm1_google_CNN_trigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 3 winterstorm 2 > output/winterstorm2_google_CNN_trigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.4 0.2 3 winterstorm 3 > output/winterstorm3_google_CNN_trigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.4 0.2 1 winterstorm 1 > output/winterstorm1_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.4 0.2 1 winterstorm 2 > output/winterstorm2_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.4 0.2 1 winterstorm 3 > output/winterstorm3_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.05 0.04 1 winterstorm 1 > output/winterstorm1_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.05 0.04 1 winterstorm 2 > output/winterstorm2_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.05 0.04 1 winterstorm 3 > output/winterstorm3_local_LR.log

