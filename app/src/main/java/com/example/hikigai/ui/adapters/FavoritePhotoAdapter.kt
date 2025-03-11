package com.example.hikigai.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hikigai.R
import com.example.hikigai.data.model.Photo
import com.example.hikigai.databinding.ItemPhotoBinding

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/

class FavoritePhotoAdapter(private val onFavoriteClicked: (Photo) -> Unit) :
    ListAdapter<Photo, FavoritePhotoAdapter.FavoritePhotoViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoritePhotoViewHolder(binding, onFavoriteClicked)
    }

    override fun onBindViewHolder(holder: FavoritePhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FavoritePhotoViewHolder(
        private val binding: ItemPhotoBinding,
        private val onFavoriteClicked: (Photo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.apply {
                titleText.text = photo.title
                idText.text = "ID: ${photo.id}"

                Glide.with(imageView)
                    .load(photo.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView)

                favoriteButton.setImageResource(R.drawable.ic_favorite_filled)

                favoriteButton.setOnClickListener {
                    onFavoriteClicked(photo)
                }
            }
        }
    }
}