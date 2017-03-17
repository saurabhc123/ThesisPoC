package Implementations.AuxiliaryDataRetrievers

import Interfaces.IAuxiliaryDataFilter
import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/17/17.
 */
class CosineSimAuxiliaryFilter extends IAuxiliaryDataFilter {
	override def filter(input: RDD[Tweet]): RDD[Tweet] = {
		null
	}
}
