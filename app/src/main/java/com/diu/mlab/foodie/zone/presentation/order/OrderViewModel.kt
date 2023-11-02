package com.diu.mlab.foodie.zone.presentation.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.domain.use_cases.order.OrderUseCases
import com.diu.mlab.foodie.zone.domain.use_cases.user.UserMainUseCases
import com.diu.mlab.foodie.zone.util.toDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val orderUseCases: OrderUseCases,
    private val userUseCases: UserMainUseCases
) : ViewModel() {
    val myOrderList= savedStateHandle.getLiveData("myOrderList", emptyList<OrderInfo>())

    val orderInfo= savedStateHandle.getLiveData("orderInfo", OrderInfo())
    val progressInfoList= savedStateHandle.getLiveData("progressInfoList", emptyList<Pair<String, String>>())

    fun getMyOrderList(failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            orderUseCases.getMyOrderList({
                savedStateHandle["myOrderList"]= it
            },failed)
        }
    }
    fun updateLocalOrderInfo(orderInfo: OrderInfo){
        savedStateHandle["orderInfo"]= orderInfo
    }

    fun getOrderInfo(orderId: String, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            orderUseCases.getOrderInfo(orderId,{odrInfo ->
                val tmpList = mutableListOf<Pair<String,String>>()
                savedStateHandle["orderInfo"]= odrInfo
                if(odrInfo.isPaid)
                    tmpList.add(Pair("Paid", odrInfo.paymentTime.toDateTime()))
                if(odrInfo.paymentConfirmationTime != 0L) {
                    if(odrInfo.isPaymentConfirmed)
                        tmpList.add(Pair("Pay Confirmed", odrInfo.paymentConfirmationTime.toDateTime()))
                    else
                        tmpList.add(Pair("Payment Failed", odrInfo.paymentConfirmationTime.toDateTime()))
                }
                if(odrInfo.isRunnerChosen)
                    tmpList.add(Pair("Runner Chosen", odrInfo.runnerChosenTime.toDateTime()))
                if(odrInfo.isFoodHandover2RunnerNdPaid)
                    tmpList.add(Pair("Food Given To Runner", odrInfo.foodHandover2RunnerTime.toDateTime()))
                if(odrInfo.runnerReceivedTime != 0L){
                    if(odrInfo.isRunnerReceivedFoodnMoney)
                        tmpList.add(Pair("Runner Got Paid", odrInfo.runnerReceivedTime.toDateTime()))
                    else
                        tmpList.add(Pair("Runner Didn't Got Paid", odrInfo.runnerReceivedTime.toDateTime()))
                }
                if(odrInfo.isFoodHandover2User)
                    tmpList.add(Pair("Food Delivered", odrInfo.foodHandover2UserTime.toDateTime()))
                if(odrInfo.userReceivedTime != 0L) {
                    if(odrInfo.isUserReceived)
                        tmpList.add(Pair("Food Received", odrInfo.userReceivedTime.toDateTime()))
                    else
                        tmpList.add(Pair("Food Not Received", odrInfo.userReceivedTime.toDateTime()))
                }
                if(odrInfo.isCanceled)
                    tmpList.add(Pair("Canceled", odrInfo.canceledTime.toDateTime()))

                savedStateHandle["progressInfoList"]= tmpList
            },failed)

        }
    }

    fun placeOrder(orderInfo: OrderInfo, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            orderUseCases.placeOrder(orderInfo, {
                getOrderInfo(it.orderId,failed)
            }, failed)
        }

    }

    fun updateUserProfile(
        updatedUser: FoodieUser,
        failed :(msg : String) -> Unit
    ){
        savedStateHandle["orderInfo"]= orderInfo.value!!.copy(userInfo = updatedUser)
        viewModelScope.launch(Dispatchers.IO){
            userUseCases.updateUserProfile(updatedUser, false ,{

            },failed)
        }
    }

    fun updateOrderInfo(
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        shopEmail: String,
        runnerEmail: String ="",
        success :() -> Unit,
        failed :(msg : String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            orderUseCases.updateOrderInfo(orderInfo.value!!.orderId, varBoolName, value, varTimeName, shopEmail, runnerEmail, success, failed)
        }
    }

    val isLeftTabSelected= savedStateHandle.getLiveData<Boolean>("leftTabSelected")
    fun setLeftTabSelected(isSelected: Boolean){
        savedStateHandle["leftTabSelected"] = isSelected
    }

    fun updatePaymentType(type: String, shopEmail: String, success: () -> Unit, failed :(msg : String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            orderUseCases.updatePaymentType(orderInfo.value!!.orderId, type, shopEmail, success, failed)
        }
    }

}