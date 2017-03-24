package main.scala.Implementations

import Factories._
import Implementations.AuxiliaryDataRetrievers.FileBasedAuxiliaryDataRetriever
import Interfaces.IExperiment
import Utilities.{CleanTweet, MetricsCalculator, TweetsFileProcessor}
import main.DataTypes.Tweet
import main.Factories.ClassifierFactory
import main.Interfaces.{DataType, IAuxiliaryDataRetriever}
import main.SparkContextManager
import main.scala.Factories.{FeatureGeneratorFactory, FeatureGeneratorType}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/8/17.
 */
class AuxiliaryDataBasedExperiment extends IExperiment {


	override def performExperiment(train: RDD[Tweet], validation: RDD[Tweet]): Unit = {


		//********** FACTORY BASED INITIALIZATIONS ************
		//Get the classifier from the factory
		val classifierFactory = new ClassifierFactory()
		val classifier = classifierFactory.getClassifier(ClassifierType.LogisticRegression)
		val featureGenerator = FeatureGeneratorFactory.getFeatureGenerator(FeatureGeneratorType.WebServiceWord2Vec)
		val auxiliaryDataRetriever: IAuxiliaryDataRetriever = new AuxiliaryDataRetrieverFactory().getAuxiliaryDataRetriever(AuxiliaryDataBasedExperiment.auxiliaryDataFile)

		val trainingFeatures: RDD[LabeledPoint] = featureGenerator.generateFeatures(train, DataType.TRAINING)
		//Train the classifier
		var model = classifier.train(trainingFeatures)

		val validationFeatures: RDD[LabeledPoint] = featureGenerator.generateFeatures(validation, DataType.TEST)
		//Perform Validation and get score
		val predictions = model.predict(validationFeatures);
		val metricsCalculator = MetricsCalculator.GenerateClassifierMetrics(predictions)
		var f1 = metricsCalculator.macroF1
		val thresholdF1 = AuxiliaryDataBasedExperiment.thresholdF1
		val auxiliaryThresholdExpectation = AuxiliaryDataBasedExperiment.auxiliaryThresholdExpectation
		println(s"Initial F1={$f1}")
		println("Initial - ConfusionMatrix:")
		println(s"${metricsCalculator.confusionMatrix}")


		var dataToTrainOn = train
		var numberOfIterations = 0
		while (f1 < thresholdF1 && numberOfIterations < AuxiliaryDataBasedExperiment.maxExperimentIterations) {
			//Get tweets based on most distinguishing words with FPM
			val filterFactory = new AuxiliaryDataFilterFactory(dataToTrainOn, featureGenerator)
			val fpmFilter = filterFactory.getAuxiliaryDataFilter(FilterType.FpmFilter)

			val sourceAuxiliaryData = FileBasedAuxiliaryDataRetriever.readTweetsFromAuxiliaryFile()
			//Retrieve auxiliary data by using most distinguishing words
			val auxiliaryData = CleanTweet.clean(fpmFilter.filter(sourceAuxiliaryData), SparkContextManager.getContext)

			//Filter based on cosine similarity
			val positiveLabelTrainingData = dataToTrainOn.filter(trainingTweet => trainingTweet.label == 1.0)
			val filter = filterFactory.getAuxiliaryDataFilter(FilterType.CosineSim)
			val filteredAuxiliaryData = filter.filter(auxiliaryData)

			println(s"Retrieved ${filteredAuxiliaryData.count()} new auxiliary tweets.")
			print(filteredAuxiliaryData.foreach(tweet => println(s"${tweet.label}|${tweet.tweetText}")))

			//train using training + auxiliary data
			val fullData = dataToTrainOn.union(filteredAuxiliaryData)
			model = classifier.train(featureGenerator.generateFeatures(fullData, DataType.TRAINING))

			//perform prediction on validation data
			val validationDataPredictions = model.predict(validationFeatures)
			val metrics = MetricsCalculator.GenerateClassifierMetrics(validationDataPredictions)
			val auxF1 = metrics.macroF1


			//if f1 is greater than aux threshold, add aux to the training data.
			if ((auxF1 - f1) > auxiliaryThresholdExpectation)
			{
				println(s"Adding ${filteredAuxiliaryData.count()} auxiliary tweets to the training data.")
				f1 = auxF1
				dataToTrainOn = fullData
			}
			numberOfIterations += 1
			println(s"Aux F1 - Iteration-$numberOfIterations=$auxF1")
			println("Aux F1 - ConfusionMatrix:")
			println(s"${metrics.confusionMatrix}")
		}
	}

	override def SetupAndRunExperiment(): Unit = {

		val trainingTweets = TweetsFileProcessor.LoadTweetsFromFile(AuxiliaryDataBasedExperiment.trainingDataFile, AuxiliaryDataBasedExperiment.fileDelimiter)
		val validationTweets = TweetsFileProcessor.LoadTweetsFromFile(AuxiliaryDataBasedExperiment.validationDataFile, AuxiliaryDataBasedExperiment.fileDelimiter)
		val cleanTrainingTweets = CleanTweet.clean(trainingTweets, SparkContextManager.getContext)
		val cleanValidationTweets = CleanTweet.clean(validationTweets, SparkContextManager.getContext)
		this.performExperiment(cleanTrainingTweets, cleanValidationTweets)

	}
}

object AuxiliaryDataBasedExperiment {
	val minSimilarityThreshold = 0.6
	val cosineSimilarityWindowSize= 0.1
	val minWmDistanceThreshold = 0.0199
	val maxFpmWordsToPick = 30
	val minFpmWordsDetected = 0
	val refreshLocalWordVectors = false
	val maxExperimentIterations = 10
	val maxAuxTweetsToAddEachIteration = 10

	val thresholdF1 = 0.98
	val auxiliaryThresholdExpectation = 0.01
	val fileDelimiter = ","
	val trainingDataFile = "data/final/egypt_training_data.txt"
	val validationDataFile = "data/final/egypt_validation_data.txt"
	val auxiliaryDataFile = "data/final/egypt_auxiliary_data.txt"
	//val auxiliaryDataFile = "data/ebola.csv"
	val supplementedCleanAuxiliaryFile = "data/final/egypt_auxiliary_data_clean.txt"



}
