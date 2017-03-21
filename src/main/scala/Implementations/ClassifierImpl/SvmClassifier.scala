package Implementations.ClassifierImpl

import Interfaces.{IClassifier, IClassifierModel}
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/19/17.
 */
class SvmClassifier extends IClassifier{
	override def train(labels: RDD[LabeledPoint]): IClassifierModel = {
		val num_labels = 2//labels.map(x => x.label).distinct().count().toInt
		val model = SVMWithSGD.train(labels, 50)
		new SvmClassifierModel(model)
	}

	override def loadModel(): IClassifierModel = ???
}
