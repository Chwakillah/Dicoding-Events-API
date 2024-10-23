package com.fundamentalandroid.dicodingevents.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fundamentalandroid.dicodingevents.databinding.FragmentHomeBinding
import com.fundamentalandroid.dicodingevents.ui.EventAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setupAdapters()
        setupSearchView()
        observeViewModel()
        viewModel.loadEvents()
    }

    private fun setupSearchView() {
        binding.searchEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                true
            } else {
                false
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            private var searchEvents: Job? = null // Changed from searchJob to searchEvents

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchEvents?.cancel() // Use the updated variable name
                searchEvents = lifecycleScope.launch {
                    delay(500) // Debounce delay
                    performSearch(s.toString())
                }
            }
        })
    }

    private fun performSearch(query: String) {
        viewModel.searchEvents(query)

        if (query.isEmpty()) {
            binding.upcomingTitle.visibility = View.VISIBLE
            binding.finishedTitle.visibility = View.VISIBLE
        } else {
            binding.upcomingTitle.visibility = View.GONE
            binding.finishedTitle.visibility = View.GONE
        }
    }

    private fun setupAdapters() {
        upcomingAdapter = EventAdapter { event ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailEvent(event)
            findNavController().navigate(action)
        }
        binding.recyclerUpcoming.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        finishedAdapter = EventAdapter { event ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailEvent(event)
            findNavController().navigate(action)
        }
        binding.recyclerFinished.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            upcomingAdapter.submitList(events)
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { showError(it) }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.ProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
