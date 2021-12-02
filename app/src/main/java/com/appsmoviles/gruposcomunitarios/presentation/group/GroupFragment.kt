package com.appsmoviles.gruposcomunitarios.presentation.group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appsmoviles.gruposcomunitarios.databinding.FragmentGroupBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupFragment : Fragment() {

    private val args: GroupFragmentArgs by navArgs()

    private val viewModel: GroupViewModel by viewModels()

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)

        viewModel.setGroup(args.group)
        (activity as AppCompatActivity).supportActionBar?.title = viewModel.group.value!!.name

        binding.createPostFab.setOnClickListener {
            val group = viewModel.group.value!!
            val action = GroupFragmentDirections.actionGroupFragmentToCreatePostFragment(group)
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(true)
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() = GroupFragment()
    }
}