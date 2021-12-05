package com.appsmoviles.gruposcomunitarios.presentation.userregister

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
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserRegisterBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
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
            viewModel.registerUser()
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
        when(item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "UserRegisterFragment"

        fun newInstance() = UserRegisterFragment()
    }

}