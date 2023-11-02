package com.diu.mlab.foodie.zone.presentation.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.ShopProfile
import com.diu.mlab.foodie.zone.domain.use_cases.user.UserMainUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserMainViewModel @Inject constructor(
    private val mainUseCases: UserMainUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val shopProfileList= savedStateHandle.getLiveData<List<ShopProfile>>("shopProfileList", emptyList())
    val currentFood= savedStateHandle.getLiveData("food", FoodItem())
    val navPosition= savedStateHandle.getLiveData("navPosition", 2)

    val shopProfile= savedStateHandle.getLiveData("shopProfile",ShopProfile())
    val userProfile= savedStateHandle.getLiveData("userProfile",FoodieUser())


    fun setNavPosition(position:Int){
        savedStateHandle["navPosition"]= position
    }
    fun getShopProfileList(
        failed :(msg : String) -> Unit
    ){
        viewModelScope.launch {
            mainUseCases.getShopProfileList({
//                val tmpList = it.toList()
                savedStateHandle["shopProfileList"]= it
                Log.d("TAG", "getShopProfileList: list loaded $it")
            },failed)
        }
    }

    fun getFoodDetails(
        shopEmail: String,
        foodId: String,
        failed: (msg: String) -> Unit
    ){
        viewModelScope.launch {
            mainUseCases.getFoodDetails(shopEmail,foodId,{
                savedStateHandle["food"]= it
            },failed)
        }
    }

    fun getShopProfile(
        shopEmail: String,
        failed: (msg: String) -> Unit
    ){
        viewModelScope.launch {
            mainUseCases.getShopProfile(shopEmail,{
                savedStateHandle["shopProfile"]= it

            },failed)
        }
    }

    fun getUserProfile(email: String, failed: (msg: String) -> Unit){
        viewModelScope.launch {
            mainUseCases.getUserProfile(email,{
                savedStateHandle["userProfile"]= it
            }, failed)
        }
    }

}