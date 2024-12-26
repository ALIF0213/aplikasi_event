package com.example.appevent.ui.adapter

import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appevent.R
import com.example.appevent.data.remote.response.ListEventsItem
import com.example.appevent.DetailActivity
import com.example.appevent.data.local.entity.FavoriteEvent

class EventAdapter(private var eventList: List<Any>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    fun updateData(newEvents: List<Any>) {
        eventList = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvEventTitle)
        private val owner: TextView = view.findViewById(R.id.tvEventOwner)
        private val city: TextView = view.findViewById(R.id.tvEventCity)
        private val quota: TextView = view.findViewById(R.id.tvEventQuota)
        private val image: ImageView = view.findViewById(R.id.ivEventImage)


        private val backgroundColor: Int
        private val textColor: Int

        init {
            val typedValue = TypedValue()
            val theme = itemView.context.theme

            theme.resolveAttribute(R.attr.eventItemBackground, typedValue, true)
            backgroundColor = typedValue.data

            theme.resolveAttribute(R.attr.eventItemTextColor, typedValue, true)
            textColor = typedValue.data
        }

        fun bind(event: Any) {
            when (event) {
                is ListEventsItem -> {
                    title.text = event.name
                    owner.text = event.ownerName
                    city.text = event.cityName
                    quota.text = "Kuota: ${event.quota}"

                    // Load image using Glide
                    Glide.with(itemView.context)
                        .load(event.mediaCover)
                        .into(image)

                    // Set background and text colors based on theme
                    itemView.setBackgroundColor(backgroundColor)
                    title.setTextColor(textColor)
                    owner.setTextColor(textColor)
                    city.setTextColor(textColor)
                    quota.setTextColor(textColor)

                    itemView.setOnClickListener {
                        val intent = Intent(itemView.context, DetailActivity::class.java)
                        intent.putExtra("EVENT_ID", event.id)
                        itemView.context.startActivity(intent)
                    }
                }
                is FavoriteEvent -> {
                    title.text = event.name
                    owner.text = event.ownerName
                    city.visibility = View.GONE  // Hide if not applicable for FavoriteEvent
                    quota.text = "Kuota: ${event.quota}"

                    // Load image using Glide
                    Glide.with(itemView.context)
                        .load(event.imageLogo)
                        .into(image)

                    // Set background and text colors based on theme
                    itemView.setBackgroundColor(backgroundColor)
                    title.setTextColor(textColor)
                    owner.setTextColor(textColor)
                    quota.setTextColor(textColor)

                    itemView.setOnClickListener {
                        val intent = Intent(itemView.context, DetailActivity::class.java)
                        intent.putExtra("EVENT_ID", event.id)
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }
}
