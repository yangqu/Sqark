package com.fm.etl

import com.`trait`.CommonTrait

object MyData extends CommonTrait{
  case class Business(id:Int,name:String, city:String , city_id:String,district:String ,address:String,
                      business:String,business1:String,business2:String,business3:String,lon:Double,lat:Double,
                      isGD:Boolean,lon_g:Double,lat_g:Double)
  case class BusinessShit(city:String,name:String, screeen_kind:String , id:Int,address:String,	project_kind:String,
                          project_level:String,mark:Int,price_avg:Int,price_day:Double,business_big:String)
  case class Company(city:String,id:Int,	project_kind1:String,project_kind2:String,name:String,address:String,
                     department:String,	depeople:String,building:String,floor:String,company:String,trade:String)
  case class Company_Stat(id:Int,floor_count:Int,company_count:Int,trade_count:Int,top_trade:String)
  case class Buildings(id:Int,name:String, city:String , city_id:String,district:String ,address:String,
                       business:String,lon:Double,lat:Double,
                       isGD:Boolean,lon_g:Double,lat_g:Double, screeen_kind:String ,project_kind:String,project_level:String,
                       mark:Int,price_avg:Int,price_day:Double,business_big:String,floor_count:Int,company_count:Int,trade_count:Int,
                       top_trade:String)
/*  case class Level(city:String,id:String,project_kind1:String,project_kind2:String,name:String,address:String,
                     department:String,	depeople:String,building:String,floor:String,company:String,trade:String)*/
  def doJob(): Unit = {
    val business = sc.textFile("/user/root/buildings/business.txt")
    val busishit = sc.textFile("/user/root/buildings/busishit.txt")
    //val level = sc.textFile("/user/root/level.txt")
    val company = sc.textFile("/user/root/buildings/company.txt")
    val business_format = business.filter(_.split("\t").length >= 14).map(line => {
      val temp = line.split("\t")
      Business(convertToInt(temp(0)), temp(1), temp(2), temp(3), temp(4), temp(5),
        temp(6), temp(7), temp(8), temp(9), convertToDouble(temp(10)), convertToDouble(temp(11)),
        convertToDBoolean(temp(12)), if (temp(13).split(",").length >= 2) convertToDouble(temp(13).split(",")(0)) else -1,
        if (temp(13).split(",").length >= 2) convertToDouble(temp(13).split(",")(1)) else -1)
    })
    val busishit_format = busishit.filter(_.split("\t").length >= 10).map(line => {
      val temp = line.split("\t")
      BusinessShit(temp(0), temp(1), temp(2), convertToInt(temp(3)), temp(4), temp(5),
        temp(6), convertToInt(temp(7)), convertToInt(temp(8)), convertToDouble(temp(9)),
        if (temp.length >= 11) temp(10) else "")
    })

    val company_format = company.filter(_.split("\t").length >= 11).map(line => {
      val temp = line.split("\t")
      Company(temp(0), convertToInt(temp(1)), temp(2), temp(3), temp(4), temp(5),
        temp(6), temp(7), temp(8), temp(9), temp(10),
        if (temp.length >= 12) temp(11) else "")
    })
    val business_join = business_format.map(line => (line.id, line))
    val busishit_join = busishit_format.map(line => (line.id, line))
    val company_join = company_format.map(line => (line.id, line))
    val union_id = (business_format.map(_.id) ++ busishit_format.map(_.id) ++ company_format.map(_.id)).distinct().map(line => (line, 1))
    val first =
      union_id
        .leftOuterJoin(business_join)
        .map(line => {
          val id = line._1
          line._2._2 match {
            case Some(x) => (id, x)
            case None => (id, Business(id, "", "", "", "", "", "", "", "", "", -1d, -1d, false, -1d, -1d))
          }
        })
        .leftOuterJoin(busishit_join)
        .map(line => {
          val id = line._1
          val business = line._2._1
          line._2._2 match {
            case Some(x) => (id, (business, x))
            case None => (id, (business, BusinessShit("", "", "", id, "", "", "", -1, -1, -1d, "")))
          }
        })
    val company_rdd =
      company_join.groupByKey().map(line => {
        val id = line._1
        val iter = line._2.toArray
        val company_count = iter.map(_.company).toSet.size
        val trade_count = iter.map(_.trade).toSet.size
        val floor_count = iter.map(_.floor).toSet.size
        val trade_kind_count = iter.map(_.trade)
        var map: scala.collection.mutable.Map[String, Int] = scala.collection.mutable.Map()
        for (trade: String <- trade_kind_count) {
          map.put(trade, map.getOrElse(trade, 0) + 1)
        }
        var sort_array = map.toArray.sortWith(_._2 > _._2)
        var trade_str = ""
        for (i: Int <- 0 to 2) {
          if (sort_array.length > i) {
            trade_str += (sort_array(i)._1 + ":" + sort_array(i)._2 + ";")
          }
          else {
            trade_str += ""
          }
        }
        Company_Stat(id, floor_count, company_count, trade_count, trade_str)
      }).map(line => (line.id, line))

    val second = first
      .leftOuterJoin(company_rdd)
      .map(line => {
        val id = line._1
        val all = line._2._1
        line._2._2 match {
          case Some(x) => (id, all._1, all._2, x)
          case None => (id, all._1, all._2, Company_Stat(id, -1, -1, -1, ""))
        }
      })
    /*    import spark.implicits._
        second.map(line=>(line._1,line._2,line._3,line._4)).map(line=>{
            line._2 match{
              case x:Business=>x.name
              case (_,x:Business)=>x.name
            }
          }).collect*/
/*    second.map(line => {
      (line._2.id, line._2.name, line._2.city, line._2.city_id, line._2.district,
        line._2.address, line._2.business, line._2.lon, line._2.lat, line._2.isGD, line._2.lon_g,
        line._2.lat_g, line._3.city, line._3.screen_kind, line._3.project_kind, line._3.project_level,
        line._3.mark, line._3.price_avg, line._3.price_day, line._3.business_big, line._4.floor_count,
        line._4.company_count, line._4.trade_count, line._4.top_trade)
    })*/
    second.map(line=>{
      line._2.id+"\t"+line._2.name+"\t"+line._2.city+"\t"+line._2.city_id+"\t"+line._2.district+"\t"+line._2.address+"\t"+line._2.business+"\t"+line._2.lon+"\t"+line._2.lat+"\t"+line._2.isGD+"\t"+line._2.lon_g+"\t"+line._2.lat_g+"\t"+line._3.city+"\t"+line._3.screeen_kind+"\t"+line._3.project_kind+"\t"+line._3.project_level+"\t"+line._3.mark+"\t"+line._3.price_avg+"\t"+line._3.price_day+"\t"+line._3.business_big+"\t"+line._4.floor_count+"\t"+line._4.company_count+"\t"+line._4.trade_count+"\t"+line._4.top_trade
    }).saveAsTextFile("/user/root/buildings/result1.txt")
    val table=second.map(line => {
      Buildings(line._2.id, line._2.name, line._2.city, line._2.city_id, line._2.district,
        line._2.address, line._2.business, line._2.lon, line._2.lat, line._2.isGD, line._2.lon_g,
        line._2.lat_g, line._3.screeen_kind, line._3.project_kind, line._3.project_level,
        line._3.mark, line._3.price_avg, line._3.price_day, line._3.business_big, line._4.floor_count,
        line._4.company_count, line._4.trade_count, line._4.top_trade)
    })
   /* val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)
    import hiveContext.implicits._
    hiveContext.sql("use DataBaseName")*/
  }

  def convertToInt(number:String):Int={
    try{
      number.toInt
    }catch{
      case e: Exception => println(e);-1
    }
  }
  def convertToDouble(number:String):Double={
    try{
      number.toDouble
    }catch{
      case e: Exception => println(e);-1d
    }
  }

  def convertToDBoolean(number:String):Boolean={
    try{
      number.toBoolean
    }catch{
      case e: Exception => println(e);false
    }
  }
  /*    val business_join=business_format.map(line=>(line.id,1)).reduceByKey(_+_).map(line=>(line._1,line._2))
      val busishit_join=busishit_format.map(line=>(line.id,1)).reduceByKey(_+_).map(line=>(line._1,line._2))
      val bcompany_join=company_format.map(line=>(line.id,1)).reduceByKey(_+_).map(line=>(line._1,line._2))
      business_join.join(busishit_join).join(bcompany_join).distinct().count*/
}
