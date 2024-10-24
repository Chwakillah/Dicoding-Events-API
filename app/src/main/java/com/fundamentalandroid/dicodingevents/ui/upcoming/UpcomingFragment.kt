package com.fundamentalandroid.dicodingevents.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.fundamentalandroid.dicodingevents.data.remote.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.databinding.FragmentUpcomingBinding
import com.fundamentalandroid.dicodingevents.ui.adapter.EventAdapter

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UpcomingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[UpcomingViewModel::class.java]

        val layoutManager = LinearLayoutManager(context)
        binding.recycleUpcoming.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.recycleUpcoming.addItemDecoration(itemDecoration)

        viewModel.events.observe(viewLifecycleOwner) { listEvents ->
            setEventData(listEvents)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { showError(it) }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.findEvents()
    }

    private fun setEventData(listEvents: List<ListEventsItem>) {
        val adapter = EventAdapter { event ->
            val action = UpcomingFragmentDirections.actionUpcomingFragmentToDetailEvent(event)
            findNavController().navigate(action)
        }
        adapter.submitList(listEvents)
        binding.recycleUpcoming.adapter = adapter
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
