package com.example.hikigai.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hikigai.R
import com.example.hikigai.data.model.Photo
import com.example.hikigai.databinding.ItemPhotoBinding
import com.example.hikigai.databinding.ItemSectionHeaderBinding

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/

class PhotoAdapter(private val onFavoriteClicked: (Photo) -> Unit) :
    PagingDataAdapter<PhotoAdapterItem, RecyclerView.ViewHolder>(PHOTO_COMPARATOR) {

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<PhotoAdapterItem>() {
            override fun areItemsTheSame(oldItem: PhotoAdapterItem, newItem: PhotoAdapterItem): Boolean {
                return when {
                    oldItem is PhotoAdapterItem.PhotoItem && newItem is PhotoAdapterItem.PhotoItem ->
                        oldItem.photo.id == newItem.photo.id
                    oldItem is PhotoAdapterItem.SectionHeader && newItem is PhotoAdapterItem.SectionHeader ->
                        oldItem.section == newItem.section
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: PhotoAdapterItem, newItem: PhotoAdapterItem): Boolean {
                return when {
                    oldItem is PhotoAdapterItem.PhotoItem && newItem is PhotoAdapterItem.PhotoItem ->
                        oldItem.photo == newItem.photo
                    oldItem is PhotoAdapterItem.SectionHeader && newItem is PhotoAdapterItem.SectionHeader ->
                        oldItem.section == newItem.section
                    else -> false
                }
            }
        }

        private const val VIEW_TYPE_PHOTO = 0
        private const val VIEW_TYPE_SECTION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PhotoAdapterItem.PhotoItem -> VIEW_TYPE_PHOTO
            is PhotoAdapterItem.SectionHeader -> VIEW_TYPE_SECTION
            null -> VIEW_TYPE_PHOTO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PHOTO -> {
                val binding = ItemPhotoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PhotoViewHolder(binding, onFavoriteClicked)
            }
            VIEW_TYPE_SECTION -> {
                val binding = ItemSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SectionViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is PhotoViewHolder -> {
                item?.let {
                    if (it is PhotoAdapterItem.PhotoItem) {
                        holder.bind(it.photo)
                    }
                }
            }
            is SectionViewHolder -> {
                item?.let {
                    if (it is PhotoAdapterItem.SectionHeader) {
                        holder.bind(it.section)
                    }
                }
            }
        }
    }

    class PhotoViewHolder(
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

                favoriteButton.setImageResource(
                    if (photo.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )

                favoriteButton.setOnClickListener {
                    onFavoriteClicked(photo)
                }
            }
        }
    }

    class SectionViewHolder(private val binding: ItemSectionHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: String) {
            binding.sectionTitle.text = section
        }
    }
}