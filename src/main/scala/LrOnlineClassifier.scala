package main.scala

import java.nio.file.{Files, Paths}

import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.StreamingLogisticRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by saur6410 on 2/25/17.
 */
object LrOnlineClassifier {



  def main(args: Array[String]): Unit = {

    if (args.length != 4) {
      System.err.println(
        "Usage: StreamingLogisticRegression <trainingDir> <testDir> <batchDuration> <numFeatures>")
      System.exit(1)
    }

    val conf = new SparkConf().setMaster("local").setAppName("StreamingLogisticRegression")
    val ssc = new StreamingContext(conf, Seconds(args(2).toLong))

    println(s"Training File Path:${Files.exists(Paths.get(args(0)))}")
    println(s"Test File Path:${Files.exists(Paths.get(args(1)))}")

    val trainingData = ssc.textFileStream(args(0)).map(LabeledPoint.parse)

    val t = ssc.textFileStream(args(0))
    t.print()
    val testData = ssc.textFileStream(args(1)).map(LabeledPoint.parse)


    val model = new StreamingLogisticRegressionWithSGD()
      .setInitialWeights(Vectors.zeros(args(3).toInt))

    model.trainOn(trainingData)
    val predictions =  model.predictOnValues(testData.map(lp => (lp.label, lp.features)))
    predictions.print()

    ssc.start()
    ssc.awaitTermination()
}

}
