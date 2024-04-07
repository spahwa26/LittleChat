package com.app.littlechat.utility

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.support.annotation.StringRes
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


fun Activity.getActivity() = this

fun Context.showToast(
    @StringRes intRes: Int? = null,
    txt: String? = null,
    length: Int = Toast.LENGTH_SHORT
) {
    val toastMsg = if (intRes != null) getString(intRes) else txt
    if (toastMsg != null) {
        var newLength = length
        if (toastMsg.length > 50) newLength = Toast.LENGTH_LONG
        Toast.makeText(this, toastMsg, newLength).show()
    }
}

fun Context.finishActivity() {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context.finish()
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    capabilities.also {
        if (it != null) {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                return true
            else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            }
        }
    }
    return false
}

@Composable
fun getColors() = MaterialTheme.colorScheme

fun Context.getResizedBitmap(
    uri: Uri,
    maxSize: Int,
    fileName: String,
    isCamera: Boolean = false
): Bitmap? {

    val options = BitmapFactory.Options()

    options.inPreferredConfig = Bitmap.Config.ARGB_8888

    val file = getFIleFromUri(uri, getImageFile(fileName))

    var saveBitmap: Bitmap? = null

    file?.absolutePath?.let { path ->
        val image: Bitmap = if (isCamera)
            CommonUtilities.rotateBitmap(BitmapFactory.decodeFile(path, options), path)
        else
            BitmapFactory.decodeFile(path, options)

        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }


        saveBitmap = Bitmap.createScaledBitmap(image, width, height, true)
        val saveFile = File(path)
        try {
            val out = FileOutputStream(saveFile)
            saveBitmap?.compress(Bitmap.CompressFormat.JPEG, 70, out)
            out.flush()
            out.close()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    return saveBitmap
}

fun Context.getImageFile(name: String) = File(cacheDir, name)

fun Context.deleteImageFile(name: String)  {
    File(cacheDir, name).let {
        if(it.exists())
            it.delete()
    }
}

fun Context.getFIleFromUri(uri: Uri, file: File): File? {
    var inputStream: InputStream? = null
    val outputStream: OutputStream
    return try {
        inputStream = contentResolver.openInputStream(uri)
        outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use {
                input.copyTo(it)
            }
        }
        file
    } catch (e: Exception) {
        null
    } finally {
        inputStream?.close()
    }
}

//fun Context.getRealPathFromURI(contentUri: Uri): String? {
//    var cursor: Cursor? = null
//    return try {
//        val proj = arrayOf(MediaStore.Images.Media.DATA)
//        cursor = contentResolver.query(contentUri, proj, null, null, null)
//        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        cursor.getString(columnIndex)
//    }catch (e : Exception){
//        null
//    } finally {
//        cursor?.close()
//    }
//}