package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {


    private var mAuth: FirebaseAuth? = null


    lateinit var activity: Activity

    private val TAG = "Login"


    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()

        listeners()
    }



    fun init() {

        mAuth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().reference

        activity = this@Login
    }

    private fun listeners() {
        iv_back.setOnClickListener {
            finish()
        }

        btn_login.setOnClickListener {

            launchHomeScreen()
        }

        iv_google.setOnClickListener {
            //            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
//            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
//            startActivityForResult(signInIntent, 101)

        }

        iv_fb.setOnClickListener {
            //login_button.performClick()
        }
    }

    private fun launchHomeScreen() {

        if (et_email.text.toString().isEmpty()) {
            et_email.error = "Enter Your Email"
            et_email.requestFocus()
            return
        }
        if (et_password.text.toString().isEmpty()) {
            et_password.error = "Enter Your Password"
            et_password.requestFocus()
            return
        }

        if (!CommonUtilities.isValidEmail(et_email.text.toString())) {
            et_email.error = "Enter Valid Email"
            et_email.requestFocus()
            return
        }
        firebaseSignInwithEmailPassword()
    }

    private fun firebaseSignInwithEmailPassword() {

        CommonUtilities.showProgressWheel(this)

        mAuth!!.signInWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            .addOnCompleteListener(
                activity
            ) { task ->
                CommonUtilities.hideProgressWheel()
                if (task.isSuccessful) {
                    if (mAuth!!.getCurrentUser()!!.isEmailVerified)
                        checkNameNumber(mAuth!!.getCurrentUser()!!.uid, mAuth!!.getCurrentUser()!!.email, "")
                    else if(mAuth?.currentUser?.isEmailVerified ==false)
                        sendVerificationEmail(mAuth!!.getCurrentUser())
                    else
                        CommonUtilities.showToast(activity, "Some error occu, Please try again.")
                } else
                    CommonUtilities.showAlert(activity, task.exception!!.message, false)
            }
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    CommonUtilities.hideProgressWheel()
                    CommonUtilities.showAlert(activity, e.message, false)
                }
            })
    }

    private fun checkNameNumber(userId: String, email: String?, name: String) {

        FirebaseDatabase.getInstance().getReference().child("users/$userId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    CommonUtilities.hideProgressWheel()
                    if (dataSnapshot.getValue() != null) {
                        try {
                            CommonUtilities.putString(activity, "isLoggedIn", "yes")
                            startActivity(
                                Intent(
                                    activity,
                                    HomeScreen::class.java
                                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        showMobilePopup(userId, email?:"", name)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, "onCancelled: ")
                    CommonUtilities.hideProgressWheel()
                }
            })
    }


    private fun showMobilePopup(userId: String, email: String, name: String) {
        startActivity(
            Intent(activity, Profile::class.java).putExtra("uid", userId)
                .putExtra("email", email).putExtra("name", name)
        )
    }


    private fun sendVerificationEmail(user : FirebaseUser?) {


        user!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    CommonUtilities.hideProgressWheel()
                    if (task.isSuccessful) {
                        //CommonUtilities.showToast(activity,"A verification email has been sent to "+user.getEmail()+", please verify the email then login.");
                        //startActivity(new Intent(activity,Login.class));
                        FirebaseAuth.getInstance().signOut()
                        CommonUtilities.showAlert(this, "A verification email has been sent to " + user.email + ", please verify the email then login.", false)
                    } else {
                        CommonUtilities.showToast(this, task.exception!!.message?:"")
                    }
                }.addOnFailureListener { e ->
                    CommonUtilities.showAlert(this, e.message?:"", false)
                    CommonUtilities.hideProgressWheel()
                }
    }


}
