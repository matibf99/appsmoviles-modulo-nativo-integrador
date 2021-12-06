package com.appsmoviles.gruposcomunitarios.presentation.userregister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserRegisterBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.creategroup.CreateGroupFragment
import com.appsmoviles.gruposcomunitarios.utils.storage.getImageUriTakenWithCamera
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromCameraIntent
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromGalleryIntent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserRegisterFragment : Fragment() {

    private val viewModel: UserRegisterViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    private var _binding: FragmentUserRegisterBinding? = null
    private val binding: FragmentUserRegisterBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserRegisterBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Register"

        binding.editRegisterUsername.editText?.doAfterTextChanged {
            viewModel.setUsername(it.toString())
        }

        binding.editRegisterName.editText?.doAfterTextChanged {
            viewModel.setName(it.toString())
        }

        binding.editRegisterSurname.editText?.doAfterTextChanged {
            viewModel.setSurname(it.toString())
        }

        binding.editRegisterEmail.editText?.doAfterTextChanged {
            viewModel.setEmail(it.toString())
        }

        binding.editRegisterPassword.editText?.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.btnRegister.setOnClickListener {
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                viewModel.imageUri.value
            )
            viewModel.registerUser(bitmap)
        }

        viewModel.imageUri.observe(viewLifecycleOwner, {
            Glide.with(requireContext())
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(binding.userRegisterPhoto)
        })

        binding.userRegisterPhoto.setOnClickListener {
            val imageUrl = viewModel.imageUri.value?.toString() ?: ""
            val action = UserRegisterFragmentDirections.actionUserRegisterFragmentToPhotoFragment(imageUrl)
            findNavController().navigate(action)
        }

        binding.btnRegisterCamera.setOnClickListener {
            startActivityForResult(
                pickImageFromCameraIntent(requireActivity().applicationContext),
                REQUEST_CAPTURE_IMAGE
            )
        }

        binding.btnRegisterStorage.setOnClickListener {
            startActivityForResult(pickImageFromGalleryIntent(), REQUEST_CAPTURE_IMAGE)
        }

        viewModel.statusRegistered.observe(viewLifecycleOwner, {
            when (it) {
                UserRegisterStatus.Success -> {
                    binding.progressRegister.visibility = View.GONE
                    findNavController().popBackStack()

                    Log.d(TAG, "onCreateView: register success")
                }
                UserRegisterStatus.Loading -> {
                    binding.progressRegister.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading register")
                }
                is UserRegisterStatus.Error -> {
                    binding.progressRegister.visibility = View.GONE

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onCreateView: loading register")
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
        when (item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
        private const val TAG = "UserRegisterFragment"
        private const val REQUEST_CAPTURE_IMAGE = 1

        fun newInstance() = UserRegisterFragment()
    }

}