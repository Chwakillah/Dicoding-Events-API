package com.fundamentalandroid.dicodingevents.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fundamentalandroid.dicodingevents.data.remote.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.databinding.FragmentFavoriteBinding
import com.fundamentalandroid.dicodingevents.ui.adapter.EventAdapter
import com.fundamentalandroid.dicodingevents.helper.FavoriteViewModelFactory

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteViewModel

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewModel()
        setupAdapter()
        setupRecyclerView()
        observeFavorites()
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory.getInstance(requireActivity().application)
        )[FavoriteViewModel::class.java]
    }

    private fun setupAdapter() {
        eventAdapter = EventAdapter { event ->
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToDetailEvent(event)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        binding.recycleFavorite.layoutManager = LinearLayoutManager(context)
        binding.recycleFavorite.setHasFixedSize(true)
        binding.recycleFavorite.adapter = eventAdapter
    }

    private fun observeFavorites() {
        showLoading(true)

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showLoading(false)
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            showLoading(false)

            try {
                val items = favorites.map { favorite ->
                    ListEventsItem(
                        id = favorite.id,
                        name = favorite.name ?: "",
                        mediaCover = favorite.mediaCover ?: "",
                        summary = favorite.summary ?: "",
                        registrants = favorite.registrants ?: 0,
                        imageLogo = favorite.imageLogo ?: "",
                        link = favorite.link ?: "",
                        description = favorite.description ?: "",
                        ownerName = favorite.ownerName ?: "",
                        cityName = favorite.cityName ?: "",
                        quota = favorite.quota ?: 0,
                        beginTime = favorite.beginTime ?: "",
                        endTime = favorite.endTime ?: "",
                        category = favorite.category ?: ""
                    )
                }
                eventAdapter.submitList(items)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Kesalahan saat memuat data.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.ProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
