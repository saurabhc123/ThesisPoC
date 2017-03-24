package Factories

import Factories.FilterType.FilterType
import Implementations.AuxiliaryDataRetrievers.{WmdAuxiliaryFilter, CosineSimAuxiliaryFilter, FpmAuxiliaryFilter}
import Interfaces.IAuxiliaryDataFilter
import main.DataTypes.Tweet
import main.Interfaces.IFeatureGenerator
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/17/17.
 */
class AuxiliaryDataFilterFactory(trainingTweets: RDD[Tweet], featureGenerator:IFeatureGenerator) {
	def getAuxiliaryDataFilter(filterType: FilterType): IAuxiliaryDataFilter = {
		filterType match {
			case FilterType.FpmFilter =>
				{
					val filter = new FpmAuxiliaryFilter(trainingTweets)
					filter
				}
			case FilterType.CosineSim => {
				val positiveTrainingTweets = trainingTweets//.filter(trainingTweet => trainingTweet.label == 1.0)
				new CosineSimAuxiliaryFilter(positiveTrainingTweets,featureGenerator)
			}
			case FilterType.Wmd => {
				new WmdAuxiliaryFilter(trainingTweets,featureGenerator)
			}
		}
	}
}


object FilterType extends Enumeration{
	type FilterType = Value
	val FpmFilter, CosineSim, Wmd = Value
}