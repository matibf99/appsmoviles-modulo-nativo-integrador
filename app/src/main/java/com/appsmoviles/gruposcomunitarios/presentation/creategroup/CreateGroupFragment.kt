package com.appsmoviles.gruposcomunitarios.presentation.creategroup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromCameraIntent
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromGalleryIntent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentCreateGroupBinding
import com.appsmoviles.gruposcomunitarios.utils.FieldStatus
import com.appsmoviles.gruposcomunitarios.utils.storage.getImageUriTakenWithCamera

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGroupFragment : Fragment() {

    private val viewModel: CreateGroupFragmentViewModel by viewModels()

    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar!!.setTitle(R.string.fragment_create_group_title)
        
        viewModel.status.observe(viewLifecycleOwner, {
            when(it) {
                CreateGroupStatus.Success -> {
                    binding.progressCreateGroup.visibility = View.GONE

                    findNavController().popBackStack()
                    Log.d(TAG, "onCreateView: success")
                }
                CreateGroupStatus.Loading -> {
                    binding.progressCreateGroup.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading")
                }
                is CreateGroupStatus.Error -> {
                    binding.progressCreateGroup.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onCreateView: error: ${it.message}")
                }
            }
        })

        viewModel.imageUri.observe(viewLifecycleOwner, {
            Glide.with(requireContext())
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(binding.photoPreview)
        })

        binding.editName.editText?.setText(viewModel.name.value)
        binding.editDescription.editText?.setText(viewModel.description.value)
        binding.editTags.editText?.setText(viewModel.tags.value)

        binding.editName.editText?.doAfterTextChanged {
            viewModel.setName(it.toString())
        }

        viewModel.formNameStatus.observe(viewLifecycleOwner, {
            when (it) {
                FieldStatus.EMPTY -> binding.editName.error = getString(R.string.create_group_validation_name_invalid)
                else -> binding.editName.isErrorEnabled = false
            }
        })

        binding.editDescription.editText?.doAfterTextChanged {
            viewModel.setDescription(it.toString())
        }

        viewModel.formDescriptionStatus.observe(viewLifecycleOwner, {
            when (it) {
                FieldStatus.EMPTY -> binding.editDescription.error = getString(R.string.create_group_validation_description_invalid)
                else -> binding.editDescription.isErrorEnabled = false
            }
        })

        binding.editTags.editText?.doAfterTextChanged {
            viewModel.setTags(it.toString())
        }

        viewModel.formTagsStatus.observe(viewLifecycleOwner, {
            when(it) {
                FieldStatus.EMPTY -> binding.editTags.error = getString(R.string.create_group_validation_tags_invalid)
                else -> binding.editTags.isErrorEnabled = false
            }
        })

        binding.btnPickCamera.setOnClickListener {
            startActivityForResult(
                pickImageFromCameraIntent(requireActivity().applicationContext),
                REQUEST_CAPTURE_IMAGE
            )
        }

        binding.btnPickStorage.setOnClickListener {
            startActivityForResult(pickImageFromGalleryIntent(), REQUEST_CAPTURE_IMAGE)
        }

        viewModel.formImageStatus.observe(viewLifecycleOwner, {
            when (it) {
                FieldStatus.EMPTY -> binding.textPhotoError.visibility = View.VISIBLE
                else -> binding.textPhotoError.visibility = View.GONE
            }
        })

        binding.btnCreateGroup.setOnClickListener {
            if (viewModel.isFormValid()) {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    viewModel.imageUri.value
                )
                viewModel.createGroup(bitmap)
            }
        }

        return view
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CAPTURE_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data?.data != null) // Gallery or storage
                        viewModel.setImageUri(data.data!!)
                    else // Camera
                        viewModel.setImageUri(getImageUriTakenWithCamera(requireActivity().applicationContext))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val TAG = "CreateGroupFragment"
        private const val REQUEST_CAPTURE_IMAGE = 1

        @JvmStatic
        fun newInstance() = CreateGroupFragment()
    }
}