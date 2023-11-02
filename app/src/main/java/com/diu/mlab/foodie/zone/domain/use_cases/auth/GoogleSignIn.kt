package com.diu.mlab.foodie.zone.domain.use_cases.auth

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.repo.AuthRepo
import javax.inject.Inject

class GoogleSignIn @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(
        user: FoodieUser? = null,
        activity: Activity,
        resultLauncher : ActivityResultLauncher<IntentSenderRequest>,
        failed :(msg : String) -> Unit
    ) {
        if(user != null){ //sign in
//            if(user.nm.isEmpty())
//                failed.invoke("You must add Name.")
//            else
            if(user.userType=="Student"){
                if(user.id.isEmpty())
                    failed.invoke("You must add Student ID.")
                else if(user.phone.isEmpty())
                    failed.invoke("You must add Phone Number.")
                else
                    repo.googleSignIn(activity, resultLauncher, failed)
            }
            else if(user.id.isEmpty())
                failed.invoke("You must add valid profile link.")
            else
                repo.googleSignIn(activity, resultLauncher, failed)

        }
        else //login
            repo.googleSignIn(activity, resultLauncher, failed)
    }
}