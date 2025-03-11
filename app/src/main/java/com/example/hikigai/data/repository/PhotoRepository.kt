package com.example.hikigai.data.repository

import androidx.paging.*
import com.example.hikigai.data.local.PhotoDao
import com.example.hikigai.data.model.Photo
import com.example.hikigai.data.remote.PhotoApiService
import com.example.hikigai.data.remote.PhotoRemoteMediator
import kotlinx.coroutines.flow.Flow

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/
class PhotoRepository(private val photoDao: PhotoDao) {
    private val photoApiService = PhotoApiService.create()

    @OptIn(ExperimentalPagingApi::class)
    fun getPhotoStream(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 100
            ),
            remoteMediator = PhotoRemoteMediator(photoDao, photoApiService),
            pagingSourceFactory = { photoDao.getAllPhotosPaged() }
        ).flow
    }

    fun getFavoritePhotos(): Flow<List<Photo>> {
        return photoDao.getFavoritePhotos()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun searchPhotos(query: String): Flow<PagingData<Photo>> {
        val searchQuery = "%$query%"
        val searchId = query.toIntOrNull()

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { photoDao.searchPhotos(searchQuery, searchId) }
        ).flow
    }

    suspend fun toggleFavorite(photo: Photo) {
        val updatedPhoto = photo.copy(isFavorite = !photo.isFavorite)
        photoDao.updatePhoto(updatedPhoto)
    }
}