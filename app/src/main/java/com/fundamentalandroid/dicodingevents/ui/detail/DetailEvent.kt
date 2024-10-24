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
import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.db.FavoriteEntity
import com.fundamentalandroid.dicodingevents.databinding.FragmentDetailEventBinding
import com.fundamentalandroid.dicodingevents.ui.helper.ViewModelFactory

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[DetailEventViewModel::class.java]

        viewModel.setEventItem(args.eventItem)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = args.eventItem.name
            setDisplayHomeAsUpEnabled(true)
        }

        setHasOptionsMenu(true)

        viewModel.eventItem.observe(viewLifecycleOwner) { eventItem ->
            bindEventData(eventItem)
        }

        viewModel.isFavorited.observe(viewLifecycleOwner) { isFavorited ->
            if (isFavorited) {
                binding.fab.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.fab.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }

        binding.fab.setOnClickListener {
            toggleFavorite()
        }
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

        viewModel.isFavorited.observe(viewLifecycleOwner) { isFavorited ->
            if (isFavorited) {
                viewModel.delete(favoriteEntity)
                binding.fab.setImageResource(R.drawable.baseline_favorite_border_24)
            } else {
                viewModel.insert(favoriteEntity)
                binding.fab.setImageResource(R.drawable.baseline_favorite_24)
            }
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
    }
}
