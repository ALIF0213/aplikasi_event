package com.example.appevent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.appevent.data.local.entity.FavoriteEvent
import com.example.appevent.ui.viewmodel.FavoriteViewModel
import com.example.appevent.ui.viewmodel.FavoriteViewModelFactory
import com.example.appevent.ui.viewmodel.DetailViewModel

class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels { FavoriteViewModelFactory(application) }

    private lateinit var eventImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var ownerTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var quotaTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var linkButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var favoriteButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        eventImageView = findViewById(R.id.imageLogo)
        nameTextView = findViewById(R.id.eventName)
        ownerTextView = findViewById(R.id.eventOwner)
        timeTextView = findViewById(R.id.eventTime)
        quotaTextView = findViewById(R.id.eventQuota)
        descriptionTextView = findViewById(R.id.eventDescription)
        linkButton = findViewById(R.id.eventLinkButton)
        progressBar = findViewById(R.id.progressBar)
        favoriteButton = findViewById(R.id.btnFavorite)

        val eventId = intent.getIntExtra("EVENT_ID", -1)
        if (eventId != -1) {
            detailViewModel.fetchEventDetail(eventId)
        }

        observeViewModel()
        setupFavoriteButton(eventId)
    }

    private fun observeViewModel() {
        detailViewModel.eventDetail.observe(this) { event ->
            if (event != null) {
                Glide.with(this).load(event.imageLogo).into(eventImageView)
                nameTextView.text = event.name
                ownerTextView.text = event.ownerName
                timeTextView.text = "Waktu: ${event.beginTime} - ${event.endTime}"
                quotaTextView.text = "Sisa Kuota: ${event.quota - event.registrants}"
                descriptionTextView.text = HtmlCompat.fromHtml(
                    event.description,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                linkButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
                progressBar.visibility = View.GONE
            }
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        detailViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupFavoriteButton(eventId: Int) {
        favoriteViewModel.checkFavoriteStatus(eventId)

        favoriteViewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteIcon(isFavorite)
        }

        favoriteButton.setOnClickListener {
            detailViewModel.eventDetail.value?.let { event ->
                val favoriteEvent = FavoriteEvent(
                    id = event.id,
                    name = event.name,
                    ownerName = event.ownerName,
                    beginTime = event.beginTime,
                    endTime = event.endTime,
                    quota = event.quota,
                    imageLogo = event.mediaCover,
                    description = event.description,
                    link = event.link,
                    registrants = event.registrants
                )
                favoriteViewModel.toggleFavoriteStatus(favoriteEvent)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}
