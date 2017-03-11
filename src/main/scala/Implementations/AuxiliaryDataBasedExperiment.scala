package main.scala.Implementations

import Factories.{AuxiliaryDataRetrieverFactory, ClassifierType, DistinguishingWordsGeneratorFactory}
import Interfaces.{IDistinguishingWordsGenerator, IExperiment}
import Utilities.{CosineSimilarityBasedFilter, MetricsCalculator}
import main.DataTypes.Tweet
import main.Factories.ClassifierFactory
import main.Interfaces.{DataType, IAuxiliaryDataRetriever}
import main.scala.Factories.{FeatureGeneratorType, FeatureGeneratorFactory}
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/8/17.
 */
class AuxiliaryDataBasedExperiment extends IExperiment {


	override def performExperiment(train: RDD[Tweet], validation: RDD[Tweet]): Unit = {


		//Get the classifier from the factory
		val classifierFactory = new ClassifierFactory()
		val classifier = classifierFactory.getClassifier(ClassifierType.LogisticRegression)

		val featureGenerator = FeatureGeneratorFactory.getFeatureGenerator(FeatureGeneratorType.WebServiceWord2Vec)
		val distinguishingWordGenerator: IDistinguishingWordsGenerator = new DistinguishingWordsGeneratorFactory().getDistinguishingWordsGenerator()
		val auxiliaryDataRetriever: IAuxiliaryDataRetriever = new AuxiliaryDataRetrieverFactory().getAuxiliaryDataRetriever()
		//Train the classifier
		var model = classifier.train(featureGenerator.generateFeatures(train, DataType.TRAINING))

		//Perform Validation and get score
		val predictions = model.predict(featureGenerator.generateFeatures(validation, DataType.TEST));
		val metricsCalculator = MetricsCalculator.GenerateClassifierMetrics(predictions)
		var f1 = metricsCalculator.macroF1
		val thresholdF1 = 0.9
		val auxiliaryThresholdExpectation = 0.05



		var distinguishingWordTweetCorpus = train
		var numberOfIterations = 0
		while (f1 < thresholdF1 && numberOfIterations < 5) {
			//Get the most distinguishing words
			val distinguishingWords = distinguishingWordGenerator.generateMostDistinguishingWords(distinguishingWordTweetCorpus)

			//Retrieve auxiliary data by using most distinguishing words
			val auxiliaryData = auxiliaryDataRetriever.retrieveAuxiliaryData(distinguishingWords)

			//Filter based on cosine similarity
			val filteredAuxiliaryData = new CosineSimilarityBasedFilter().filter(distinguishingWordTweetCorpus, auxiliaryData, featureGenerator)

			//train using training + auxiliary data
			val fullData = train.union(filteredAuxiliaryData)
			model = classifier.train(featureGenerator.generateFeatures(fullData, DataType.TRAINING))

			//perform prediction on validation data
			val auxiliaryPredictions = model.predict(featureGenerator.generateFeatures(validation, DataType.TRAINING))
			val metrics = MetricsCalculator.GenerateClassifierMetrics(auxiliaryPredictions)
			val auxF1 = metrics.macroF1

			//if f1 is greater than aux threshold, add aux to the training data.
			if ((auxF1 - f1) > auxiliaryThresholdExpectation) {
				f1 = auxF1
				distinguishingWordTweetCorpus = fullData
			}
			numberOfIterations += 1

		}
	}
}
