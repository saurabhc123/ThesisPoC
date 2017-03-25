package Implementations.ClassifierImpl

import DataTypes.CnnPrediction
import Interfaces.{IClassifier, IClassifierModel}
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

/**
 * Created by saur6410 on 3/19/17.
 */
class CnnClassifier extends IClassifier{
	override def train(labels: RDD[LabeledPoint]): IClassifierModel = {
		implicit val formats = DefaultFormats
		val exportedFolderName = "egypt"
		val url = s"http://localhost:5000/cnn_train_and_get_prediction_labels/${AuxiliaryDataBasedExperiment.folderNameForCnnClassifier}"
		try {
			val result = scala.io.Source.fromURL(url).mkString
			val predictions = parse(result).extract[Array[CnnPrediction]]
			CnnClassifier._predictions = predictions
		}
		catch {

			case _ => {
				println("Some issues with executing CNN web classifier.")
				//return new LabeledPoint(label, Vectors.dense(0))
			}
		}
		new CnnClassifierModel
	}

	override def loadModel(): IClassifierModel = ???
}

object CnnClassifier {
	var _predictions:Array[CnnPrediction] = null
	var _exportedFolderName = " "

}
