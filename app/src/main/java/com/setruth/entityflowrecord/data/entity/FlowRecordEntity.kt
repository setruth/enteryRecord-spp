package com.setruth.entityflowrecord.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.setruth.entityflowrecord.data.model.FLowType
import com.setruth.entityflowrecord.data.model.FlowBaseRecord
import java.time.LocalDateTime

@Entity
data class FlowRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    override val timestamp: LocalDateTime,
    @ColumnInfo
    override val type: FLowType,
    @ColumnInfo
    override val changeAfterTotalPeople: Int
) : FlowBaseRecord