package main.Implementations.FeatureGeneratorImpl


import main.DataTypes.Tweet
import main.Interfaces.DataType._
import main.Interfaces.IFeatureGenerator
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.mllib.linalg.{Vectors, Vector, VectorPub}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

import scala.util.Try

/**
 * A feature generator for word vector generation
 * Created by Eric on 2/2/2017.
 */
class WordVectorGenerator extends IFeatureGenerator {
	var model: Word2VecModel = _

	def InitModel() = {
		val filename = AuxiliaryDataBasedExperiment.auxiliaryDataFile
		val sc = SparkContextManager.getContext
		val delimiter = AuxiliaryDataBasedExperiment.fileDelimiter
		var fileContent = sc.textFile(filename).map(l => l.split(delimiter))
		fileContent = fileContent.map(stringArrays => {
			if (stringArrays.length > 2) {
				val resultArray = new Array[String](2)
				resultArray(0) = stringArrays(0)
				resultArray(1) = stringArrays(1) + stringArrays(2)
				resultArray
			}
			else {
				stringArrays
			}
		})

		val tweetSentences = fileContent.filter(record => record.length > 1) .map(t => t(1))
		val sentences = tweetSentences.map(t => t.split(" ").toSeq)
		model = new Word2Vec().setVectorSize(300).setMinCount(1).fit(sentences)
	}

	def train(tweets: RDD[Tweet]): Unit = {
		if (model == null)
			InitModel()
	}

	override def generateFeatures(tweets: RDD[Tweet], dataType: DataType): RDD[LabeledPoint] = {
		if (dataType == TRAINING) {
			train(tweets)
		}
		checkModel()

		tweets.map(t => generateFeature(t))

	}


	def saveGenerator(filePath: String, sc: SparkContext): Unit = {
		checkModel()
		model.save(sc, filePath)
	}

	def loadGenerator(filePath: String, sc: SparkContext): Unit = {
		model = Word2VecModel.load(sc, filePath)
	}

	def checkModel(): Unit = {
		if (model == null) {
			throw new IllegalStateException("Model has not been loaded or trained!")
		}
	}

	override def generateFeature(tweet: Tweet): LabeledPoint =
	{
		if (model == null)
			InitModel()
		def avgWordFeatures(wordFeatures: Iterable[Vector]): Vector = VectorPub.BreezeVectorPublications(
			wordFeatures.map(VectorPub.VectorPublications(_).toBreeze).reduceLeft((x, y) => x + y) / wordFeatures.size.toDouble)
			.fromBreeze

		try {
			val vector = tweet.tweetText.split(" ").map(w => Try(model.transform(w))).filter(_.isSuccess).map(x => x.get)

			val averagedVector = avgWordFeatures(vector.toIterable)

			new LabeledPoint(tweet.label, averagedVector)
		}
		catch {
			case _ => {
				println(s"**********Omitting Tweet**********:${tweet.tweetText}")
				return LabeledPoint(tweet.label, Vectors.dense(new Array[Double](300)))}
		}

	}
}
