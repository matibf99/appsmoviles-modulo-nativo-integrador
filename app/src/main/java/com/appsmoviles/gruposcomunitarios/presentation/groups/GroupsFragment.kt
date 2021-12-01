package com.appsmoviles.gruposcomunitarios.presentation.groups

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.databinding.FragmentGroupsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupsFragment : Fragment() {

    private val viewModel: GroupsViewModel by viewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterRole: GroupsAdapter
    private lateinit var adapterSubscribed: GroupsAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar!!.title = "Groups"

        binding.recyclerViewGroups.layoutManager = LinearLayoutManager(requireContext())
        adapterRole = object : GroupsAdapter("My groups", viewModel.groupsWithRole.value!!, false) {
            override fun onUnsubscribeListener(position: Int) {
            }
        }
        adapterSubscribed = object : GroupsAdapter("Subscribed groups", viewModel.subscribedGroups.value!!, true) {
            override fun onUnsubscribeListener(position: Int) {
                viewModel.unsubscribeTo(position)
            }

        }
        concatAdapter = ConcatAdapter(adapterRole, adapterSubscribed)
        binding.recyclerViewGroups.adapter = concatAdapter

        viewModel.subscribedGroups.observe(requireActivity(), {
            Log.d(TAG, "onCreateView: notify subscribedGroups, size ${it.size}")
            adapterSubscribed.items = it
            adapterSubscribed.notifyDataSetChanged()
        })

        viewModel.groupsWithRole.observe(requireActivity(), {
            Log.d(TAG, "onCreateView: notify groupsWithRole, size ${it.size}")
            adapterRole.items = it
            adapterRole.notifyDataSetChanged()
        })

        return view
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "GroupsFragment"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupsFragment()
    }
}