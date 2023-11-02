package com.diu.mlab.foodie.zone.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.ActivityShopFoodViewBinding
import com.diu.mlab.foodie.zone.domain.model.CartItem
import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.presentation.auth.LoginActivity
import com.diu.mlab.foodie.zone.presentation.order.OrderActivity
import com.diu.mlab.foodie.zone.util.getDrawable
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopFoodViewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShopFoodViewBinding
    private val viewModel by viewModels<UserMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopFoodViewBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_FoodieZone_windowTranslucentStatus)
        setContentView(binding.root)

//        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val bundle = intent.extras!!
        val shopEmail = bundle.getString("shopEmail")!!
        val foodId = bundle.getString("foodId")!!
        Log.d("TAG", "onCreate ShopFoodViewActivity: $shopEmail $foodId")
        var currentFood = FoodItem()
        var price: Int
        var currentType = ""
        var currentTypePrice = 0
        var quantity = 1
        var typeList = emptyList<String>()
        var priceList = emptyList<String>()

        viewModel.getFoodDetails(shopEmail, foodId){
            MainScope().launch {
                Toast.makeText(this@ShopFoodViewActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.currentFood.observe(this){food ->
            currentFood = food
            price = if(food.price.isNotEmpty()) food.price.split(",")[0].toInt() else 0
            currentTypePrice = price
            currentFood.pic.getDrawable { binding.foodImageView.setImageDrawable(it) }

            binding.nm.text = currentFood.nm
            binding.price.text = currentFood.price.split(",")[0]
            binding.time.text = currentFood.time
            binding.status.text = currentFood.status

            if(currentFood.types.isNotEmpty()){
                typeList = currentFood.types
                    .replace(",  ", ",")
                    .replace(", ", ",")
                    .split(",")
                currentType = typeList[0]

                priceList = currentFood.price
                    .replace(",  ", ",")
                    .replace(", ", ",")
                    .split(",")

//              binding.type.setAdapter(ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,typeList))
                (binding.type as? MaterialAutoCompleteTextView)?.setSimpleItems(typeList.toTypedArray())
                binding.type.setText(typeList[0],false)
                binding.typeLayout.visibility = View.VISIBLE
            }

        }
        binding.type.setOnItemClickListener { parent, view, position, id ->
            currentType = typeList[position]
            currentTypePrice = if(priceList.isNotEmpty()) priceList[position].toInt() else 0
            price = currentTypePrice * quantity
            binding.price.text = price.toString()
        }
        binding.btnPlus.setBounceClickListener{
            if(quantity<50) {
                quantity++
                binding.quantity.text = quantity.toString()
                price = currentTypePrice * quantity
                binding.price.text = price.toString()
            }
            else{
                Toast.makeText(this, "Can't order more than 50", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMinus.setBounceClickListener{
            if(quantity>1) {
                quantity--
                binding.quantity.text = quantity.toString()
                price = currentTypePrice * quantity
                binding.price.text = price.toString()
            }
            else{
                Toast.makeText(this, "Must order one", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCart.setBounceClickListener {
            if(currentFood.status != "Unavailable") {
                Toast.makeText(this, "Food Added to Cart", Toast.LENGTH_SHORT).show()
                CartFragment.cartList.add(
                    CartItem(
                        currentFood,
                        quantity,
                        currentType,
                        currentTypePrice,
                        shopEmail
                    )
                )
            }
            else{
                Toast.makeText(this, "Food Not Available", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnOrder.setBounceClickListener {
            if(currentFood.status != "Unavailable") {
                bundle.putString("currentType", currentType)
                bundle.putInt("currentTypePrice", currentTypePrice)
                bundle.putInt("quantity", quantity)

                if(Firebase.auth.currentUser != null)
                    startActivity(Intent(this, OrderActivity::class.java).putExtras(bundle))
                else {
                    Toast.makeText(this, "You must login first", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }

        binding.btnBack.setBounceClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}