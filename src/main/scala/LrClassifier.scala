package main.scala

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * Created by saur6410 on 2/25/17.
 */
object LrClassifier {

  def main(args: Array[String]): Unit = {

    val trainingFilename = "data/training/1"
    val testFilename = "data/test/1"

    val conf = new SparkConf()
        .setMaster("local[*]")
        .setAppName("ThesisPoC")
      val sc = new SparkContext(conf)
    val trainingFileContent = sc.textFile(trainingFilename)
    val lrClassifier = new LogisticRegressionWithLBFGS()
    lrClassifier.optimizer.setConvergenceTol(0.01)
    lrClassifier.optimizer.setNumIterations(75)

    val trainingData = trainingFileContent.map(LabeledPoint.parse)

    val model = lrClassifier
      .setNumClasses(2)
      .run(trainingData)

    val testFileContent = sc.textFile(testFilename)
    val testData = testFileContent.map(LabeledPoint.parse)

    val predictionAndLabels = testData.map { case LabeledPoint(label, features) =>
    val prediction = model.predict(features)
    (prediction, label)
    }

    predictionAndLabels.collect()
    val p = predictionAndLabels.map(p => println(s"${p._1} : ${p._2}"))
    p.collect()




  }

}
