package com.example.appevent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appevent.R
import com.example.appevent.ui.adapter.EventAdapter
import com.example.appevent.ui.viewmodel.EventViewModel

class PastFragment : Fragment() {
    private val viewModel: EventViewModel by viewModels()
    private lateinit var eventAdapter: EventAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_past, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        searchView = view.findViewById(R.id.searchView)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(emptyList())
        recyclerView.adapter = eventAdapter
        viewModel.fetchCompletedEvents()
        observeViewModel()
        setupSearchView()
        return view
    }

    private fun observeViewModel() {
        viewModel.eventsCompleted.observe(viewLifecycleOwner) { events ->
            eventAdapter.updateData(events)
        }
        viewModel.searchedEvents.observe(viewLifecycleOwner) { searchResults ->
            eventAdapter.updateData(searchResults)
        }
        viewModel.isLoadingPast.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchEvents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.searchEvents(it) }
                return true
            }
        })
    }
}
