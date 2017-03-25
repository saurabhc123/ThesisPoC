package Implementations.ClassifierImpl

import Interfaces.IClassifierModel
import main.DataTypes.PredictionResult
import main.SparkContextManager
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/19/17.
 */
class CnnClassifierModel() extends IClassifierModel{
	override def saveModel(): Unit = ???

	override def predict(labeledFeatures: RDD[LabeledPoint]): RDD[PredictionResult] = {
		//labeledFeatures.map(lp => PredictionResult(trueLabel = lp.label, predictedLabel = model.predict(lp.features)))
		val sc = SparkContextManager.getContext
		sc.parallelize(CnnClassifier._predictions.map(cp => new PredictionResult(cp.actual, cp.predicted)))
	}
}
