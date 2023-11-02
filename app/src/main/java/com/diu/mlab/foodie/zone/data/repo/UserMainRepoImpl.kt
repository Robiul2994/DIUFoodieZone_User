package com.diu.mlab.foodie.zone.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.ShopInfo
import com.diu.mlab.foodie.zone.domain.model.ShopProfile
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import com.diu.mlab.foodie.zone.util.copyUriToFile
import com.diu.mlab.foodie.zone.util.transformedEmailId
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserMainRepoImpl @Inject constructor(
    private val realtime: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val context: Context
) : UserMainRepo {
    override fun getShopProfileList(
        success: (shopProfileList: List<ShopProfile>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopProfileList = mutableListOf<ShopProfile>()
        realtime
            .getReference("shopProfile")
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val info = snapshot.child("info").getValue<ShopInfo>()!!
                    val foods = mutableListOf<FoodItem>()
                    snapshot.child("foodList").children.forEach {
                        foods.add(it.getValue<FoodItem>()!!)
                    }

                    shopProfileList.add( ShopProfile(info, foods) )
                    success.invoke(shopProfileList)
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getShopProfile(
        shopEmail: String,
        success: (shopProfile: ShopProfile) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopProfile = ShopProfile()
        realtime
            .getReference("shopProfile").child(shopEmail)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    shopProfile.shopInfo = snapshot.child("info").getValue<ShopInfo>()!!
                    val foods = mutableListOf<FoodItem>()
                    snapshot.child("foodList").children.forEach {
                        foods.add(it.getValue<FoodItem>()!!)
                    }
                    shopProfile.foodList = foods
                    success.invoke(shopProfile)                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getFoodDetails(
        shopEmail: String,
        foodId: String,
        success: (food: FoodItem) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        realtime
            .getReference("shopProfile").child(shopEmail).child("foodList").child(foodId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue<FoodItem>()!!
                    success.invoke(food)
                }
                override fun onCancelled(error: DatabaseError) {
                    failed.invoke(error.message)
                }
            })
    }

    override fun getUserProfile(
        email: String,
        success: (user: FoodieUser) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        firestore.collection("userProfiles").document(email.transformedEmailId())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    val user = document.toObject(FoodieUser::class.java)!!
                    success.invoke(user)
                } else {
                    Log.d("TAG", "No such document")
                    failed.invoke("User doesn't exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
                failed.invoke("Something went wrong")

            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun updateUserProfile(
        updatedUser: FoodieUser,
        picUpdated: Boolean,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val userRef = storage.reference.child("user/${updatedUser.email}")
        val path = firestore.collection("userProfiles").document(updatedUser.email)

        var tmpUserInfo = updatedUser

        GlobalScope.launch(Dispatchers.IO){
            if(picUpdated) {
                var pic = context.copyUriToFile(Uri.parse(updatedUser.pic))
                pic = Compressor.compress(context, pic) {
                    default(height = 360, width = 360, format = Bitmap.CompressFormat.JPEG)
                }
                val picLink = userRef.child("pic.jpg")
                    .putFile(Uri.fromFile(pic)).await().storage.downloadUrl.await()
                tmpUserInfo = updatedUser.copy(pic = picLink.toString())
            }
            path.set(tmpUserInfo)
                .addOnSuccessListener {
                    Log.d("TAG", "updateUserProfile DocumentSnapshot successfully written!")
                    success.invoke()
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                    failed.invoke("Something went wrong")
                }
        }
    }
}