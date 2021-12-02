package com.appsmoviles.gruposcomunitarios.presentation.groups

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentGroupsBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class GroupsFragment : Fragment() {

    private val viewModel: GroupsViewModel by viewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
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

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewGroups.layoutManager = linearLayoutManager
        binding.recyclerViewGroups.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstVisible = linearLayoutManager.findFirstCompletelyVisibleItemPosition()

                if (firstVisible == 0)
                    binding.groupsFab.extend()
                else
                    binding.groupsFab.shrink()
            }
        })

        adapterRole = object : GroupsAdapter("My groups", viewModel.groupsWithRole.value!!, false) {
            override fun onUnsubscribeListener(position: Int) {
            }

            override fun onOpenGroupListener(position: Int) {
                val group = viewModel.groupsWithRole.value!![position]
                val action = GroupsFragmentDirections.actionGroupsFragmentToGroupFragment(group)
                findNavController().navigate(action)
            }
        }
        adapterSubscribed = object : GroupsAdapter("Subscribed groups", viewModel.subscribedGroups.value!!, true) {
            override fun onUnsubscribeListener(position: Int) {
                viewModel.unsubscribeTo(position)
            }

            override fun onOpenGroupListener(position: Int) {
                val group = viewModel.subscribedGroups.value!![position]
                val action = GroupsFragmentDirections.actionGroupsFragmentToGroupFragment(group)
                findNavController().navigate(action)
            }

        }
        concatAdapter = ConcatAdapter(adapterRole, adapterSubscribed)
        binding.recyclerViewGroups.adapter = concatAdapter

        viewModel.status.observe(requireActivity(), {
            when(it) {
                GroupsStatus.Success -> {
                    binding.progressGroups.visibility = View.GONE
                    Log.d(TAG, "onCreateView: success")
                }
                GroupsStatus.Loading -> {
                    binding.progressGroups.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading")
                }
                is GroupsStatus.Error -> {
                    binding.progressGroups.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error: ${it.message}")
                }
            }
        })

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

        binding.groupsFab.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_groupsFragment_to_createGroupFragment)
        }

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

    companion object {
        private const val TAG = "GroupsFragment"

        @JvmStatic
        fun newInstance() = GroupsFragment()
    }
}