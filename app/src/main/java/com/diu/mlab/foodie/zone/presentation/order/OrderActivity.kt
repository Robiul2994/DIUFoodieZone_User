package com.diu.mlab.foodie.zone.presentation.order

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.diu.mlab.foodie.zone.databinding.ActivityOrderBinding
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.presentation.main.CartFragment
import com.diu.mlab.foodie.zone.presentation.main.UserMainViewModel
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.diu.mlab.foodie.zone.util.transformedEmailId
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOrderBinding
    private val viewModel by viewModels<OrderViewModel>()

    private val secondaryViewModel by viewModels<UserMainViewModel>()
    lateinit var orderInfo: OrderInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val manager: FragmentManager = supportFragmentManager

        manager.beginTransaction()
            .replace(binding.orderFragment.id, OrderConfirmationFragment())
            .commit()

        val bundle = intent.extras!!
        val shopEmail = bundle.getString("shopEmail")!!
        val foodId = bundle.getString("foodId")!!
        val currentType = bundle.getString("currentType")!!
        val currentTypePrice = bundle.getInt("currentTypePrice")
        val quantity = bundle.getInt("quantity")
        val newOrder = bundle.getBoolean("newOrder", true)
        if(newOrder)
        {
            orderInfo =
                OrderInfo(type = currentType, typePrice = currentTypePrice, quantity = quantity)

            secondaryViewModel.getFoodDetails(shopEmail, foodId) {
                MainScope().launch {
                    Toast.makeText(this@OrderActivity, it, Toast.LENGTH_SHORT).show()
                }
            }

            secondaryViewModel.getUserProfile(
                Firebase.auth.currentUser?.email?.transformedEmailId().toString()
            ) {
                MainScope().launch {
                    Toast.makeText(this@OrderActivity, it, Toast.LENGTH_SHORT).show()
                }
            }

            secondaryViewModel.getShopProfile(shopEmail) {
                MainScope().launch {
                    Toast.makeText(this@OrderActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
            secondaryViewModel.currentFood.observe(this) {
                viewModel.updateLocalOrderInfo(orderInfo.copy(foodInfo = it))
            }
            secondaryViewModel.userProfile.observe(this) {
                viewModel.updateLocalOrderInfo(orderInfo.copy(userInfo = it))
            }
            secondaryViewModel.shopProfile.observe(this) {
                viewModel.updateLocalOrderInfo(orderInfo.copy(shopInfo = it.shopInfo))
            }
        }
        else{
            val orderId = bundle.getString("orderId")!!
            viewModel.getOrderInfo(orderId){
                MainScope().launch {
                    Toast.makeText(this@OrderActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.orderInfo.observe(this){
            orderInfo = it
            Log.d("TAG", "onCreate: isOrdered ${it.isOrdered} isPaid ${it.isPaid}")
            if(orderInfo.isPaid){
                manager.beginTransaction()
                    .replace(binding.orderFragment.id, OrderInfoFragment())
                    .commit()
            }
            else if(orderInfo.isOrdered){
                manager.beginTransaction()
                    .replace(binding.orderFragment.id, PaymentFragment())
                    .commit()
            }
        }


        binding.btnBack.setBounceClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}