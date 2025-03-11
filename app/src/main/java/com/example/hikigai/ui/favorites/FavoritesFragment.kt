package com.example.hikigai.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hikigai.PhotoAlbumApplication
import com.example.hikigai.R
import com.example.hikigai.databinding.FragmentFavoritesBinding
import com.example.hikigai.ui.adapters.FavoritePhotoAdapter
import com.example.hikigai.ui.adapters.PhotoAdapter
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory((requireActivity().application as PhotoAlbumApplication).repository)
    }

    private lateinit var adapter: FavoritePhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()
    }

    private fun setupRecyclerView() {
        adapter = FavoritePhotoAdapter { photo ->
            viewModel.toggleFavorite(photo)
            Snackbar.make(
                binding.root,
                R.string.removed_from_favorites,
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@FavoritesFragment.adapter
        }
    }

    private fun observeFavorites() {
        viewModel.favoritePhotos.observe(viewLifecycleOwner) { photos ->
            adapter.submitList(photos)
            binding.emptyView.isVisible = photos.isEmpty()
            binding.recyclerView.isVisible = photos.isNotEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}