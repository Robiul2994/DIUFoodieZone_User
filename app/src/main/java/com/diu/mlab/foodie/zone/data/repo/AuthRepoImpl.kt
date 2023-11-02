package com.diu.mlab.foodie.zone.data.repo

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.repo.AuthRepo
import com.diu.mlab.foodie.zone.util.transformedEmailId
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import javax.inject.Inject


class AuthRepoImpl @Inject constructor(
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val context: Context
    ) : AuthRepo {

    override fun firebaseLogin(
        credential: SignInCredential,
        success: (user: FoodieUser) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val authCredential = GoogleAuthProvider.getCredential(credential.googleIdToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    firestore.collection("userProfiles").document(credential.id.transformedEmailId())
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
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    failed.invoke("Something went wrong")
                }
            }
    }

    override fun firebaseSignup(
        credential: SignInCredential,
        user: FoodieUser,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val email = credential.id.transformedEmailId()
        val path = firestore.collection("userProfiles").document(email)
        val authCredential = GoogleAuthProvider.getCredential(credential.googleIdToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    //val user = auth.currentUser!!
                    path.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                //val user = document.toObject(FoodieUser::class.java)!!
                                failed.invoke("User already exist.")
                            } else {
                                path.set(user.copy(email = email ))
                                    .addOnSuccessListener {
                                        Log.d("TAG", "DocumentSnapshot successfully written!")
                                        success.invoke()
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("TAG", "get failed with ", exception)
                                        failed.invoke("Something went wrong")
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                            failed.invoke("Something went wrong")

                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    failed.invoke("Something went wrong")
                }
            }
    }


    override fun googleSignIn(activity: Activity, resultLauncher : ActivityResultLauncher<IntentSenderRequest>,
                              failed: (msg: String) -> Unit) {
        Log.e("TAG1", "Google Sign-in")

        val request = GetSignInIntentRequest.builder()
            .setServerClientId(activity.getString(R.string.server_client_id))
            .build()
        Identity.getSignInClient(activity)
            .getSignInIntent(request)
            .addOnSuccessListener { result ->
                try {
                    Log.d("TAG", "googleSignIn: ${result.describeContents()}")
                    Log.e("TAG2", "Google Sign-in")
                    resultLauncher.launch(IntentSenderRequest.Builder(result).build())
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("TAG", "Google Sign-in failed")
                    failed.invoke("Something went wrong")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Google Sign-in failed", e)
                failed.invoke("Something went wrong")
            }
    }

    override suspend fun getTeacherInfo(link: String): FoodieUser?{
        val info = FoodieUser(userType="Teacher")
        try{
            val doc = Jsoup.connect(link).get() //https://faculty.daffodilvarsity.edu.bd/profile/cse/shah-md-tanvir.html
            Log.d("TAG", "onCreate: ${doc.title()}")
            val newsHeadlines0 = doc.select(".profile-row0")
            val newsHeadlines1 = doc.select(".profile-row1")
            val dp = doc.select(".profaile-pic img")
            if(dp.isNotEmpty())
                info.pic = dp[0].attr("src")
            Log.d("TAG", "Name: "+"\n\t ${dp[0].html()}")
            for (headline in newsHeadlines0) {
                if(headline.select(".profile-row-left").html().lowercase().contains("name"))
                    info.nm = headline.select(".profile-row-right").html()
                else if(headline.select(".profile-row-left").html().lowercase().contains("mail")) {
                    info.email = headline.select(".profile-row-right").html().split(",")[0]
                }
                else if(headline.select(".profile-row-left").html().lowercase().contains("phone")) {
                    info.phone = headline.select(".profile-row-right").html().split(",")[0]
                }
            }
            for (headline in newsHeadlines1) {
                if(headline.select(".profile-row-left").html().lowercase().contains("id"))
                    info.id = headline.select(".profile-row-right").html()
            }
            Log.d("TAG", "onCreate: $info")
            return info
        }
        catch (ex: HttpStatusException){
            Log.d("TAG", "getTeacherInfo HttpStatusException: $ex")
            return null
        }

    }

}