package com.example.hikigai.ui.favorites

import androidx.lifecycle.*
import com.example.hikigai.data.model.Photo
import com.example.hikigai.data.repository.PhotoRepository
import kotlinx.coroutines.launch

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/

class FavoritesViewModel(private val repository: PhotoRepository) : ViewModel() {
    val favoritePhotos: LiveData<List<Photo>> = repository.getFavoritePhotos().asLiveData()

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            repository.toggleFavorite(photo)
        }
    }
}

class FavoritesViewModelFactory(private val repository: PhotoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
