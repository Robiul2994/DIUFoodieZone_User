package com.diu.mlab.foodie.zone.di

import android.content.Context
import com.diu.mlab.foodie.zone.data.data_source.NotificationApi
import com.diu.mlab.foodie.zone.data.repo.AuthRepoImpl
import com.diu.mlab.foodie.zone.data.repo.OrderRepoImpl
import com.diu.mlab.foodie.zone.data.repo.UserMainRepoImpl
import com.diu.mlab.foodie.zone.domain.repo.AuthRepo
import com.diu.mlab.foodie.zone.domain.repo.OrderRepo
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseUser() = Firebase.auth.currentUser

    @Provides
    @Singleton
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    @Singleton
    fun provideAuthRepo(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        @ApplicationContext context: Context
    ): AuthRepo =
        AuthRepoImpl(firebaseAuth, firestore, storage, context)

    @Provides
    @Singleton
    fun provideUserMainRepo(
        realtime: FirebaseDatabase,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        @ApplicationContext context: Context
    ): UserMainRepo =
        UserMainRepoImpl(realtime, firestore, storage, context)

    @Provides
    @Singleton
    fun provideOrderRepo(
        realtime: FirebaseDatabase,
        firebaseUser: FirebaseUser?,
        api: NotificationApi,
        @ApplicationContext context: Context
    ): OrderRepo =
        OrderRepoImpl(realtime, firebaseUser, api, context)

    @Provides
    @Singleton
    fun providePortalApi(): NotificationApi{
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(NotificationApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApi::class.java)
    }


}