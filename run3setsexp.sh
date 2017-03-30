sbt package
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.45 0.2 1 obesity > output/obesity_google_CNN_unigram.log
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.2 1 ebola > output/ebola_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.2 1 severeweather > output/severeweather_google_CNN_unigram.log

java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.40 0.2 3 winterstorm > output/winterstorm_google_CNN_trigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.2 3 severeweather > output/severeweather_google_CNN_trigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.45 0.2 3 obesity > output/obesity_google_CNN_trigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.2 3 ebola > output/ebola_google_CNN_trigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.50 0.2 3 egypt > output/egypt_google_CNN_trigram.log
