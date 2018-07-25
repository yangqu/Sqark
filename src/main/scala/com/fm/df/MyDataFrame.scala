package com.fm.df

import com.`trait`.CommonTrait
import org.apache.spark
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._

object MyDataFrame extends CommonTrait{
  def doJob(): Unit ={
    import spark.implicits._
    val rdd = sc.parallelize(Array(("quyang","88"),("gulu","198"),("yaoyao","88")))
    val schemaString = "name age"
    val fields = schemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)
    val rowRDD = rdd
      .map(attributes => Row(attributes._1, attributes._2))
    val peopleDF = spark.createDataFrame(rowRDD, schema)
    peopleDF.createOrReplaceTempView("people")
    val results = spark.sql("SELECT name FROM people")
    results.map(attributes => "Name: " + attributes(0)).show()
    val result_rdd= results.rdd.map(line => (line(0), line(1)))
    //result_rdd.pipe("ls").foreach(println(_))
    results.printSchema()
  }
}
