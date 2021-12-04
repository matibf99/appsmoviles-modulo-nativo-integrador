package com.appsmoviles.gruposcomunitarios.presentation.createcomment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentCreateCommentBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.utils.FieldStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCommentFragment : Fragment() {

    private val args: CreateCommentFragmentArgs by navArgs()

    private val viewModel: CreateCommentViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    private var _binding: FragmentCreateCommentBinding? = null
    private val binding: FragmentCreateCommentBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCommentBinding.inflate(inflater, container, false)

        args.let {
            viewModel.setPost(it.post)
            viewModel.setCommentParent(it.commentParent)
        }

        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.fragment_create_comment_title)

        binding.editCommentContent.editText?.setText(viewModel.commentContent.value)

        binding.editCommentContent.editText?.doAfterTextChanged {
            viewModel.setCommentContent(it.toString())
        }

        viewModel.commentFormStatus.observe(viewLifecycleOwner, {
            when(it) {
                FieldStatus.EMPTY -> {
                    binding.btnCreateComment.isEnabled = false
                    binding.btnCreateComment.alpha = 0.5f
                }
                else -> {
                    binding.btnCreateComment.isEnabled = true
                    binding.btnCreateComment.alpha = 1f
                }
            }
        })

        binding.btnCreateComment.setOnClickListener {
            Log.d(TAG, "onCreateView: button clicked!")

            if (viewModel.isFormValid())
                viewModel.createComment(mainViewModel.user.value!!.username ?: "")
        }

        viewModel.commentStatus.observe(viewLifecycleOwner, {
            when(it) {
                CreateCommentStatus.Success -> {
                    findNavController().popBackStack()
                    Log.d(TAG, "onCreateView: success creating comment")
                }
                CreateCommentStatus.Loading -> {
                    binding.progressCreateComment.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading comment")
                }
                is CreateCommentStatus.Error -> {
                    binding.progressCreateComment.visibility = View.GONE

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error ${it.message}")
                }
            }
        })

        return binding.root
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(true)
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "CreateCommentFragment"

        fun newInstance() = CreateCommentFragment()
    }

}