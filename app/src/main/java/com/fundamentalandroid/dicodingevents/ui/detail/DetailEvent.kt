package com.fundamentalandroid.dicodingevents.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fundamentalandroid.dicodingevents.R
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity
import com.fundamentalandroid.dicodingevents.data.remote.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.databinding.FragmentDetailEventBinding
import com.fundamentalandroid.dicodingevents.helper.FavoriteViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailEvent : Fragment() {

    private var _binding: FragmentDetailEventBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    private val args: DetailEventArgs by navArgs()
    private lateinit var viewModel: DetailEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailEventBinding.inflate(inflater, container, false)
        hideBottomNavigationView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory.getInstance(requireActivity().application)
        )[DetailEventViewModel::class.java]

        setupObservers()

        viewModel.setEventItem(args.eventItem)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = args.eventItem.name
            setDisplayHomeAsUpEnabled(true)
        }

        setHasOptionsMenu(true)

        binding.fab.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun setupObservers() {
        viewModel.eventItem.observe(viewLifecycleOwner) { eventItem ->
            bindEventData(eventItem)
        }

        viewModel.isFavorited.observe(viewLifecycleOwner) { isFavorited ->
            updateFavoriteIcon(isFavorited)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { showError(it) }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            ProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            eventDetail.alpha = if (isLoading) 0.5f else 1.0f
            eventButton.isEnabled = !isLoading
            fab.isEnabled = !isLoading
        }
    }

    private fun showError(errorMessage: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            errorMessage,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    private fun updateFavoriteIcon(isFavorited: Boolean) {
        binding.fab.setImageResource(
            if (isFavorited) R.drawable.baseline_favorite_24
            else R.drawable.baseline_favorite_border_24
        )
    }

    private fun bindEventData(eventItem: ListEventsItem) {
        binding.apply {
            eventTitle.text = eventItem.name
            eventInformation.text = HtmlCompat.fromHtml(
                eventItem.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(this@DetailEvent)
                .load(eventItem.mediaCover)
                .into(eventImg)

            eventSummarize.text = eventItem.summary
            categoryText.text = eventItem.category
            ownerText.text = eventItem.ownerName
            cityText.text = eventItem.cityName
            quotaText.text = (eventItem.quota - eventItem.registrants).toString()
            registrantsText.text = eventItem.registrants.toString()
            beginTimeText.text = eventItem.beginTime
            endTimeText.text = eventItem.endTime

            eventButton.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(eventItem.link)))
            }
        }
    }

    private fun toggleFavorite() {
        val eventItem = viewModel.eventItem.value ?: return
        val favoriteEntity = FavoriteEntity(
            id = eventItem.id,
            name = eventItem.name,
            mediaCover = eventItem.mediaCover,
            summary = eventItem.summary,
            description = eventItem.description,
            ownerName = eventItem.ownerName,
            cityName = eventItem.cityName,
            quota = eventItem.quota,
            registrants = eventItem.registrants,
            beginTime = eventItem.beginTime,
            endTime = eventItem.endTime,
            category = eventItem.category,
            link = eventItem.link,
            imageLogo = eventItem.imageLogo
        )

        if (viewModel.isFavorited.value == true) {
            viewModel.delete(favoriteEntity)
        } else {
            viewModel.insert(favoriteEntity)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
        _binding = null
    }

    private fun hideBottomNavigationView() {
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE
    }
}