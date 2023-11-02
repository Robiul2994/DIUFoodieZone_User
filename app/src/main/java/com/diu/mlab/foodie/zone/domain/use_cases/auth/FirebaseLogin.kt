package com.diu.mlab.foodie.zone.domain.use_cases.auth

import android.util.Log
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.SuperUser
import com.diu.mlab.foodie.zone.domain.repo.AuthRepo
import com.diu.mlab.foodie.zone.util.transformedEmailId
import com.google.android.gms.auth.api.identity.SignInCredential
import javax.inject.Inject

class FirebaseLogin @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(credential: SignInCredential, success :(user: FoodieUser) -> Unit, failed :(msg : String) -> Unit){
        Log.d("TAG", "FirebaseLogin invoke: ${credential.id}")
        if (!credential.id.contains("@diu.edu.bd") && !credential.id.contains("@daffodilvarsity.edu.bd"))
            failed.invoke("Please use DIU email address.")
        else
            repo.firebaseLogin(credential, success, failed)
    }
}