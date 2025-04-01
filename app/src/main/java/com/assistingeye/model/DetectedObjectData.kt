package com.assistingeye.model

import android.graphics.RectF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetectedObjectData(
    val confidence: Float,
    val boundingBox: RectF,
    val name: String
): Parcelable
