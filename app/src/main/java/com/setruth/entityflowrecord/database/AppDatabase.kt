package com.setruth.entityflowrecord.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.setruth.entityflowrecord.data.entity.FlowRecordEntity
import com.setruth.entityflowrecord.database.dao.FlowRecordDao
import com.setruth.entityflowrecord.util.Converters

@Database(entities = [FlowRecordEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flowRecordDao(): FlowRecordDao
}