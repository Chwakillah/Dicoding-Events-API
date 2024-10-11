package com.fundamentalandroid.dicodingevents.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.databinding.ItemEventBinding
import com.fundamentalandroid.dicodingevents.ui.detail.DetailEvent

class EventAdapter : ListAdapter<ListEventsItem, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.eventTitle.text = event.name

            Glide.with(binding.eventImg.context)
                .load(event.mediaCover)
                .into(binding.eventImg)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailEvent::class.java)
                intent.apply {
                    putExtra("EVENT_NAME", event.name)
                    putExtra("EVENT_SUMMARY", event.summary)
                    putExtra("EVENT_CATEGORY", event.category)
                    putExtra("EVENT_OWNER", event.ownerName)
                    putExtra("EVENT_CITY", event.cityName)
                    putExtra("EVENT_QUOTA", event.quota)
                    putExtra("EVENT_REGISTRANTS", event.registrants)
                    putExtra("EVENT_BEGIN_TIME", event.beginTime)
                    putExtra("EVENT_END_TIME", event.endTime)
                    putExtra("EVENT_INFO", event.description)
                    putExtra("EVENT_IMG", event.mediaCover)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

