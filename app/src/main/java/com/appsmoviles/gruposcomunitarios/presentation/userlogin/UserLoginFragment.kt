package com.appsmoviles.gruposcomunitarios.presentation.userlogin

import androidx.lifecycle.ViewModelProvider
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
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserLoginBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserLoginFragment : Fragment() {

    private val viewModel: UserLoginViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    private var _binding: FragmentUserLoginBinding? = null
    private val binding: FragmentUserLoginBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserLoginBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Log in"

        binding.editLoginEmail.editText?.doAfterTextChanged {
            viewModel.setEmail(it.toString())
        }

        binding.editLoginPassword.editText?.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.btnLogin.setOnClickListener {
            if (viewModel.isFormValid())
                viewModel.logInUser()
        }

        viewModel.logInStatus.observe(viewLifecycleOwner, {
            when(it) {
                UserLogInStatus.Success -> {
                    binding.progressUserLogin.visibility = View.GONE
                    findNavController().popBackStack()

                    Log.d(TAG, "onCreateView: success in login")
                }
                UserLogInStatus.Loading -> {
                    binding.progressUserLogin.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading login")
                }
                is UserLogInStatus.Error -> {
                    binding.progressUserLogin.visibility = View.GONE

                    Log.d(TAG, "onCreateView: error in login: ${it.message}")
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.formEmailStatus.observe(viewLifecycleOwner, {

        })

        return binding.root
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(true)
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->  findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "UserLoginFragment"

        fun newInstance() = UserLoginFragment()
    }

}