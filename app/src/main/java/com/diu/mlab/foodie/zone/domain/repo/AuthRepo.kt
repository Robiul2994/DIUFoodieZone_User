package com.diu.mlab.foodie.zone.domain.repo

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.google.android.gms.auth.api.identity.SignInCredential

interface AuthRepo {
    fun firebaseLogin(credential: SignInCredential, success :(user: FoodieUser) -> Unit, failed :(msg : String) -> Unit)

    fun firebaseSignup(credential: SignInCredential, user: FoodieUser, success :() -> Unit, failed :(msg : String) -> Unit)

    fun googleSignIn(
        activity: Activity,
        resultLauncher : ActivityResultLauncher<IntentSenderRequest>,
        failed :(msg : String) -> Unit
    )

    suspend fun getTeacherInfo(link: String) : FoodieUser?


}