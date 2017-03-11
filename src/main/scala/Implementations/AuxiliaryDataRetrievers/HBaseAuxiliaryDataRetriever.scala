package Implementations.AuxiliaryDataRetrievers

import main.DataTypes.Tweet
import main.Interfaces.IAuxiliaryDataRetriever
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/9/17.
 */
class HBaseAuxiliaryDataRetriever extends IAuxiliaryDataRetriever {
	override def retrieveAuxiliaryData(distinguishingWords: Array[String]): RDD[Tweet] = {


		null
	}
}
