package com.diu.mlab.foodie.zone.domain.use_cases.auth

import android.util.Log
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.SuperUser
import com.diu.mlab.foodie.zone.domain.repo.AuthRepo
import com.google.android.gms.auth.api.identity.SignInCredential
import javax.inject.Inject

class FirebaseSignup @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(credential: SignInCredential, user: FoodieUser, success :() -> Unit, failed :(msg : String) -> Unit) {
        val partOfId = user.email.filter { it.isDigit() || it == '-'}
        Log.d("TAG", "working case")
        Log.d("TAG", "invoke: ${credential.id}")

        if(user.userType=="Teacher"){
            if(!user.email.contains(credential.id)) {
                failed.invoke("Please use email address from above.")
                Log.d("TAG", "working failed")
            }
            else
                repo.firebaseSignup(credential, user.copy(email = credential.id), success, failed)
        }
        else if (!credential.id.contains("@diu.edu.bd") && !credential.id.contains("@daffodilvarsity.edu.bd"))
            failed.invoke("Please use DIU email address.")
        else if(partOfId.isNotEmpty() && user.id.contains(partOfId))
            repo.firebaseSignup(credential, user, success, failed)
        else
            failed.invoke("Use DIU email associated with your Student ID.")
    }
}