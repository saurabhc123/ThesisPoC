package Interfaces

import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/8/17.
 */
trait IExperiment extends java.io.Serializable {
  def performExperiment(train:RDD[Tweet], validation:RDD[Tweet]) : Unit

}
