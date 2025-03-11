package com.example.hikigai.ui.adapters

import com.example.hikigai.data.model.Photo

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/

sealed class PhotoAdapterItem {
    data class PhotoItem(val photo: Photo) : PhotoAdapterItem()
    data class SectionHeader(val section: String) : PhotoAdapterItem()
}