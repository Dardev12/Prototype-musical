package com.dardev.prototypemusicallly.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Music(
    val Titre:String?,
    val Artist: String?,
    val musicUri:String?,
    val duree:String?
):Parcelable