package Interfaces

import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/17/17.
 */
trait IAuxiliaryDataFilter extends java.io.Serializable {
	def filter(input:RDD[Tweet]): RDD[Tweet]
}
