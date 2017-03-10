package main.Implementations.ClassifierImpl

import Interfaces.IClassifierModel
import main.DataTypes.PredictionResult
import main.SparkContextManager
import main.scala.Implementations.ClassifierImpl.LogisticRegressionClassifier
import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by ericrw96 on 2/2/17.
  */
class LogisticRegressionClassifierModel(model: LogisticRegressionModel) extends IClassifierModel{
  override def saveModel(): Unit = {
    model.save(SparkContextManager.getContext, LogisticRegressionClassifier.ModelFilename)
  }

  override def predict(labeledFeatures: RDD[LabeledPoint]): RDD[PredictionResult] = {
    labeledFeatures.map(lp => PredictionResult(trueLabel = lp.label, predictedLabel = model.predict(lp.features)))
  }
}
