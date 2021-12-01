package com.appsmoviles.gruposcomunitarios.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentSearchBinding
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar!!.title = "Search"

        viewModel.status.observe(requireActivity(), { status ->
            when(status) {
                SearchGroupsStatus.LOADING -> Log.d(TAG, "onCreateView: loading")
                SearchGroupsStatus.LOADED -> Log.d(TAG, "onCreateView: loaded")
                SearchGroupsStatus.FAILED -> Log.d(TAG, "onCreateView: failed")
                SearchGroupsStatus.UNKNOWN -> Log.d(TAG, "onCreateView: unknown")
                else -> Log.d(TAG, "onCreateView: null")
            }
        })

        viewModel.groups.observe(requireActivity(), { list ->
            Log.d(TAG, "onCreateView: new groups list")

            if (list != null) {
                Log.d(TAG, "onCreateView: is not empty")
                binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewSearch.adapter = object : SearchAdapter(list) {
                    override fun onSubscribeListener(position: Int) {
                        viewModel.subscribeToGroup(position)
                    }
                }
            }
        })

        viewModel.sortBy.observe(requireActivity(), { sortBy ->
            binding.textSortBy.text = when(sortBy) {
                SortBy.NAME_DESCENDING -> "name, descending"
                SortBy.NAME_ASCENDING -> "name, ascending"
                SortBy.CREATED_AT_DESCENDING -> "created at, descending"
                SortBy.CREATED_AT_ASCENDING -> "created at, ascending"
                else -> "-"
            }
        })

        return view
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchView: SearchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    viewModel.searchGroups(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        searchView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                Log.d(TAG, "onViewAttachedToWindow: searchview opened")
            }

            override fun onViewDetachedFromWindow(v: View?) {
                // Searchview collapsed
                Log.d(TAG, "onCreateOptionsMenu: searchview closed")
                viewModel.loadGroups()
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val options = arrayOf("Name - Descending", "Name - Ascending", "Created at - Descending", "Created at - Ascending")

        when(item.itemId) {
            R.id.menu_search_filter -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Order by")
                    .setItems(options) { dialog, which ->
                        when(which) {
                            0 -> viewModel.setSortBy(SortBy.NAME_DESCENDING)
                            1 -> viewModel.setSortBy(SortBy.NAME_ASCENDING)
                            2 -> viewModel.setSortBy(SortBy.CREATED_AT_DESCENDING)
                            3 -> viewModel.setSortBy(SortBy.CREATED_AT_ASCENDING)
                        }
                    }
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "SearchFragment"

        @JvmStatic
        fun newInstance() =
            SearchFragment()
    }
}