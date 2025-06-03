package com.setruth.entityflowrecord.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.setruth.entityflowrecord.data.entity.FlowRecordEntity
import com.setruth.entityflowrecord.database.dao.FlowRecordDao

@Database(entities = [FlowRecordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flowRecordDao(): FlowRecordDao
}