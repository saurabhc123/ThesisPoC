package Implementations.ClassifierImpl

import Interfaces.{IClassifier, IClassifierModel}
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.optimization.L1Updater
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/19/17.
 */
class SvmClassifier extends IClassifier{
	override def train(labels: RDD[LabeledPoint]): IClassifierModel = {
		val model =  new SVMWithSGD
		model.optimizer.setNumIterations(200)
		.setStepSize(20)
		.setUpdater(new L1Updater)
		.setRegParam(0.01)

		val m = model.run(labels)
		new SvmClassifierModel(m)

	}

	override def loadModel(): IClassifierModel = ???
}
