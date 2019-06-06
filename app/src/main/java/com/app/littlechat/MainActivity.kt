package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this@MainActivity

        if (CommonUtilities.getString(activity, "isLoggedIn").equals("yes"))
            startActivity(
                Intent(activity, HomeScreen::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        else {

            setContentView(R.layout.activity_main)

            generateKeyHash()

            CommonUtilities.underlineTextView(tv_terms, getString(R.string.terms_and_conditions))

            listeners()
        }

    }

    private fun listeners() {

        btn_login.setOnClickListener { if (!cb_terms!!.isChecked) {
            CommonUtilities.showAlert(
                activity,
                "Please indicate that you have read and agree to the Terms & Conditions",
                false
            )
        } else
            startActivity(Intent(activity, Login::class.java)) }

        btn_signup.setOnClickListener { if (!cb_terms!!.isChecked) {
            CommonUtilities.showAlert(
                activity,
                "Please indicate that you have read and agree to the Terms & Conditions",
                false
            )
        } else
            startActivity(Intent(activity, Signup::class.java)) }


        tv_terms.setOnClickListener {
            val url = "http://www.google.com"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

    private fun generateKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                "com.seraphic.housetag",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }
}
