package main.scala.Implementations

import Factories.{AuxiliaryDataRetrieverFactory, ClassifierType, DistinguishingWordsGeneratorFactory}
import Interfaces.{IDistinguishingWordsGenerator, IExperiment}
import Utilities.{TweetsFileProcessor, CosineSimilarityBasedFilter, MetricsCalculator}
import main.DataTypes.Tweet
import main.Factories.ClassifierFactory
import main.Interfaces.{DataType, IAuxiliaryDataRetriever}
import main.scala.Factories.{FeatureGeneratorFactory, FeatureGeneratorType}
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
		val distinguishingWordGenerator: IDistinguishingWordsGenerator = new DistinguishingWordsGeneratorFactory().getDistinguishingWordsGenerator()
		val auxiliaryDataRetriever: IAuxiliaryDataRetriever = new AuxiliaryDataRetrieverFactory().getAuxiliaryDataRetriever(AuxiliaryDataBasedExperiment.auxiliaryDataFile)

		//Train the classifier
		var model = classifier.train(featureGenerator.generateFeatures(train, DataType.TRAINING))

		//Perform Validation and get score
		val predictions = model.predict(featureGenerator.generateFeatures(validation, DataType.TEST));
		val metricsCalculator = MetricsCalculator.GenerateClassifierMetrics(predictions)
		var f1 = metricsCalculator.macroF1
		val thresholdF1 = AuxiliaryDataBasedExperiment.thresholdF1
		val auxiliaryThresholdExpectation = AuxiliaryDataBasedExperiment.auxiliaryThresholdExpectation
		println(s"Initial F1={$f1}")
		println("Initial - ConfusionMatrix:")
		println(s"${metricsCalculator.confusionMatrix}")

		var dataToTrainOn = train
		var numberOfIterations = 0
		while (f1 < thresholdF1 && numberOfIterations < 5) {
			//Get the most distinguishing words
			val distinguishingWords = distinguishingWordGenerator.generateMostDistinguishingWords(dataToTrainOn.filter(t => t.label == 1.0))

			//Retrieve auxiliary data by using most distinguishing words
			val auxiliaryData = auxiliaryDataRetriever.retrieveAuxiliaryData(distinguishingWords)

			//Filter based on cosine similarity
			val positiveLabelTrainingData = dataToTrainOn.filter(trainingTweet => trainingTweet.label == 1.0)
			val filteredAuxiliaryData = new CosineSimilarityBasedFilter().filter(positiveLabelTrainingData, auxiliaryData, featureGenerator)

			println(s"Retrieved ${filteredAuxiliaryData.count()} new auxiliary tweets.")
			print(filteredAuxiliaryData.foreach(tweet => println(s"${tweet.label}|${tweet.tweetText}")))

			//train using training + auxiliary data
			val fullData = dataToTrainOn.union(filteredAuxiliaryData)
			model = classifier.train(featureGenerator.generateFeatures(fullData, DataType.TRAINING))

			//perform prediction on validation data
			val validationDataPredictions = model.predict(featureGenerator.generateFeatures(validation, DataType.TRAINING))
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

		val trainingTweets = TweetsFileProcessor.LoadTweetsFromFile(AuxiliaryDataBasedExperiment.trainingDataFile)
		val validationTweets = TweetsFileProcessor.LoadTweetsFromFile(AuxiliaryDataBasedExperiment.validationDataFile)
		this.performExperiment(trainingTweets, validationTweets)

	}
}

object AuxiliaryDataBasedExperiment {
	val minSimilarityThreshold = 0.6
	val maxFpmWordsToPick = 30
	val minFpmWordsDetected = 2

	val thresholdF1 = 0.98
	val auxiliaryThresholdExpectation = 0.01
	val trainingDataFile = "data/training/exp_training_data.txt"
	val validationDataFile = "data/training/exp_validation_data.txt"
	val auxiliaryDataFile = "data/training/multi_class_lem"



}
