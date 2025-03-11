package com.example.hikigai.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.example.hikigai.data.model.Photo
import kotlinx.coroutines.flow.Flow

/*
File :
Description :

Author : Neev Gaur

Todo >
*/
@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY section, id")
    fun getAllPhotosPaged(): PagingSource<Int, Photo>

    @Query("SELECT * FROM photos WHERE isFavorite = 1 ORDER BY section, id")
    fun getFavoritePhotos(): Flow<List<Photo>>

    @Query("SELECT * FROM photos WHERE title LIKE :searchQuery OR id = :searchId ORDER BY section, id")
    fun searchPhotos(searchQuery: String, searchId: Int? = null): PagingSource<Int, Photo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<Photo>)

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Query("SELECT COUNT(*) FROM photos")
    suspend fun getPhotoCount(): Int

    @Query("DELETE FROM photos")
    suspend fun clearAll()
}