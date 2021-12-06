package com.appsmoviles.gruposcomunitarios.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentSearchBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.UserStatus
import com.appsmoviles.gruposcomunitarios.presentation.adapters.SearchAdapter
import com.appsmoviles.gruposcomunitarios.utils.helpers.SortBy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar!!.setTitle(R.string.fragment_search_title)
        viewModel.loadGroups(viewModel.sortBy.value!!)

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.layoutManager = linearLayoutManager

        adapter = object :
            SearchAdapter(mainViewModel.user.value?.username ?: "", viewModel.groups.value!!) {
            override fun onSubscribeListener(position: Int, username: String) {
                if (username.isNotEmpty()) {
                    viewModel.subscribeToGroup(position, username)
                    adapter.notifyItemChanged(position)
                }
            }

            override fun onOpenGroupListener(position: Int) {
                val group = viewModel.groups.value!![position]
                val action = SearchFragmentDirections.actionSearchFragmentToGroupFragment(group)
                findNavController().navigate(action)
            }

            override fun onOpenImageListener(position: Int) {
                val image = viewModel.groups.value!![position].photo

                if (image != null) {
                    val action = SearchFragmentDirections.actionSearchFragmentToPhotoFragment(
                        imageUrl = image
                    )
                    findNavController().navigate(action)
                }
            }
        }
        binding.recyclerViewSearch.adapter = adapter

        mainViewModel.userStatus.observe(viewLifecycleOwner, {
            adapter.username = mainViewModel.user.value?.username ?: ""
            adapter.notifyDataSetChanged()

            when (it) {
                UserStatus.SUCCESS -> Log.d(TAG, "onCreateView: user loaded")
                UserStatus.LOADING -> Log.d(TAG, "onCreateView: loading user")
                UserStatus.ERROR -> Log.d(TAG, "onCreateView: error in loading user")
                else -> Log.d(TAG, "onCreateView: prevent null")
            }
        })

        viewModel.status.observe(viewLifecycleOwner, { status ->
            when (status) {
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

        viewModel.groups.observe(viewLifecycleOwner, { list ->
            Log.d(TAG, "onCreateView: new groups list")
            adapter.items = list
            adapter.notifyDataSetChanged()
        })

        viewModel.sortBy.observe(viewLifecycleOwner, { sortBy ->
            binding.textSortBy.setText(
                when (sortBy) {
                    SortBy.NAME_DESCENDING -> R.string.order_by_name_descending
                    SortBy.NAME_ASCENDING -> R.string.order_by_name_ascending
                    SortBy.CREATED_AT_DESCENDING -> R.string.order_by_created_at_descending
                    SortBy.CREATED_AT_ASCENDING -> R.string.order_by_created_at_ascending
                    else -> R.string.order_by_nothing
                }
            )
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
                Log.d(TAG, "onViewAttachedToWindow: SearchView opened")
            }

            override fun onViewDetachedFromWindow(v: View?) {
                Log.d(TAG, "onCreateOptionsMenu: SearchView closed")
                viewModel.loadGroups()
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val options = resources.getStringArray(R.array.fragment_search_dialog_sort_by)

        when (item.itemId) {
            R.id.menu_search_filter -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.fragment_search_dialog_order_by)
                    .setSingleChoiceItems(
                        options, when (viewModel.sortBy.value) {
                            SortBy.NAME_ASCENDING -> 0
                            SortBy.NAME_DESCENDING -> 1
                            SortBy.CREATED_AT_ASCENDING -> 2
                            SortBy.CREATED_AT_DESCENDING -> 3
                            else -> 0
                        }
                    ) { dialog, which ->
                        Log.d(TAG, "onOptionsItemSelected: new order selected - which: $which")
                        when (which) {
                            0 -> viewModel.setSortBy(SortBy.NAME_ASCENDING)
                            1 -> viewModel.setSortBy(SortBy.NAME_DESCENDING)
                            2 -> viewModel.setSortBy(SortBy.CREATED_AT_ASCENDING)
                            3 -> viewModel.setSortBy(SortBy.CREATED_AT_DESCENDING)
                        }
                    }
                    .setPositiveButton(R.string.dialog_positive_button_select, null)
                    .setNegativeButton(R.string.dialog_negative_button_cancel, null)
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "SearchFragment"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}