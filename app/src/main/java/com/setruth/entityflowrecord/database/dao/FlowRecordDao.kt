package com.setruth.entityflowrecord.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.setruth.entityflowrecord.data.entity.FlowRecordEntity
import com.setruth.entityflowrecord.data.model.FLowType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FlowRecordDao {

    // 插入单条记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flowRecord: FlowRecordEntity): Long

    // 批量插入记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flowRecords: List<FlowRecordEntity>): List<Long>

    // 更新记录
    @Update
    suspend fun update(flowRecord: FlowRecordEntity)

    // 删除记录
    @Delete
    suspend fun delete(flowRecord: FlowRecordEntity)

    // 根据ID删除记录
    @Query("DELETE FROM FlowRecordEntity WHERE id = :id")
    suspend fun deleteById(id: Int)

    // 删除所有记录
    @Query("DELETE FROM FlowRecordEntity")
    suspend fun deleteAll()

    // 根据ID查询记录
    @Query("SELECT * FROM FlowRecordEntity WHERE id = :id")
    suspend fun getById(id: Int): FlowRecordEntity?

    // 获取所有记录（按时间倒序）
    @Query("SELECT * FROM FlowRecordEntity ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<FlowRecordEntity>>

    // 获取所有记录（同步方法）
    @Query("SELECT * FROM FlowRecordEntity ORDER BY timestamp DESC")
    suspend fun getAllRecordsSync(): List<FlowRecordEntity>

    // 根据流动类型查询记录
    @Query("SELECT * FROM FlowRecordEntity WHERE type = :flowType ORDER BY timestamp DESC")
    fun getRecordsByType(flowType: FLowType): Flow<List<FlowRecordEntity>>

    // 根据时间范围查询记录
    @Query("SELECT * FROM FlowRecordEntity WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getRecordsByTimeRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<List<FlowRecordEntity>>

    // 获取最新的N条记录
    @Query("SELECT * FROM FlowRecordEntity ORDER BY timestamp DESC LIMIT :limit")
    fun getLatestRecords(limit: Int): Flow<List<FlowRecordEntity>>

    // 获取最新的一条记录
    @Query("SELECT * FROM FlowRecordEntity ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestRecord(): FlowRecordEntity?

    // 获取记录总数
    @Query("SELECT COUNT(*) FROM FlowRecordEntity")
    suspend fun getRecordCount(): Int

    // 根据流动类型获取记录数量
    @Query("SELECT COUNT(*) FROM FlowRecordEntity WHERE type = :flowType")
    suspend fun getRecordCountByType(flowType: FLowType): Int

    // 获取指定日期的记录
    @Query("SELECT * FROM FlowRecordEntity WHERE DATE(timestamp) = DATE(:date) ORDER BY timestamp DESC")
    fun getRecordsByDate(date: LocalDateTime): Flow<List<FlowRecordEntity>>

    // 获取当前总人数（最新记录的人数）
    @Query("SELECT changeAfterTotalPeople FROM FlowRecordEntity ORDER BY timestamp DESC LIMIT 1")
    suspend fun getCurrentTotalPeople(): Int?


    // 清理指定日期之前的记录
    @Query("DELETE FROM FlowRecordEntity WHERE timestamp < :beforeDate")
    suspend fun deleteRecordsBefore(beforeDate: LocalDateTime): Int
}