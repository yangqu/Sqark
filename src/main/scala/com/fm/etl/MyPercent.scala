package com.fm.etl

import java.io.{File, PrintWriter}
import java.util

import scala.collection.mutable.HashMap
import scala.io.Source

object MyPercent {
  def main(args:Array[String]): Unit = {
    val file1 = Source.fromFile("E:\\city_building_date.csv")
    val file2 = Source.fromFile("E:\\����¥�����ݻ��ܱ�.txt", "utf-8")
    val set_my = scala.collection.mutable.Set[String]();
    val set_wei = scala.collection.mutable.Set[String]();
    val set_my_city = scala.collection.mutable.Set[String]();
    val set_wei_city = scala.collection.mutable.Set[String]();

    //val write = new PrintWriter(new File("E:\\����¥��ȼ���.txt"))
    for (line <- file1.getLines()) {
      val building_no=line.split(",")(4)
      val city=line.split(",")(0)
      set_wei.+=(building_no)
      if(city=="�Ϻ�"){
        set_wei_city.+=(building_no)
      }
    }
    for (line <- file2.getLines()) {
      val building_no=line.split("\t")(0)
      val city=line.split("\t")(2)
      set_my.+=(building_no)
      if(city=="�Ϻ�"){
        set_my_city.+=(building_no)
      }
    }

    println("��άάȫ��¥��\t"+set_wei.size)
    println("������ȫ��¥��\t"+set_my.size)
    println("ȫ��¥�����\t"+set_wei.size.toDouble/set_my.size)
    println("��άά�Ϻ�¥��\t"+set_wei_city.size)
    println("�������Ϻ�¥��\t"+set_my_city.size)
    println("�Ϻ�¥�����\t"+set_wei_city.size.toDouble/set_my_city.size)
    println("ȫ��¥���\t"+set_my.&(set_wei).size)
    println("�Ϻ�¥���\t"+set_my_city.&(set_my_city).size)
  }
}
