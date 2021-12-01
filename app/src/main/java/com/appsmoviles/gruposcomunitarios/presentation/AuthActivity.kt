package com.appsmoviles.gruposcomunitarios.presentation

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import android.R.attr.data
import android.net.Uri
import android.widget.Toast


class AuthActivity : AppCompatActivity() {
    companion object{
        private const val RC_SIGN_IN = 120
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityAuthBinding
    private lateinit var mGoogleSignInClient:GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try{
                val account = task.getResult(ApiException::class.java)

                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //Navegamos a la nueva pantalla
                            showHome(account.email?: "", ProviderType.GOOGLE)
                        } else {
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try{
//                val account = task.getResult(ApiException::class.java)
//
//                if(account != null){
//                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            //Navegamos a la nueva pantalla
//                            showHome(account.email?: "", ProviderType.GOOGLE)
//                        } else {
//                            showAlert()
//                        }
//                    }
//                }
//            } catch (e: ApiException) {
//                showAlert()
//            }
//        }
//    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_files), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider= prefs.getString("provider", null)

        if(email != null && provider != null ){
            binding.authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup() {
        title = "Autenticaction";

        binding.singUpButton.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() ) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()).addOnCompleteListener{
                    if (it.isSuccessful) {
                        //Navegamos a la nueva pantalla
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }

            }
        }

        binding.loginButton.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() ) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()).addOnCompleteListener{
                    if (it.isSuccessful) {
                        //Navegamos a la nueva pantalla
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }

            }
        }

        //Google auth
        binding.googleButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult.launch(signInIntent)
        }
    }

    private val startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            handleSignInResult(task)
        }
    }
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);



    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val personName: String = account.getDisplayName()
                val personGivenName: String = account.getGivenName()
                val personFamilyName: String = account.getFamilyName()
                val personEmail: String = account.getEmail()
                val personId: String = account.getId()
                val personPhoto: Uri = account.getPhotoUrl()

                Toast.makeText(this,"Email" + personEmail,Toast.LENGTH_SHORT).show()
            }
            // Signed in successfully, show authenticated UI.

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("GOOGLE ERROR","Erro al iniciar sesion")
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider:ProviderType) {
         val homeIntent = Intent(this, HomeActivity::class.java).apply {
              putExtra("email", email)
              putExtra("provider", provider.name)
          }
      startActivity(homeIntent);
    }

}