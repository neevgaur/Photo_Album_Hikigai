package com.example.hikigai.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.hikigai.data.local.PhotoDao
import com.example.hikigai.data.model.Photo
import retrofit2.HttpException
import java.io.IOException
import kotlin.random.Random

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/
@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val photoDao: PhotoDao,
    private val photoApiService: PhotoApiService
) : RemoteMediator<Int, Photo>() {

    override suspend fun initialize(): InitializeAction {
        return if (photoDao.getPhotoCount() > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Photo>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = false)
                    (lastItem.id / state.config.pageSize) + 1
                }
            }

            val response = photoApiService.getPhotos(page, state.config.pageSize)

            if (response.isSuccessful) {
                val photos = response.body() ?: emptyList()

                // Transform the photos to include better titles and sections
                // In a real app, you'd get this from the API
                val transformedPhotos = photos.map { photo ->
                    val sectionNumber = photo.id / 100 + 1
                    Photo(
                        id = photo.id,
                        title = "Photo ${photo.id} - ${getRandomPhotoTitle()}",
                        imageUrl = "https://picsum.photos/id/${photo.id % 1000}/400/300",
                        section = "Section $sectionNumber",
                        isFavorite = false
                    )
                }

                if (loadType == LoadType.REFRESH) {
                    photoDao.clearAll()
                }

                photoDao.insertAll(transformedPhotos)

                MediatorResult.Success(endOfPaginationReached = photos.isEmpty())
            } else {
                MediatorResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun getRandomPhotoTitle(): String {
        val titles = listOf(
            "Sunset view", "City landscape", "Mountain peak", "Ocean waves",
            "Forest path", "Desert dunes", "Autumn leaves", "Winter snow",
            "Spring flowers", "Summer beach", "Wildlife", "Architecture",
            "Street life", "Night sky", "Portrait", "Food photography"
        )
        return titles[Random.nextInt(titles.size)]
    }
}