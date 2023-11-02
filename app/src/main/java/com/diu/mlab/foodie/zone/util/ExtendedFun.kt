package com.diu.mlab.foodie.zone.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.documentfile.provider.DocumentFile
import com.diu.mlab.foodie.zone.R
import com.google.auth.http.HttpTransportFactory
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("ClickableViewAccessibility")
fun View.setBounceClickListener(onClick: (() -> Unit)? = null){
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val scaleDownX = ObjectAnimator.ofFloat(
                    v, "scaleX", 0.9f
                )
                val scaleDownY = ObjectAnimator.ofFloat(
                    v, "scaleY", 0.9f
                )
                scaleDownX.duration = 200
                scaleDownY.duration = 200

                val scaleDown = AnimatorSet()
                scaleDown.play(scaleDownX).with(scaleDownY)
                scaleDown.start()

            }
            MotionEvent.ACTION_UP -> {
                val scaleDownX2 = ObjectAnimator.ofFloat(
                    v, "scaleX", 1f
                )
                val scaleDownY2 = ObjectAnimator.ofFloat(
                    v, "scaleY", 1f
                )
                scaleDownX2.duration = 200
                scaleDownY2.duration = 200

                val scaleDown2 = AnimatorSet()
                scaleDown2.play(scaleDownX2).with(scaleDownY2)

                scaleDown2.start()
//                v.background.setHotspot(event.x,event.y)
            }
        }
        false
    }
    this.setOnClickListener { onClick?.invoke() }
}

fun TextView.addLiveTextListener(onClick: (String) -> Unit){
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, after: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            onClick.invoke(text.toString())
        }
    })
}

fun String.transformedEmailId(): String = this.replace('.','~')

fun String.getTopic(): String =
    this.transformedEmailId()
        .replace('~','y')
        .replace('@','z')
        .toCharArray()
        .filter { it.isLetter() }
        .joinToString(separator = "")


@OptIn(DelicateCoroutinesApi::class)
fun String.getDrawable( success : (Drawable?) -> Unit) {
    GlobalScope.launch(Dispatchers.IO){
        try {
            val `is`: InputStream = URL(this@getDrawable).content as InputStream
            val drawable = Drawable.createFromStream(`is`, "dp")
            //val x = BitmapFactory.decodeStream(`is`)
            withContext(Dispatchers.Main) {
                success.invoke(drawable)
            }
        } catch (e: Exception) {
            Log.d("Exception", "getDrawable: $e")
            withContext(Dispatchers.Main) {
                success.invoke(null)
            }
        }
    }
}

fun Activity.changeStatusBarColor(colorId: Int, isLight: Boolean) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = getColor(colorId)

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
}

fun Activity.changeNavBarColor(colorId: Int, isLight: Boolean) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.navigationBarColor = getColor(colorId)

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
}

fun Context.copyUriToFile2(uri: Uri): File {
    val inputStream = contentResolver.openInputStream(uri)
    val fileName = DocumentFile.fromSingleUri(this, uri)?.name!!
    val outputFile = File(cacheDir, fileName)
    val outputStream = FileOutputStream(outputFile)

    inputStream.use { input ->
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input!!.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
    return outputFile
}

fun Context.copyUriToFile(uri: Uri): File {
    val result: String
    val cursor = contentResolver.query(uri, null, null, null, null)
    if (cursor == null) { // Source is Dropbox or other similar local file path
        result = uri.path.toString()
    } else {
        cursor.moveToFirst()
        val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return File(result)
}

fun Long.toDateTime(): String{
    var date = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(this)
    val day = SimpleDateFormat("dd", Locale.US).format(System.currentTimeMillis())
    if (day.toInt() == date.split(" ")[0].toInt())
        date = date.split(", ")[1]
    return date.toString()
}

fun Int.getDeliveryCharge(): Int =
    if(this<200) 10
    else 20
fun Int.getDeliveryCharge2(): Int =
    if(this<45) 5
    else if(this<85) 10
    else if(this<125) 20
    else if(this<165) 25
    else if(this<205) 30
    else if(this<245) 35
    else if(this<285) 40
    else if(this<325) 45
    else 50

fun Context.getAccessToken(): String{
    val scopes = listOf("https://www.googleapis.com/auth/firebase.messaging")
    val googleCredentials  = GoogleCredentials
        .fromStream( resources.openRawResource(R.raw.service_account))
        .createScoped(scopes)
    googleCredentials.refreshIfExpired()
    googleCredentials.refreshAccessToken()
    Log.d("TAG", "getAccessToken suc: ${googleCredentials.accessToken.tokenValue}")
    return googleCredentials.accessToken.tokenValue
}