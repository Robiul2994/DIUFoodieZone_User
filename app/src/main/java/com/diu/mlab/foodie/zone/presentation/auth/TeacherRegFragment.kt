package com.diu.mlab.foodie.zone.presentation.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.zone.databinding.FragmentTeacherRegBinding
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.presentation.main.UserMainActivity
import com.diu.mlab.foodie.zone.util.addLiveTextListener
import com.diu.mlab.foodie.zone.util.getDrawable
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeacherRegFragment : Fragment() {
    private lateinit var binding: FragmentTeacherRegBinding
    private val viewModel : AuthViewModel by activityViewModels()
    private var tmpUser= FoodieUser()

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(requireActivity()).getSignInCredentialFromIntent(data)
            Log.d("TAG", "working")
            viewModel.firebaseSignup(credential, tmpUser,{
                viewModel.setLoadingVisibility(false)
                startActivity(Intent(requireContext(), UserMainActivity::class.java))
                requireActivity().finish()
                MainScope().launch {
                    Toast.makeText(requireContext(), "Signup successful", Toast.LENGTH_SHORT).show()
                }
            }){
                Log.e("TAG", "failed: $it")
                MainScope().launch {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.d("TAG", "RESULT_CANCELED")
            viewModel.setLoadingVisibility(false,0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherRegBinding.inflate(inflater, container, false)
        binding.teacherInfoCard.visibility = View.GONE
        binding.bottomInfo.visibility = View.GONE

        var tmpLink = ""

        binding.link.addLiveTextListener{
            tmpLink = it
            viewModel.getTeacherInfo(it)
        }
        viewModel.teacherLink.observe(requireActivity()){
            if(it != tmpLink)
                binding.link.setText(it)
        }
        viewModel.teacherInfo.observe(requireActivity()){user ->
            tmpUser = user
            binding.nm.text=user.nm
            binding.pn.text=user.phone
            binding.id.text=user.id
            binding.email.text=user.email
            binding.teacherInfoCard.visibility = View.VISIBLE
            binding.bottomInfo.visibility = View.VISIBLE

            user.pic.getDrawable { binding.pic.setImageDrawable(it) }
            binding.link.clearFocus()
            if(activity != null){
                val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.rootView?.windowToken,0)
            }
        }

        binding.btnRegister.setBounceClickListener{
            viewModel.googleSignIn(requireActivity(),resultLauncher, tmpUser){
                Log.e("TAG", "failed: $it")
                MainScope().launch {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

}