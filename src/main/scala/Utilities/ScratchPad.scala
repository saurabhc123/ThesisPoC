package Utilities

import main.SparkContextManager

/**
 * Created by saur6410 on 3/12/17.
 */
object ScratchPad {

	def Scratch() =
	{
		val sc = SparkContextManager.getContext
		val bookpair = Array(1,2,3,4,5)
		val bookpairRdd = sc.parallelize(bookpair)
		val readerbook = Array(6,7,8,9)
		val readerRdd = sc.parallelize(readerbook).map(x => x)
		val readerRddBc = sc.broadcast(readerRdd)
		val bookpairBc = sc.broadcast(bookpairRdd)
		val joinedRdd = readerRdd.map(r => bookpairBc.value.map(b => println(b*r)) )
		joinedRdd.collect()
		joinedRdd.foreach(x => println(x.collect()))
	}

}
