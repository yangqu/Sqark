package com.fm.ml

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object MyAlg {
  def main(args:Array[String]): Unit ={

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local")
    val sc = new SparkContext(conf)
    Corretation(sc)
    StratifiedSample(sc)
    HypothesisTest(sc)
  }

  def CollaborativeFiltering(sc:SparkContext):Unit={
    import org.apache.spark.mllib.recommendation.ALS
    import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
    import org.apache.spark.mllib.recommendation.Rating

    val data = sc.textFile("file:///D:\\ml\\data\\test.data")
    val ratings = data.map(_.split(',') match { case Array(user, item, rate) =>
      Rating(user.toInt, item.toInt, rate.toDouble)
    })
    val rank = 10
    val numIterations = 10
    val model = ALS.train(ratings, rank, numIterations, 0.01)
    val usersProducts = ratings.map { case Rating(user, product, rate) =>
      (user, product)
    }
    val predictions =
      model.predict(usersProducts).map { case Rating(user, product, rate) =>
        ((user, product), rate)
      }
    val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(predictions)
    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
      val err = (r1 - r2)
      err * err
    }.mean()
    println("Mean Squared Error = " + MSE)
    model.save(sc, "file:///D:\\ml\\model\\myCollaborativeFilter")
    val sameModel = MatrixFactorizationModel.load(sc, "file:///D:\\ml\\model\\myCollaborativeFilter")
  }

  def HypothesisTest(sc:SparkContext):Unit={
    import org.apache.spark.mllib.linalg._
    import org.apache.spark.mllib.regression.LabeledPoint
    import org.apache.spark.mllib.stat.Statistics
    import org.apache.spark.mllib.stat.test.ChiSqTestResult
    val vec: Vector = Vectors.dense(0.1, 0.15, 0.2, 0.3, 0.25)
    val goodnessOfFitTestResult = Statistics.chiSqTest(vec)
    println(s"$goodnessOfFitTestResult\n")
    val mat: Matrix = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
    val independenceTestResult = Statistics.chiSqTest(mat)
    println(s"$independenceTestResult\n")
    val obs: RDD[LabeledPoint] =
      sc.parallelize(
        Seq(
          LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0)),
          LabeledPoint(1.0, Vectors.dense(1.0, 2.0, 0.0)),
          LabeledPoint(-1.0, Vectors.dense(-1.0, 0.0, -0.5)
          )
        )
      )
    val featureTestResults: Array[ChiSqTestResult] = Statistics.chiSqTest(obs)
    featureTestResults.zipWithIndex.foreach { case (k, v) =>
      println("Column " + (v + 1).toString + ":")
      println(k)
    }
  }

  def StratifiedSample(sc:SparkContext):Unit={
    val data = sc.parallelize(
      Seq((1, 'a'), (1, 'b'), (2, 'c'), (2, 'd'), (2, 'e'), (3, 'f')))
    val fractions = Map(1 -> 0.1, 2 -> 0.6, 3 -> 0.3)
    val approxSample = data.sampleByKey(withReplacement = false, fractions = fractions)
    val exactSample = data.sampleByKeyExact(withReplacement = false, fractions = fractions)
    println("StratifiedSample: "+approxSample.collect.mkString("\t"))
    println("StratifiedSample: "+exactSample.collect.mkString("\t"))
  }

  def Corretation(sc:SparkContext):Unit={
    import org.apache.spark.mllib.linalg._
    import org.apache.spark.mllib.stat.Statistics
    val seriesX: RDD[Double] = sc.parallelize(Array(1, 2, 3, 3, 5))
    val seriesY: RDD[Double] = sc.parallelize(Array(11, 22, 33, 33, 555))
    val correlation: Double = Statistics.corr(seriesX, seriesY, "pearson")
    println(s"Correlation is: $correlation")

    val data: RDD[Vector] = sc.parallelize(
      Seq(
        Vectors.dense(1.0, 10.0, 100.0),
        Vectors.dense(2.0, 20.0, 200.0),
        Vectors.dense(5.0, 33.0, 366.0))
    )
    val correlMatrix: Matrix = Statistics.corr(data, "pearson")
    println(correlMatrix.toString)
  }

}
