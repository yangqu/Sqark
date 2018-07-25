package com.fm.etl

import scala.io.Source

object MyDistrict {

/*  def main(args:Array[String]): Unit = {
    val file1 = Source.fromFile("E:\\areamap.txt", "utf-8")
    for (line <- file1.getLines()) {
      val array = line.split("\t")(1)
      val zk = array.split("\\],\\[")
      var buffk = scala.collection.mutable.ArrayBuffer[String]()
      //println(zk.length)

      for(oo<-zk){
        val latlng=oo.replaceAll("\\[","").replaceAll("\\]","")
        /*val lat=latlng.split(",")(0).toDouble
        val lng = latlng.split(",")(1).toDouble*/
        buffk.+=(latlng)
      }
      //println(buffk.length)
      var kl="";
      val ioio=buffk.length
      for(i<-0 until ioio){
        i match {
          case 0=>kl=buffk.mkString("[","],[","]")+","
          //case ioio=>kl=kl+buffk.mkString("[","],[","]")
          case _ =>kl=kl+buffk.mkString("[","],[","]")+","
        }
        buffk.remove(0,1)

      }

      println(kl)
    }
  }*/

  def takeNumber(num:Int):Int={
    if(num/4<3){
      3
    }else{
      5
    }
  }
 def main(args:Array[String]): Unit = {
    val file1 = Source.fromFile("E:\\areamap.txt", "utf-8")
    for (line <- file1.getLines()) {
      val array = line.split("\t")(1)
      val zk = array.split("\\],\\[")
      var buffk = scala.collection.mutable.ArrayBuffer[(Double,Double)]()
      for(oo<-zk){
        val latlng=oo.replaceAll("\\[","").replaceAll("\\]","")
        val lat=latlng.split(",")(0).toDouble
        val lng = latlng.split(",")(1).toDouble
        buffk.+=((lat,lng))
      }
      val left=buffk.sortWith(_._1<_._1).take(takeNumber(buffk.length)).toSet.toArray.sortWith(_._1<_._1).sortWith(_._2>_._2)
      val right=buffk.sortWith(_._1>_._1).take(takeNumber(buffk.length)).toSet.toArray.sortWith(_._1>_._1).sortWith(_._2<_._2)
      val top=buffk.sortWith(_._2>_._2).take(takeNumber(buffk.length)).toSet.toArray.sortWith(_._2>_._2).sortWith(_._1>_._1)
      val bottom=buffk.sortWith(_._2<_._2).take(takeNumber(buffk.length)).toSet.toArray.sortWith(_._2<_._2).sortWith(_._1<_._1)
   /*   val l="["+left(0)._1+","+left(0)._2+"]"+","+"["+left(1)._1+","+left(1)._2+"]"
      val b="["+bottom(0)._1+","+bottom(0)._2+"]"+","+"["+bottom(1)._1+","+bottom(1)._2+"]"
      val r="["+right(0)._1+","+right(0)._2+"]"+","+"["+right(1)._1+","+right(1)._2+"]"
      val t="["+top(0)._1+","+top(0)._2+"]"+","+"["+top(1)._1+","+top(1)._2+"]"*/

      val l=left.mkString("[","],[","]").replaceAll("\\)","").replaceAll("\\(","")
      val b=bottom.mkString("[","],[","]").replaceAll("\\)","").replaceAll("\\(","")
      val r=right.mkString("[","],[","]").replaceAll("\\)","").replaceAll("\\(","")
      val t=top.mkString("[","],[","]").replaceAll("\\)","").replaceAll("\\(","")
      println(line.split("\t")(0)+"\t"+l+","+b+","+r+","+t)

    }
  }
}
