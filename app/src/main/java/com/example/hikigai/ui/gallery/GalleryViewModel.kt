package com.example.hikigai.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.hikigai.data.model.Photo
import com.example.hikigai.data.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/

class GalleryViewModel(private val repository: PhotoRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")

    val photosFlow: Flow<PagingData<Photo>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.getPhotoStream()
            } else {
                repository.searchPhotos(query)
            }
        }
        .cachedIn(viewModelScope)

    fun searchPhotos(query: String): Flow<PagingData<Photo>> {
        _searchQuery.value = query
        return repository.searchPhotos(query).cachedIn(viewModelScope)
    }

    fun resetSearch() {
        _searchQuery.value = ""
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            repository.toggleFavorite(photo)
        }
    }
}

class GalleryViewModelFactory(private val repository: PhotoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalleryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}