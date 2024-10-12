package com.fundamentalandroid.dicodingevents.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fundamentalandroid.dicodingevents.data.respons.EventResponse
import com.fundamentalandroid.dicodingevents.data.retrofit.ApiConfig
import com.fundamentalandroid.dicodingevents.databinding.FragmentHomeBinding
import com.fundamentalandroid.dicodingevents.ui.EventAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d("HomeFragment", "onCreateView called, binding initialized")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upcomingAdapter = EventAdapter { event ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailEvent(event)
            findNavController().navigate(action)
            Log.d("HomeFragment", "Clicked on upcoming event: ${event.id}")
        }
        binding.recyclerUpcoming.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        finishedAdapter = EventAdapter { event ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailEvent(event)
            findNavController().navigate(action)
            Log.d("HomeFragment", "Clicked on finished event: ${event.id}")
        }
        binding.recyclerFinished.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }

        loadEvents()
    }

    private fun loadEvents() {
        showLoading(true)

        val apiService = ApiConfig.getApiService()

        apiService.getEvents(1).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val upcomingEvents = response.body()?.listEvents?.take(5)
                    upcomingAdapter.submitList(upcomingEvents)
                } else {
                    Log.e("HomeFragment", "Error loading upcoming events: ${response.message()}")
                    showError("Gagal memuat acara mendatang: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                showLoading(false)
                Log.e("HomeFragment", "Error loading upcoming events", t)
                showError("Kesalahan jaringan: ${t.message ?: "Gagal memuat."}")
            }
        })

        apiService.getEvents(0).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val finishedEvents = response.body()?.listEvents?.take(5)
                    finishedAdapter.submitList(finishedEvents)
                } else {
                    Log.e("HomeFragment", "Error loading finished events: ${response.message()}")
                    showError("Gagal memuat acara selesai: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e("HomeFragment", "Error loading finished events", t)
                showError("Kesalahan jaringan: ${t.message ?: "Gagal memuat."}")
            }
        })
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
