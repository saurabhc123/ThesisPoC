package Implementations.ClassifierImpl

import Interfaces.IClassifierModel
import main.DataTypes.PredictionResult
import org.apache.spark.mllib.classification.SVMModel
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/19/17.
 */
class SvmClassifierModel(model: SVMModel) extends IClassifierModel{
	override def saveModel(): Unit = ???

	override def predict(labeledFeatures: RDD[LabeledPoint]): RDD[PredictionResult] = {
		labeledFeatures.map(lp => PredictionResult(trueLabel = lp.label, predictedLabel = model.predict(lp.features)))
	}
}
