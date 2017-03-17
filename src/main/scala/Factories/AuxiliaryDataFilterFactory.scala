package Factories

import Factories.FilterType.FilterType
import Implementations.AuxiliaryDataRetrievers.FpmAuxiliaryFilter
import Interfaces.IAuxiliaryDataFilter
import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/17/17.
 */
class AuxiliaryDataFilterFactory(trainingTweets: RDD[Tweet]) {
	def getAuxiliaryDataFilter(filterType: FilterType): IAuxiliaryDataFilter = {
		filterType match {
			case FilterType.FpmFilter =>
				{
					val filter = new FpmAuxiliaryFilter(trainingTweets)
					filter
				}
			case FilterType.CosineSim => null
			case FilterType.Wmd => null
		}
	}
}


object FilterType extends Enumeration{
	type FilterType = Value
	val FpmFilter, CosineSim, Wmd = Value
}