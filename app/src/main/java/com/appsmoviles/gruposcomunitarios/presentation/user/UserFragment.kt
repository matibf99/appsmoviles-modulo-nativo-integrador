package com.appsmoviles.gruposcomunitarios.presentation.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.work.ListenableWorker
import dagger.hilt.android.AndroidEntryPoint
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserBinding
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root


        (activity as AppCompatActivity).supportActionBar!!.title = "User"









        //Obvserve variables
        binding.nameEditText.setText(viewModel.name.value?.toString())
        binding.nameEditText.doAfterTextChanged {
            viewModel.setName(it.toString())
        }

        binding.surnameEditText.setText(viewModel.surname.value?.toString())
        binding.surnameEditText.doAfterTextChanged {
            viewModel.setSurname(it.toString())
        }

        binding.usernameEditText.setText(viewModel.username.value?.toString())
        binding.usernameEditText.doAfterTextChanged {
            viewModel.setUsername(it.toString())
        }

        binding.emailEditText.setText(viewModel.email.value?.toString())
        binding.emailEditText.doAfterTextChanged {
            viewModel.setEmail(it.toString())
            Log.d("ValorEmail",it.toString())
        }

        binding.passwordEditText.setText(viewModel.password.value?.toString())
        binding.passwordEditText.doAfterTextChanged {
            viewModel.setPassword(it.toString())
            Log.d("ValorPassword",it.toString())
        }

        //Change TextView
        viewModel.name.observe(viewLifecycleOwner,{
            binding.nameTextView.text  = it.toString()
        })

        viewModel.surname.observe(viewLifecycleOwner,{
            binding.surnametextView.text = it.toString()
        })

        viewModel.username.observe(viewLifecycleOwner, {
            binding.usernametextView.text= it.toString()
        })

        viewModel.email.observe(viewLifecycleOwner, {
            binding.emailtextView.text = it.toString()
        })

        //Invisible attributes
        invisibleAttributesTextView()

        //SignIn function
        binding.loginButton.setOnClickListener {
            if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() ) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.emailEditText.text.toString(),
                        binding.passwordEditText.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(),"SE LOGEO",Toast.LENGTH_LONG).show()
                            //Show attributes
                            showAttributes()
                            //Invisible attributes
                            invisibleAttributesEditText()
                        } else {
                            Toast.makeText(requireContext(),"NO SE LOGEO",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }


        binding.singUpButton.setOnClickListener {
            viewModel.registerUser()
        }

        viewModel.status.observe(viewLifecycleOwner,{
            when(it){
                 UserStatus.LOADED -> Toast.makeText(requireContext(),"Se Registro el usuario",Toast.LENGTH_SHORT).show()
                 UserStatus.FAILED -> Toast.makeText(requireContext(),"No se registro el usuario",Toast.LENGTH_SHORT).show()
            }
        })


        return view
    }

    private fun invisibleAttributesTextView () {
        binding.nameTextView.visibility = View.INVISIBLE
        binding.usernametextView.visibility = View.INVISIBLE
        binding.emailtextView.visibility = View.INVISIBLE
        binding.surnametextView.visibility = View.INVISIBLE
        binding.userimageView.visibility = View.INVISIBLE
    }

    private fun showAttributes() {
        binding.nameTextView.visibility = View.VISIBLE
        binding.usernametextView.visibility = View.VISIBLE
        binding.emailtextView.visibility = View.VISIBLE
        binding.surnametextView.visibility = View.VISIBLE
        binding.userimageView.visibility = View.VISIBLE
    }

    private fun invisibleAttributesEditText () {
        binding.emailEditText.visibility = View.INVISIBLE
        binding.nameEditText.visibility = View.INVISIBLE
        binding.usernameEditText.visibility = View.INVISIBLE
        binding.surnameEditText.visibility = View.INVISIBLE
        binding.passwordEditText.visibility = View.INVISIBLE
        binding.loginButton.visibility = View.INVISIBLE
        binding.singUpButton.visibility =View.INVISIBLE
        binding.iconimageView.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment()
    }
}