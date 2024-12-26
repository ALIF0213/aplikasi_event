package com.example.appevent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appevent.R
import com.example.appevent.ui.adapter.EventAdapter
import com.example.appevent.ui.viewmodel.EventViewModel

class HomeFragment : Fragment() {

    private val viewModel: EventViewModel by viewModels()
    private lateinit var eventAdapterActive: EventAdapter
    private lateinit var eventAdapterCompleted: EventAdapter
    private lateinit var upcomingProgressBar: ProgressBar
    private lateinit var pastProgressBar: ProgressBar
    private lateinit var errorTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        upcomingProgressBar = view.findViewById(R.id.upcomingProgressBar)
        pastProgressBar = view.findViewById(R.id.pastProgressBar)
        errorTextView = view.findViewById(R.id.errorTextView)
        val recyclerViewActive: RecyclerView = view.findViewById(R.id.recyclerViewActive)
        val recyclerViewCompleted: RecyclerView = view.findViewById(R.id.recyclerViewCompleted)

        recyclerViewActive.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCompleted.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        eventAdapterActive = EventAdapter(emptyList())
        eventAdapterCompleted = EventAdapter(emptyList())
        recyclerViewActive.adapter = eventAdapterActive
        recyclerViewCompleted.adapter = eventAdapterCompleted

        observeViewModel()

        viewModel.fetchActiveEvents()
        viewModel.fetchCompletedEvents()

        return view
    }

    private fun observeViewModel() {
        viewModel.eventsActive.observe(viewLifecycleOwner) { activeEvents ->
            val limitedActiveEvents =
                if (activeEvents.size > 5) activeEvents.take(5) else activeEvents
            eventAdapterActive.updateData(limitedActiveEvents)
            checkErrorState()
        }

        viewModel.eventsCompleted.observe(viewLifecycleOwner) { completedEvents ->
            val limitedCompletedEvents =
                if (completedEvents.size > 5) completedEvents.take(5) else completedEvents
            eventAdapterCompleted.updateData(limitedCompletedEvents)
            checkErrorState()
        }

        viewModel.isLoadingUpcoming.observe(viewLifecycleOwner) { isLoading ->
            upcomingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isLoadingPast.observe(viewLifecycleOwner) { isLoading ->
            pastProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                errorTextView.text = errorMessage
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
            }
        }
    }

    private fun checkErrorState() {
        if (eventAdapterActive.itemCount == 0 && eventAdapterCompleted.itemCount == 0) {
            errorTextView.text =
                "Tidak ada acara yang tersedia. Silakan periksa koneksi internet Anda."
            errorTextView.visibility = View.VISIBLE
        } else {
            errorTextView.visibility = View.GONE
        }
    }
}
