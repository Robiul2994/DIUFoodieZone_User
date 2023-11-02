package com.diu.mlab.foodie.zone.presentation.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.FragmentStudentRegBinding
import com.diu.mlab.foodie.zone.databinding.FragmentTeacherRegBinding
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.presentation.main.UserMainActivity
import com.diu.mlab.foodie.zone.util.addLiveTextListener
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentRegFragment : Fragment() {
    private lateinit var binding: FragmentStudentRegBinding
    private val viewModel : AuthViewModel by activityViewModels()
    private var tmpUser= FoodieUser()

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(requireActivity()).getSignInCredentialFromIntent(data)
            val pic = credential.profilePictureUri.toString().replace("=s96","=s384")

            viewModel.firebaseSignup(credential,
                tmpUser.copy(
                    nm = credential.displayName!!,
                    email = credential.id,
                    pic = pic
                ),{
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
        binding = FragmentStudentRegBinding.inflate(inflater, container, false)

        var id = ""
        var pn = ""
        viewModel.studentId.observe(requireActivity()){
            if(id!=it)
                binding.studentId.setText(it)
        }
        viewModel.studentPn.observe(requireActivity()){
            if(pn!=it)
                binding.pnNo.setText(it)
        }
        binding.studentId.addLiveTextListener {
            id=it
            viewModel.setStudentId(it)
        }
        binding.pnNo.addLiveTextListener {
            pn=it
            viewModel.setStudentPn(it)
        }
        binding.btnRegister.setBounceClickListener{
            tmpUser = FoodieUser(
                id = id,
                phone = pn,
                userType = "Student"
            )
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