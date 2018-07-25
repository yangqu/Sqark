package com.fm.etl

import java.io.{File, PrintWriter}
import java.util

import scala.collection.mutable.HashMap
import scala.io.Source

object MyPercent {
  def main(args:Array[String]): Unit = {
    val file1 = Source.fromFile("E:\\city_building_date.csv")
    val file2 = Source.fromFile("E:\\线下楼宇数据汇总表.txt", "utf-8")
    val set_my = scala.collection.mutable.Set[String]();
    val set_wei = scala.collection.mutable.Set[String]();
    val set_my_city = scala.collection.mutable.Set[String]();
    val set_wei_city = scala.collection.mutable.Set[String]();

    //val write = new PrintWriter(new File("E:\\线下楼宇等级表.txt"))
    for (line <- file1.getLines()) {
      val building_no=line.split(",")(4)
      val city=line.split(",")(0)
      set_wei.+=(building_no)
      if(city=="上海"){
        set_wei_city.+=(building_no)
      }
    }
    for (line <- file2.getLines()) {
      val building_no=line.split("\t")(0)
      val city=line.split("\t")(2)
      set_my.+=(building_no)
      if(city=="上海"){
        set_my_city.+=(building_no)
      }
    }

    println("葛维维全量楼宇\t"+set_wei.size)
    println("曲洋洋全量楼宇\t"+set_my.size)
    println("全量楼宇比例\t"+set_wei.size.toDouble/set_my.size)
    println("葛维维上海楼宇\t"+set_wei_city.size)
    println("曲洋洋上海楼宇\t"+set_my_city.size)
    println("上海楼宇比例\t"+set_wei_city.size.toDouble/set_my_city.size)
    println("全量楼宇交集\t"+set_my.&(set_wei).size)
    println("上海楼宇交集\t"+set_my_city.&(set_my_city).size)
  }
}
