package com.setruth.entityflowrecord.data.model

import java.time.LocalDateTime

interface  FlowBaseRecord{
    val timestamp: LocalDateTime
    val type: FLowType
    val changeAfterTotalPeople: Int
}
