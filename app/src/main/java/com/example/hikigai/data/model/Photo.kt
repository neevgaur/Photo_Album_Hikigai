package com.example.hikigai.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


/*
File :
Description :

Author : Neev Gaur

Todo >
*/
@Parcelize
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val section: String,
    var isFavorite: Boolean = false
) : Parcelable
