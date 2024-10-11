package com.fundamentalandroid.dicodingevents.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        upcomingAdapter = EventAdapter()
        binding.recyclerUpcoming.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        finishedAdapter = EventAdapter()
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
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                showLoading(false)
            }
        })

        apiService.getEvents(0).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val finishedEvents = response.body()?.listEvents?.take(5)
                    finishedAdapter.submitList(finishedEvents)
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.ProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}