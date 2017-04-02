sbt package
# Obesity
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.50 0.25 1 obesity 1 > output/obesity1_google_CNN_unigram.log
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.50 0.20 1 obesity 2 > output/obesity2_google_CNN_unigram.log
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.60 0.25 1 obesity 3 > output/obesity3_google_CNN_unigram.log
#java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.50 0.25 1 obesity 1 > output/obesity1_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.50 0.20 1 obesity 2 > output/obesity2_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.60 0.25 1 obesity 3 > output/obesity3_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.48 0.20 1 obesity 1 > output/obesity1_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.44 0.25 1 obesity 2 > output/obesity2_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.45 0.30 1 obesity 3 > output/obesity3_local_LR.log

# Egypt
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.50 0.20 1 egypt 1 > output/egypt1_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.60 0.35 1 egypt 2 > output/egypt2_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google cnn 0.55 0.20 1 egypt 3 > output/egypt3_google_CNN_unigram.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.50 0.20 1 egypt 1 > output/egypt1_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.60 0.35 1 egypt 2 > output/egypt2_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program google lr 0.55 0.20 1 egypt 3 > output/egypt3_google_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.45 0.25 1 egypt 1 > output/egypt1_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.55 0.30 1 egypt 2 > output/egypt2_local_LR.log
java -cp target/scala-2.10/thesispoc_2.10-0.1-SNAPSHOT.jar:$(cat target/streams/compile/dependencyClasspath/\$global/streams/export) main.scala.program local lr 0.50 0.18 1 egypt 3 > output/egypt3_local_LR.log
