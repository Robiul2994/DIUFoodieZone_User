package com.diu.mlab.foodie.zone.domain.use_cases.auth

import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val firebaseLogin: FirebaseLogin,
    val firebaseSignup: FirebaseSignup,
    val googleSignIn: GoogleSignIn,
    val getTeacherInfo: GetTeacherInfo
)
