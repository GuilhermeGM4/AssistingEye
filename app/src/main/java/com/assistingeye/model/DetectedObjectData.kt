package com.assistingeye.model

import android.graphics.RectF

data class DetectedObjectData(
    val confidence: Float,
    val boundingBox: RectF,
    val name: String
)
