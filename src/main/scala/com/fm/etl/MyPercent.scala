package com.fm.etl

import java.io.{File, PrintWriter}
import java.util

import scala.collection.mutable.HashMap
import scala.io.Source

object MyPercent {
  def main(args:Array[String]): Unit = {
    val file1 = Source.fromFile("E:\\city_building_date.csv")
    val file2 = Source.fromFile("E:\\xianxialouyuhuizong.txt", "utf-8")
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

  }
}
