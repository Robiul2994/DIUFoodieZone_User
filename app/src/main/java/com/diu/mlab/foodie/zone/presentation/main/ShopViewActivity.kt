package com.diu.mlab.foodie.zone.presentation.main

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.ActivityShopViewBinding
import com.diu.mlab.foodie.zone.databinding.QrLayoutBinding
import com.diu.mlab.foodie.zone.domain.model.ShopInfo
import com.diu.mlab.foodie.zone.util.getDrawable
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShopViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopViewBinding
    private val viewModel by viewModels<UserMainViewModel>()
    private var shopInfo = ShopInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopViewBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_FoodieZone_windowTranslucentStatus);
        setContentView(binding.root)

//        window.statusBarColor = getColor(R.color.trans)

        val shopIndex = intent.getIntExtra("shopIndex", 0)
        viewModel.getShopProfileList {
            MainScope().launch {
                Toast.makeText(this@ShopViewActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.shopProfileList.observe(this){lst ->
            if(lst.size > shopIndex) {
                shopInfo = lst[shopIndex].shopInfo
                shopInfo.pic.getDrawable { binding.pic.setImageDrawable(it) }
                shopInfo.cover.getDrawable { binding.cover.setImageDrawable(it) }
                binding.shopNm.text = shopInfo.nm
                binding.foodRecyclerView.adapter = FoodListViewAdapter(lst[shopIndex].foodList, shopInfo.email, 1)
            }
        }
        binding.qr.setBounceClickListener {
            if(shopInfo.qr.isNotEmpty()) {
                val builder = AlertDialog.Builder(this)
                val dialogBinding = QrLayoutBinding.inflate(LayoutInflater.from(this))
                builder.setView(dialogBinding.root);
                val alertDialog = builder.create()
                alertDialog.setCancelable(true)
                alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
                shopInfo.qr.getDrawable { dialogBinding.qr.setImageDrawable(it) }
                dialogBinding.shopNm.text = shopInfo.nm
                try { alertDialog.show() } catch (npe: NullPointerException) { npe.printStackTrace() }
            }
            else{
                MainScope().launch {
                    Toast.makeText(this@ShopViewActivity, "1Card Merchant QR code not provided by the seller", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBack.setBounceClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}