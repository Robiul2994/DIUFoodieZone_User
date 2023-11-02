package com.diu.mlab.foodie.zone.presentation.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.ActivityUserMainBinding
import com.diu.mlab.foodie.zone.presentation.auth.LoginActivity
import com.diu.mlab.foodie.zone.util.*
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserMainBinding
    val viewModel by viewModels<UserMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.appBarUserMain.toolbar)
        changeNavBarColor(R.color.tia,true)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val isUserLoggedIn = Firebase.auth.currentUser != null

//        GlobalScope.launch(Dispatchers.IO){
//            Log.d("token", "onCreate: ${getAccessToken()}")
//        }


        viewModel.userProfile.observe(this){user ->
            if(user.nm.isNotEmpty() && isUserLoggedIn) {
                headerView.findViewById<TextView>(R.id.userName).text = user.nm
                user.pic.getDrawable { headerView.findViewById<ImageView>(R.id.userPic).setImageDrawable(it) }
            }

        }
        if(!isUserLoggedIn){
            findViewById<TextView>(R.id.edit).visibility = View.GONE
            findViewById<TextView>(R.id.logout).visibility = View.GONE
        }
        else{
            viewModel.getUserProfile(Firebase.auth.currentUser!!.email!!.transformedEmailId()){}
        }
        headerView.setOnClickListener {
            if(Firebase.auth.currentUser == null)
                startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.appBarUserMain.btnMenu.setBounceClickListener{
            drawerLayout.open()
        }
        changeStatusBarColor(R.color.whiteX,resources.getBoolean(R.bool.isDarkDisable))
//        ActionBarDrawerToggle(this,drawerLayout,binding.appBarUserMain.toolbar,R.string.app_name, R.string.app_name).syncState()

        val manager: FragmentManager = supportFragmentManager
        val meowNav = binding.appBarUserMain.contentMain.meowNav

        meowNav.add(MeowBottomNavigation.Model(1, R.drawable.ic_cart2))
        meowNav.add(MeowBottomNavigation.Model(2, R.drawable.ic_home))
        meowNav.add(MeowBottomNavigation.Model(3, R.drawable.ic_order_list))

        viewModel.navPosition.observe(this){remId ->
            meowNav.show(remId,false)
        }

        meowNav.setOnClickMenuListener {
            viewModel.setNavPosition(it.id)
            when(it.id){
                1 ->{
                    if(isUserLoggedIn)
                        manager.beginTransaction()
                            .replace(binding.appBarUserMain.contentMain.mainFragment.id, CartFragment())
                            .commit()
                    else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        recreate()
                        viewModel.setNavPosition(2)

                    }

                }
                2 ->{
                    manager.beginTransaction()
                        .replace(binding.appBarUserMain.contentMain.mainFragment.id, HomeFragment())
                        .commit()
                }
                3 ->{
                    if(isUserLoggedIn)
                        manager.beginTransaction()
                            .replace(binding.appBarUserMain.contentMain.mainFragment.id, OrderListFragment())
                            .commit()
                    else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        recreate()
                        viewModel.setNavPosition(2)
                    }
                }
            }
        }

        findViewById<TextView>(R.id.edit).setBounceClickListener {
//            startActivity(Intent(this, LoginActivity::class.java)) //edit
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.logout).setBounceClickListener {
            Firebase.auth.signOut()
//            headerView.findViewById<TextView>(R.id.userName).text = "Login"
//            headerView.findViewById<ImageView>(R.id.userPic).setImageDrawable(getDrawable(R.drawable.ic_profile))
//            startActivity(Intent(this, LoginActivity::class.java))
            recreate()
        }
    }

}