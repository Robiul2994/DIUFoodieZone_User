package com.diu.mlab.foodie.zone.data.repo

import android.content.Context
import android.util.Log
import com.diu.mlab.foodie.zone.data.data_source.*
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.domain.repo.OrderRepo
import com.diu.mlab.foodie.zone.util.getAccessToken
import com.diu.mlab.foodie.zone.util.getTopic
import com.diu.mlab.foodie.zone.util.transformedEmailId
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderRepoImpl @Inject constructor(
    private val realtime: FirebaseDatabase,
    private val firebaseUser: FirebaseUser?,
    private val api: NotificationApi,
    private val context: Context
): OrderRepo {
    override fun getOrderInfo(
        orderId: String,
        success: (orderInfo: OrderInfo) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val userEmail = firebaseUser?.email?.transformedEmailId().toString()

        if(firebaseUser != null) {
            realtime
                .getReference("orderInfo/all").child(userEmail).child(orderId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val info = snapshot.getValue<OrderInfo>()
                        success.invoke(info ?: OrderInfo())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        failed.invoke(error.message)
                    }
                })
        }
    }

    override fun getMyOrderList(
        success: (orderInfoList: List<OrderInfo>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val userEmail = firebaseUser?.email?.transformedEmailId().toString()

        val myOrderList = mutableListOf<OrderInfo>()
        realtime
            .getReference("orderInfo/all").child(userEmail)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val info = snapshot.getValue<OrderInfo>()!!
                    myOrderList.add(0,info)
                    success.invoke(myOrderList)
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val info = snapshot.getValue<OrderInfo>()!!
                    myOrderList.forEachIndexed { index, orderInfo ->
                        if(orderInfo.orderId == previousChildName){
                            success.invoke(myOrderList.toMutableList().apply {
                                removeAt(index)
                                add(index,info)
                            })
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    failed.invoke(error.message)
                }
            })
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun placeOrder(
        orderInfo: OrderInfo,
        success: (orderInfo: OrderInfo) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val userEmail = firebaseUser?.email?.transformedEmailId().toString()

        if(firebaseUser != null) {
            val ref = realtime.getReference("orderInfo/current")
            val orderId = ref.push().key!!
            val info =
                orderInfo
                    .copy(
                        orderId = orderId,
                        isOrdered = true,
                        orderTime = System.currentTimeMillis()
                    )

            realtime
                .getReference("orderInfo/all")
                .child(userEmail)
                .child(orderId)
                .setValue(info)
                .addOnFailureListener {
                    failed.invoke(it.message.toString())
                }

            realtime
                .getReference("orderInfo/shop")
                .child(orderInfo.shopInfo.email)
                .child("current")
                .child(orderId)
                .setValue(info)
                .addOnFailureListener {
                    failed.invoke(it.message.toString())
                }

            ref.child(orderId)
                .setValue(info)
                .addOnSuccessListener {
                    success.invoke(info)
                    GlobalScope.launch(Dispatchers.IO){
                        try {
                            api.notifySeller(
                                NotificationMessage(
                                    OrderNotifyInfo(
                                        topic = info.shopInfo.email.getTopic(),
                                        data = NotificationData(
                                            title = "${info.userInfo.nm.split(" ")[0]} Ordered Food",
                                            body = "${info.quantity}x ${info.foodInfo.nm}"
                                        )
                                    )
                                ), "Bearer ${context.getAccessToken()}"
                            )
                        }
                        catch(e: Exception){
                            Log.d("TAG", "placeOrder Exception: ${e.message}")
                            failed.invoke("Server Error")
                        }
                    }
                }
                .addOnFailureListener {
                    failed.invoke(it.message.toString())
                }
        }
    }

    override fun updatePaymentType(
        orderId: String,
        type: String,
        shopEmail: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ){
        val userEmail = firebaseUser?.email?.transformedEmailId().toString()
        if(firebaseUser != null) {
            realtime
                .getReference("orderInfo/all")
                .child(userEmail)
                .child(orderId)
                .child("paymentType")
                .setValue(type)

            realtime
                .getReference("orderInfo/current")
                .child(orderId)
                .child("paymentType")
                .setValue(type)

            realtime
                .getReference("orderInfo/shop")
                .child(shopEmail)
                .child("current")
                .child(orderId)
                .child("paymentType")
                .setValue(type)
                .addOnSuccessListener {
                    success.invoke()
                }
                .addOnFailureListener {
                    failed.invoke(it.message.toString())
                }
        }

    }

    override fun updateOrderInfo(
        orderId: String,
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        shopEmail: String,
        runnerEmail: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val userEmail = firebaseUser?.email?.transformedEmailId().toString()

        if(firebaseUser != null) {
            realtime
                .getReference("orderInfo/all")
                .child(userEmail)
                .child(orderId)
                .child(varBoolName)
                .setValue(value)


            val time = System.currentTimeMillis()
            realtime
                .getReference("orderInfo/all")
                .child(userEmail)
                .child(orderId)
                .child(varTimeName)
                .setValue(time)


            if(varTimeName != "userReceivedTime"){
                realtime
                    .getReference("orderInfo/current")
                    .child(orderId)
                    .child(varBoolName)
                    .setValue(value)
                realtime
                    .getReference("orderInfo/current")
                    .child(orderId)
                    .child(varTimeName)
                    .setValue(time)

                realtime
                    .getReference("orderInfo/shop")
                    .child(shopEmail)
                    .child("current")
                    .child(orderId)
                    .child(varBoolName)
                    .setValue(value)
                    .addOnSuccessListener {
                        success.invoke()
                    }
                    .addOnFailureListener {
                        failed.invoke(it.message.toString())
                    }

                realtime
                    .getReference("orderInfo/shop")
                    .child(shopEmail)
                    .child("current")
                    .child(orderId)
                    .child(varTimeName)
                    .setValue(time)
            }
            else{
                realtime.getReference("orderInfo/current").child(orderId)
                    .addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(!snapshot.exists() && runnerEmail.isNotEmpty()){
                                realtime
                                    .getReference("orderInfo/runner")
                                    .child(runnerEmail)
                                    .child(orderId)
                                    .child(varBoolName)
                                    .setValue(value)
                                realtime
                                    .getReference("orderInfo/runner")
                                    .child(runnerEmail)
                                    .child(orderId)
                                    .child(varTimeName)
                                    .setValue(time)
                            }
                            else if(snapshot.exists()){
                                realtime
                                    .getReference("orderInfo/current")
                                    .child(orderId)
                                    .child(varBoolName)
                                    .setValue(value)
                                realtime
                                    .getReference("orderInfo/current")
                                    .child(orderId)
                                    .child(varTimeName)
                                    .setValue(time)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })

                realtime.getReference("orderInfo/shop")
                    .child(shopEmail)
                    .child("current")
                    .child(orderId)
                    .addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(!snapshot.exists()){
                                realtime
                                    .getReference("orderInfo/shop")
                                    .child(shopEmail)
                                    .child("old")
                                    .child(orderId)
                                    .child(varBoolName)
                                    .setValue(value)
                                realtime
                                    .getReference("orderInfo/shop")
                                    .child(shopEmail)
                                    .child("old")
                                    .child(orderId)
                                    .child(varTimeName)
                                    .setValue(time)
                            }
                            else{
                                realtime
                                    .getReference("orderInfo/shop")
                                    .child(shopEmail)
                                    .child("current")
                                    .child(orderId)
                                    .child(varBoolName)
                                    .setValue(value)
                                realtime
                                    .getReference("orderInfo/shop")
                                    .child(shopEmail)
                                    .child("current")
                                    .child(orderId)
                                    .child(varTimeName)
                                    .setValue(time)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }
    }
}