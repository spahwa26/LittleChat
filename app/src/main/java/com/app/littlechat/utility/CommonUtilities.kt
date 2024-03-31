package com.app.littlechat.utility

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.BuildConfig
import com.app.littlechat.ui.MainActivity
import com.app.littlechat.R
import com.app.littlechat.data.model.User
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CommonUtilities {

    companion object {


        fun isNetworkConnected(activity: Activity) : Boolean{
            var cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

        private var dialog: Dialog? = null


        fun putString(activity: Context, name: String, value: String?) {
            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

            preferences.edit().putString(name, value).apply()
        }

        fun getString(activity: Context, name: String): String {
            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

            return preferences.getString(name, "")?:""
        }


        fun putToken(activity: Context, value: String?) {
            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID+"+token", Context.MODE_PRIVATE)

            preferences.edit().putString("token", value).apply()
        }

        fun getToken(activity: Context): String {
            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID+"+token", Context.MODE_PRIVATE)

            return preferences.getString("token", "")?:""
        }

        fun clearPrefrences(activity: Activity) {
            val preferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

            preferences.edit().clear().apply()
        }


        fun showLogoutPopup(activity: Activity) {
            val alert = AlertDialog.Builder(activity)

            alert.setCancelable(true)

            alert.setMessage("Do you want to logout?")

            alert.setPositiveButton("Yes") { dialog, whichButton ->
                clearPrefrences(activity)
                activity.startActivity(
                    Intent(
                        activity,
                        MainActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }

            alert.setNegativeButton(
                "No"
            ) { dialog, whichButton -> dialog.cancel() }


            alert.show()
        }

        fun showToast(activity: Activity, message: String) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }


        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun underlineTextView(textView: TextView, text: String) {
            val content = SpannableString(text)
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            textView.text = content
        }


        fun showAlert(activity: Activity, msg: String?, isFinish: Boolean, isCancelable : Boolean) {
            val alert = AlertDialog.Builder(activity)

            alert.setCancelable(isCancelable)

            alert.setMessage(msg)

            alert.setPositiveButton("Ok") { dialog, whichButton ->
                dialog.cancel()
                if (isFinish)
                    activity.finish()
            }

            alert.show()
        }

        fun setLayoutManager(view: RecyclerView, manager: RecyclerView.LayoutManager) {
            view.layoutManager = manager
            view.itemAnimator = DefaultItemAnimator()
            view.isNestedScrollingEnabled = false
        }


        fun showProgressWheel(context: Context) {
            hideProgressWheel()

            dialog = Dialog(context)

            dialog!!.setCancelable(false)

            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog!!.setContentView(R.layout.layout_progress_wheel)

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val display = (context as Activity).windowManager.defaultDisplay

            val size = Point()

            display.getSize(size)

            val width = size.x

            dialog!!.window!!.setLayout(
                width - 60,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )// set width of alert dialog box nad get width dynamically

            dialog!!.show()
        }

        fun hideProgressWheel() {
            try {
                if (dialog != null)
                    dialog!!.dismiss()
            } catch (e: Exception) {

            }

        }


        fun getResizedBitmap(
            path: String,
            maxSize: Int,
            fileName: String,
            activity: Activity,
            isCamera: Boolean
        ): String {

            val options = BitmapFactory.Options()

            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val image: Bitmap
            if (isCamera)
                image = CommonUtilities.rotateBitmap(BitmapFactory.decodeFile(path, options), path)
            else
                image = BitmapFactory.decodeFile(path, options)

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


            val saveBitmap = Bitmap.createScaledBitmap(image, width, height, true)

            val cacheDir = activity.baseContext.cacheDir

            val saveFile = File(cacheDir, fileName)

            try {
                val out = FileOutputStream(saveFile)
                saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return saveFile.path
        }


        fun rotateBitmap(realImage: Bitmap, imagePath: String): Bitmap {
            var realImage = realImage
            try {
                val exif = ExifInterface(imagePath)

                Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION)?:"")
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true)) {

                    realImage = rotate(realImage, 90)
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("8", ignoreCase = true)) {
                    realImage = rotate(realImage, 270)
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("3", ignoreCase = true)) {
                    realImage = rotate(realImage, 180)
                }
                return realImage
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return realImage
        }

        fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
            val w = bitmap.width
            val h = bitmap.height

            val mtx = Matrix()
            mtx.postRotate(degree.toFloat())

            return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
        }

        fun getUserData(activity: Activity): User {
            return User(
                getString(activity, Constants.ID),
                getString(activity, Constants.NAME),
                getString(activity, Constants.EMAIL),
                getString(activity, Constants.PHONE),
                getString(activity, Constants.IMAGE),
                getString(activity, Constants.STATUS)
            )
        }

    }


}