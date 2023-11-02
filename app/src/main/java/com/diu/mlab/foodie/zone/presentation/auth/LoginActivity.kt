package com.diu.mlab.foodie.zone.presentation.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.ActivityLoginBinding
import com.diu.mlab.foodie.zone.presentation.main.UserMainActivity
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
            viewModel.firebaseLogin(credential,{
                viewModel.setLoadingVisibility(false)
                startActivity(Intent(this,UserMainActivity::class.java))
                finish()
            }){
                Log.e("TAG", "failed: $it")
                MainScope().launch {
                    Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.d("TAG", "RESULT_CANCELED")
            viewModel.setLoadingVisibility(false,0)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInBtn.setBounceClickListener {
            viewModel.googleSignIn(this,resultLauncher){msg ->
                Log.d("TAG", "onCreate: $msg")
                MainScope().launch {
                    Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()

                }
            }
        }
        binding.signUpBtn.setBounceClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }
        binding.btnBack.setBounceClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewModel.loadingVisibility.observe(this){
            binding.loadingLayout.visibility = if(it) View.VISIBLE else View.GONE
        }
    }
}