package com.fm.etl
import java.io.{File, PrintWriter}

import scala.io.Source
import scala.collection.mutable._;

object MyLabel {
  def main(args:Array[String]): Unit = {
    val file = Source.fromFile("E:\\xianxialouyuhuizong.txt", "utf-8")
    val set = Set[String]();
    val map = HashMap[String, Int]();

    val resultMap = HashMap[String, HashMap[String, ArrayBuffer[(Int, Int)]]]();
    val write = new PrintWriter(new File("E:\\level_define.txt"))
    for (line <- file.getLines()) {
      val buildNo = line.split("\t")(0)
      val city = line.split("\t")(2)
      val ttype = line.split("\t")(14)
      val mark = line.split("\t")(16)
      val key = city + "\t" + ttype
      if (ttype == "" || city == "" || ttype == "" || mark == "" || mark == "-1" || !isIntByRegex(mark)) {

      } else {
        map.+=(key->(map.getOrElse(key,0)+1))
        if (resultMap.contains(city)) {
          //resultMap.+=(city->HashMap[String,ArrayBuffer[(Int,Int)]](ttype->ArrayBuffer[(Int,Int)]((buildNo.toInt,mark.toInt))))
          val value = resultMap.getOrElse(city, HashMap[String, ArrayBuffer[(Int, Int)]]())
          if (value.contains(ttype)) {
            val buffer = value.getOrElse(ttype, ArrayBuffer[(Int, Int)]())
            buffer += ((buildNo.toInt, mark.toInt))
            value.+=(ttype -> buffer)
          } else {
            value.+=(ttype -> ArrayBuffer[(Int, Int)]((buildNo.toInt, mark.toInt)))
          }

        } else {
          resultMap.+=(city -> HashMap[String, ArrayBuffer[(Int, Int)]](ttype -> ArrayBuffer[(Int, Int)]((buildNo.toInt, mark.toInt))))
        }

      }
    }
      for (city <- resultMap.keySet) {
        for (map_type <- resultMap.getOrElse(city, HashMap[String, ArrayBuffer[(Int, Int)]]()).keySet) {
          val mapsss = resultMap.getOrElse(city, HashMap[String, ArrayBuffer[(Int, Int)]]())
          val buildings = mapsss.getOrElse(map_type, ArrayBuffer[(Int, Int)]())
          val resultha=map_type match {
            case "商务楼" => defineLevel(city,map_type,buildings);
            case "高级公寓楼" => defineLevel(city,map_type,buildings);
            case "商住两用楼" => defineLevel(city,map_type,buildings);
            case _ => defineLevelAll(city,map_type,buildings);
          }

          //write.print(resultha)

          println(resultha)
        }
      }
      println(map)
      file.close()
    write.close()

  }

  def defineLevelAll(city:String,ttype:String,array:ArrayBuffer[(Int, Int)]):String ={
    val result = new StringBuilder();
    for((build_no:Int,mark:Int)<-array){
      ttype match{
        case "A级(住宅)"=>result.append(build_no).append("\t").append(city).append("\t").append("高级公寓楼").append("\t").append("D").append("\n")
        case "AA级(住宅)"=>result.append(build_no).append("\t").append(city).append("\t").append("高级公寓楼").append("\t").append("B").append("\n")
        case "AAA级(住宅)"=>result.append(build_no).append("\t").append(city).append("\t").append("高级公寓楼").append("\t").append("A").append("\n")
        case _=>result.append(build_no).append("\t").append(city).append("\t").append("商务楼").append("\t").append("B").append("\n")
      }

    }
    result.toString()
  }

  def defineLevel(city:String,ttype:String,array:ArrayBuffer[(Int, Int)]):String ={
    val result = new StringBuilder();
    if(array.map(_._2).toSet[Int].size<4){
      for((build_no:Int,mark:Int)<-array){
        result.append(build_no).append("\t").append(city).append("\t").append(ttype).append("\t").append("B").append("\n")
      }
    }else{
      val sort=array.sortWith(_._2<_._2)
      val level=array.map(_._2).toSet[Int].toArray[Int].sortWith(_<_)
      val interval = level.length/4.0d
      val map = HashMap[Int,String]()
      for(i<-0 until level.length){
        if(i<interval*1){
          map.+=(level(i)->"A")
        }
        if(i>=interval*1 && i<interval*2){
          map.+=(level(i)->"B")
        }
        if(i>=interval*2 && i<interval*3){
          map.+=(level(i)->"C")
        }
        if(i>=interval*3 && i<level.length){
          map.+=(level(i)->"D")
        }
      }
      for(kksl<-sort){
        result.append(kksl._1).append("\t").append(city).append("\t").append(ttype).append("\t").append(map.getOrElse(kksl._2,"B")).append("\n")
      }
    }
    result.toString()
  }

  def isIntByRegex(s : String) = {
    val pattern = """^(\d+)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }
}
