package main.scala.Implementations.ClassifierImpl

import Interfaces.{IClassifierModel, IClassifier}
import main.Implementations.ClassifierImpl.LogisticRegressionClassifierModel
import main.SparkContextManager
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
/**
  * Created by Eric on 2/2/2017.
  */
class LogisticRegressionClassifier extends IClassifier{

  /*override def train(labels: RDD[LabeledPoint]): Unit = {
    val num_labels = labels.map(x => x.label).distinct().count().toInt
    val lrClassifier = new LogisticRegressionWithLBFGS()
    Model = lrClassifier.setNumClasses(num_labels).run(labels)
  }

  override def predict(toBePredicted: RDD[Vector]): RDD[Double] = {
    Model.predict(toBePredicted)
  }

  override def saveClassifier(filePath: String, sc: SparkContext): Unit = {
    Model.save(sc,filePath)
  }

  override def loadClassifier(filePath: String, sc: SparkContext): Unit = {
    Model = LogisticRegressionModel.load(sc,filePath)
  }*/
  override def train(labels: RDD[LabeledPoint]): IClassifierModel = {
    val num_labels = 2//labels.map(x => x.label).distinct().count().toInt
    val lrClassifier = new LogisticRegressionWithLBFGS()
    new LogisticRegressionClassifierModel(lrClassifier.setNumClasses(num_labels).run(labels))
  }

  override def loadModel(): IClassifierModel = {
    new LogisticRegressionClassifierModel(LogisticRegressionModel.load(SparkContextManager.getContext,LogisticRegressionClassifier.ModelFilename))
  }
}
object LogisticRegressionClassifier{
  val ModelFilename: String = "LogisticRegressionModel"
}
