package com.appsmoviles.gruposcomunitarios.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentSearchBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.adapters.SearchAdapter
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
                SearchGroupsStatus.Loading -> {
                    binding.progressSearch.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading")
                }
                SearchGroupsStatus.Success -> {
                    binding.progressSearch.visibility = View.GONE
                    Log.d(TAG, "onCreateView: success")
                }
                is SearchGroupsStatus.Error -> {
                    binding.progressSearch.visibility = View.GONE
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: failed: ${status.message}")
                }
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

                    override fun onOpenGroupListener(position: Int) {
                        val group = viewModel.groups.value!![position]
                        val action = SearchFragmentDirections.actionSearchFragmentToGroupFragment(group)
                        findNavController().navigate(action)
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

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(false)
        super.onResume()
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
        val options = arrayOf("Name - Ascending", "Name - Descending","Created at - Ascending", "Created at - Descending")

        when(item.itemId) {
            R.id.menu_search_filter -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Order by")
                    .setItems(options) { dialog, which ->
                        when(which) {
                            0 -> viewModel.setSortBy(SortBy.NAME_ASCENDING)
                            1 -> viewModel.setSortBy(SortBy.NAME_DESCENDING)
                            2 -> viewModel.setSortBy(SortBy.CREATED_AT_ASCENDING)
                            3 -> viewModel.setSortBy(SortBy.CREATED_AT_DESCENDING)
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