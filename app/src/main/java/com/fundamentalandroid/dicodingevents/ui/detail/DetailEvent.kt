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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fundamentalandroid.dicodingevents.databinding.FragmentDetailEventBinding

class DetailEvent : Fragment() {

    private var _binding: FragmentDetailEventBinding? = null
    private val binding get() = _binding!!
    private val args: DetailEventArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = args.eventItem.name
            setDisplayHomeAsUpEnabled(true)
        }

        val eventItem = args.eventItem
        binding.eventTitle.text = eventItem.name
        binding.eventInformation.text = HtmlCompat.fromHtml(
            eventItem.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        Glide.with(this)
            .load(eventItem.mediaCover)
            .into(binding.eventImg)

        binding.eventSummarize.text = eventItem.summary
        binding.categoryText.text = eventItem.category
        binding.ownerText.text = eventItem.ownerName
        binding.cityText.text = eventItem.cityName
        binding.quotaText.text = (eventItem.quota?.minus(eventItem.registrants)).toString()
        binding.registrantsText.text = eventItem.registrants.toString()
        binding.beginTimeText.text = eventItem.beginTime
        binding.endTimeText.text = eventItem.endTime

        binding.eventButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(eventItem.link)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
