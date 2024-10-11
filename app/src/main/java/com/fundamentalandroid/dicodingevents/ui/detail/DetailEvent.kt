package com.fundamentalandroid.dicodingevents.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fundamentalandroid.dicodingevents.databinding.FragmentDetailEventBinding

class DetailEvent : AppCompatActivity() {

    private lateinit var binding: FragmentDetailEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventName = intent.getStringExtra("EVENT_NAME")
        val eventSummary = intent.getStringExtra("EVENT_SUMMARY")
        val eventCategory = intent.getStringExtra("EVENT_CATEGORY")
        val eventOwner = intent.getStringExtra("EVENT_OWNER")
        val eventCity = intent.getStringExtra("EVENT_CITY")
        val eventQuota = intent.getIntExtra("EVENT_QUOTA", 0)
        val eventRegistrants = intent.getIntExtra("EVENT_REGISTRANTS", 0)
        val eventBeginTime = intent.getStringExtra("EVENT_BEGIN_TIME")
        val eventEndTime = intent.getStringExtra("EVENT_END_TIME")
        val eventInfo = intent.getStringExtra("EVENT_INFO")
        val eventImgUrl = intent.getStringExtra("EVENT_IMG")

        binding.apply {
            eventTitle.text = eventName
            eventSummarize.text = eventSummary
            categoryText.text = eventCategory
            ownerText.text = eventOwner
            cityText.text = eventCity
            quotaText.text = eventQuota.toString()
            registrantsText.text = eventRegistrants.toString()
            beginTimeText.text = eventBeginTime
            endTimeText.text = eventEndTime
            eventInformation.text = eventInfo

            Glide.with(this@DetailEvent)
                .load(eventImgUrl)
                .into(binding.eventImg)
        }
    }
}
