package com.fm.etl

import java.io.{File, PrintWriter}

import scala.collection.mutable.{ArrayBuffer, HashMap, Set}
import scala.io.Source

object MyCombine {
  def main(args:Array[String]): Unit = {
    val file1 = Source.fromFile("E:\\level_define.txt")
    val file2 = Source.fromFile("E:\\xianxialouyuhuizong.txt", "utf-8")
    val map = HashMap[String, String]();

    val write = new PrintWriter(new File("E:\\xianxialouyudengji.txt"))
    for (line <- file1.getLines()) {
      map.+=(line.split("\t")(0)->line.split("\t")(3))
    }
    for (line <- file2.getLines()) {
      val no = line.split("\t")(0)
      write.println(line+"\t"+map.getOrElse(no,"#"))
    }
  }

}
