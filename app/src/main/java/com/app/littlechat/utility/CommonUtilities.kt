//package com.app.littlechat.utility
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import android.graphics.Matrix
//import android.graphics.Point
//import android.graphics.drawable.ColorDrawable
//import android.media.ExifInterface
//import android.net.ConnectivityManager
//import android.text.SpannableString
//import android.text.TextUtils
//import android.text.style.UnderlineSpan
//import android.util.Log
//import android.view.ViewGroup
//import android.view.Window
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.DefaultItemAnimator
//import androidx.recyclerview.widget.RecyclerView
//import com.app.littlechat.BuildConfig
//import com.app.littlechat.R
//import com.app.littlechat.data.model.User
//import java.io.File
//import java.io.FileNotFoundException
//import java.io.FileOutputStream
//import java.io.IOException
//
//
//class CommonUtilities {
//
//    companion object {
//
//
//        fun isNetworkConnected(context: Context) : Boolean{
//            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val activeNetwork = cm.activeNetworkInfo
//            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
//            return isConnected
//        }
//
//        private var dialog: Dialog? = null
//
//
//        fun putString(activity: Context, name: String, value: String?) {
//            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
//
//            preferences.edit().putString(name, value).apply()
//        }
//
//        fun getString(activity: Context, name: String): String {
//            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
//
//            return preferences.getString(name, "")?:""
//        }
//
//
//        fun putToken(activity: Context, value: String?) {
//            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID+"+token", Context.MODE_PRIVATE)
//
//            preferences.edit().putString("token", value).apply()
//        }
//
//        fun getToken(activity: Context): String {
//            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID+"+token", Context.MODE_PRIVATE)
//
//            return preferences.getString("token", "")?:""
//        }
//
//        fun clearPrefrences(activity: Activity) {
//            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
//
//            preferences.edit().clear().apply()
//        }
//
//
//        fun showToast(activity: Activity, message: String) {
//            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
//        }
//
//
//        fun isValidEmail(target: CharSequence): Boolean {
//            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
//        }
//
//        fun underlineTextView(textView: TextView, text: String) {
//            val content = SpannableString(text)
//            content.setSpan(UnderlineSpan(), 0, content.length, 0)
//            textView.text = content
//        }
//
//
//        fun showAlert(activity: Activity, msg: String?, isFinish: Boolean, isCancelable : Boolean) {
//            val alert = AlertDialog.Builder(activity)
//
//            alert.setCancelable(isCancelable)
//
//            alert.setMessage(msg)
//
//            alert.setPositiveButton("Ok") { dialog, whichButton ->
//                dialog.cancel()
//                if (isFinish)
//                    activity.finish()
//            }
//
//            alert.show()
//        }
//
//        fun setLayoutManager(view: RecyclerView, manager: RecyclerView.LayoutManager) {
//            view.layoutManager = manager
//            view.itemAnimator = DefaultItemAnimator()
//            view.isNestedScrollingEnabled = false
//        }
//
//
//        fun hideProgressWheel() {
//            try {
//                if (dialog != null)
//                    dialog!!.dismiss()
//            } catch (e: Exception) {
//
//            }
//
//        }
//
//
//        fun getResizedBitmap(
//            path: String,
//            maxSize: Int,
//            fileName: String,
//            activity: Activity,
//            isCamera: Boolean
//        ): String {
//
//            val options = BitmapFactory.Options()
//
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888
//            val image: Bitmap = if (isCamera)
//                rotateBitmap(BitmapFactory.decodeFile(path, options), path)
//            else
//                BitmapFactory.decodeFile(path, options)
//
//            var width = image.width
//            var height = image.height
//
//            val bitmapRatio = width.toFloat() / height.toFloat()
//            if (bitmapRatio > 1) {
//                width = maxSize
//                height = (width / bitmapRatio).toInt()
//            } else {
//                height = maxSize
//                width = (height * bitmapRatio).toInt()
//            }
//
//
//            val saveBitmap = Bitmap.createScaledBitmap(image, width, height, true)
//
//            val cacheDir = activity.baseContext.cacheDir
//
//            val saveFile = File(cacheDir, fileName)
//
//            try {
//                val out = FileOutputStream(saveFile)
//                saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//                out.flush()
//                out.close()
//
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            return saveFile.path
//        }
//
//
//        fun rotateBitmap(realImage: Bitmap, imagePath: String): Bitmap {
//            var realImage = realImage
//            try {
//                val exif = ExifInterface(imagePath)
//
//                Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION)?:"")
//                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true)) {
//
//                    realImage = rotate(realImage, 90)
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("8", ignoreCase = true)) {
//                    realImage = rotate(realImage, 270)
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("3", ignoreCase = true)) {
//                    realImage = rotate(realImage, 180)
//                }
//                return realImage
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            return realImage
//        }
//
//        fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
//            val w = bitmap.width
//            val h = bitmap.height
//
//            val mtx = Matrix()
//            mtx.postRotate(degree.toFloat())
//
//            return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
//        }
//
//        fun getUserData(activity: Activity): User {
//            return User(
//                getString(activity, Constants.SENDER_ID),
//                getString(activity, Constants.NAME),
//                getString(activity, Constants.EMAIL),
//                getString(activity, Constants.PHONE_NUMBER),
//                getString(activity, Constants.IMAGE),
//                getString(activity, Constants.STATUS)
//            )
//        }
//
//    }
//
//
//}