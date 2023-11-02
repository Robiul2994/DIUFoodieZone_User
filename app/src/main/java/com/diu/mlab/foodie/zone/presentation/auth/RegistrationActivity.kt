package com.diu.mlab.foodie.zone.presentation.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import com.diu.mlab.foodie.zone.databinding.ActivityRegistrationBinding
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.jem.fliptabs.FlipTab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manager: FragmentManager = supportFragmentManager

        viewModel.isLeftTabSelected.observe(this){
            if (it)
                binding.flipTab.selectLeftTab(false)
            else
                binding.flipTab.selectRightTab(false)
        }

        binding.flipTab.setTabSelectedListener(object: FlipTab.TabSelectedListener {
            override fun onTabSelected(isLeftTab: Boolean, tabTextValue: String) {
                if (isLeftTab){
                    viewModel.setLeftTabSelected(true)
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, StudentRegFragment()).commit()
                }else{
                    viewModel.setLeftTabSelected(false)
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, TeacherRegFragment()).commit()
                }
            }
            override fun onTabReselected(isLeftTab: Boolean, tabTextValue: String) {}
        })
        binding.btnBack.setBounceClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewModel.loadingVisibility.observe(this){
            binding.loadingLayout.visibility = if(it) View.VISIBLE else View.GONE
        }
    }
}