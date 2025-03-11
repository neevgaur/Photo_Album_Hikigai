package com.example.hikigai

import android.app.Application
import com.example.hikigai.data.local.PhotoDatabase
import com.example.hikigai.data.repository.PhotoRepository

/*
File :
Description :

Author : Neev Gaur

Todo >
*/
class PhotoAlbumApplication : Application() {
    val database by lazy { PhotoDatabase.getDatabase(this) }
    val repository by lazy { PhotoRepository(database.photoDao()) }
}