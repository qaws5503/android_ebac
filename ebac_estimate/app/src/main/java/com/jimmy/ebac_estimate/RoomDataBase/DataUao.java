package com.jimmy.ebac_estimate.RoomDataBase;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Calendar;
import java.util.List;

@Dao
public interface DataUao {

    String tableName = "MyTable";
    /**=======================================================================================*/
    /**簡易新增所有資料的方法*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)//預設萬一執行出錯怎麼辦，REPLACE為覆蓋
    void insertData(Data myData);

    /**複雜(?)新增所有資料的方法*/
    @Query("INSERT INTO "+tableName+"(drinkName, drinkML, drinkAbv, SD, startDrinkTime, soberDrinkTime) VALUES(:drinkName, :drinkML, :drinkAbv, :SD, :startDrinkTime, :soberDrinkTime)")
    void insertData(String drinkName, float drinkML, float drinkAbv, float SD, String startDrinkTime, String soberDrinkTime);

    /**=======================================================================================*/
    /**撈取全部資料*/
    @Query("SELECT * FROM " + tableName)
    List<Data> displayAll();

    /**撈取全部資料*/
    @Query("SELECT * FROM " + tableName +" ORDER BY startDrinkTime DESC")
    List<Data> displayAllByTime();

    /**撈取某個名字的相關資料*/
    @Query("SELECT * FROM " + tableName +" WHERE drinkName = :drinkName")
    List<Data> findDataByDrinkName(String drinkName);

    @Query("SELECT * FROM " + tableName +" WHERE id = :id")
    Data findDataById(int id);

    /**=======================================================================================*/
    /**簡易更新資料的方法*/
    @Update
    void updateData(Data myData);

    /**複雜(?)更新資料的方法*/
    @Query("UPDATE "+tableName+" SET drinkName = :drinkName,drinkML=:drinkML,drinkAbv=:drinkAbv,SD=:SD,startDrinkTime=:startDrinkTime,soberDrinkTime=:soberDrinkTime WHERE id = :id" )
    void updateData(int id,String drinkName,float drinkML, float drinkAbv, float SD, String startDrinkTime, String soberDrinkTime);

    /**=======================================================================================*/
    /**簡單刪除資料的方法*/
    @Delete
    void deleteData(Data myData);

    /**複雜(?)刪除資料的方法*/
    @Query("DELETE  FROM " + tableName + " WHERE id = :id")
    void deleteData(int id);

    @Query("DELETE FROM "+ tableName)
    void delete();

    /**抽取最後一筆資料*/
    @Query("SELECT * FROM " + tableName +" ORDER BY id DESC LIMIT 1")
    Data findLastData();

    /**抽取當時刻未消化酒杯*/
    @Query("SELECT * FROM " + tableName +" WHERE startDrinkTime < :time AND soberDrinkTime > :time")
    List<Data> findBetweenDrink(Calendar time);


    @Query("SELECT * FROM " + tableName +" WHERE startDrinkTime < :startTime AND startDrinkTime > :endTime")
    List<Data> findSpecificDateDrink(Calendar startTime,Calendar endTime);

}

