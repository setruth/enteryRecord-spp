package com.setruth.entityflowrecord.data.model


data class LightInfoItem(
    val status: TipLight,
    val title: String,
    val description: String,
)
data class LightRange(
    val warn: Int=0,
    val err: Int=0,
)