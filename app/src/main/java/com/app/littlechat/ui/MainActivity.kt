package com.app.littlechat.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.littlechat.BuildConfig
import com.app.littlechat.HomeScreen
import com.app.littlechat.Login
import com.app.littlechat.R
import com.app.littlechat.Signup
import com.app.littlechat.databinding.ActivityMainBinding
import com.app.littlechat.utility.CommonUtilities
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this@MainActivity
        if (CommonUtilities.getString(activity, "isLoggedIn").equals("yes"))
            startActivity(
                Intent(
                    activity,
                    HomeScreen::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        else {
            binding = ActivityMainBinding.inflate(layoutInflater)

            generateKeyHash()

            CommonUtilities.underlineTextView(
                binding.tvTerms,
                getString(R.string.terms_and_conditions)
            )

            listeners()
            setContentView(binding.root)
        }

    }

    private fun listeners() {
        binding.run {

            btnLogin.setOnClickListener {
                if (!cbTerms.isChecked) {
                    CommonUtilities.showAlert(
                        activity,
                        "Please indicate that you have read and agree to the Terms & Conditions",
                        false, true
                    )
                } else
                    startActivity(Intent(activity, Login::class.java))
            }

            btnSignup.setOnClickListener {
                if (!cbTerms!!.isChecked) {
                    CommonUtilities.showAlert(
                        activity,
                        "Please indicate that you have read and agree to the Terms & Conditions",
                        false, true
                    )
                } else
                    startActivity(Intent(activity, Signup::class.java))
            }


            tvTerms.setOnClickListener {
                val url = "http://www.google.com"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
    }

    private fun generateKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID,
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
