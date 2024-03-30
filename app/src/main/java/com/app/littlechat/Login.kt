package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.app.littlechat.databinding.ActivityLoginBinding
import com.app.littlechat.model.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var mAuth: FirebaseAuth? = null


    private lateinit var activity: Activity

    private val TAG = "Login"


    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        init()

        listeners()
        setContentView(binding.root)
    }


    fun init() {

        mAuth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().reference

        activity = this@Login
    }

    private fun listeners() {
        binding.run {

            ivBack.setOnClickListener {
                finish()
            }

            btnLogin.setOnClickListener {

                launchHomeScreen()
            }

            ivGoogle.setOnClickListener {
                //            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
//            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
//            startActivityForResult(signInIntent, 101)

            }

            ivFb.setOnClickListener {
                //login_button.performClick()
            }
        }
    }

    private fun launchHomeScreen() {
        binding.run {

            if (etEmail.text.toString().isEmpty()) {
                etEmail.error = "Enter Your Email"
                etEmail.requestFocus()
                return
            }
            if (etPassword.text.toString().isEmpty()) {
                etPassword.error = "Enter Your Password"
                etPassword.requestFocus()
                return
            }

            if (!CommonUtilities.isValidEmail(etEmail.text.toString())) {
                etEmail.error = "Enter Valid Email"
                etEmail.requestFocus()
                return
            }
            firebaseSignInwithEmailPassword()
        }
    }

    private fun firebaseSignInwithEmailPassword() {

        CommonUtilities.showProgressWheel(this)

        mAuth!!.signInWithEmailAndPassword(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
            .addOnCompleteListener(
                activity
            ) { task ->
                CommonUtilities.hideProgressWheel()
                if (task.isSuccessful) {
                    if (mAuth!!.getCurrentUser()!!.isEmailVerified)
                        checkNameNumber(
                            mAuth!!.getCurrentUser()!!.uid,
                            mAuth!!.getCurrentUser()!!.email,
                            ""
                        )
                    else if (mAuth?.currentUser?.isEmailVerified == false)
                        sendVerificationEmail(mAuth!!.getCurrentUser())
                    else
                        CommonUtilities.showToast(activity, "Some error occu, Please try again.")
                } else
                    CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
            }
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    CommonUtilities.hideProgressWheel()
                    CommonUtilities.showAlert(activity, e.message, false, true)
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
                            FirebaseDatabase.getInstance().getReference().child("users")
                                ?.child(userId)?.child("device_token")
                                ?.setValue(CommonUtilities.getToken(activity))
                            val pojo = dataSnapshot.getValue<User>(User::class.java)
                            CommonUtilities.putString(activity, "isLoggedIn", "yes")
                            setUserData(pojo)
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
                        startActivity(
                            Intent(activity, Profile::class.java).putExtra("uid", userId)
                                .putExtra("email", email).putExtra("name", name)
                        )
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, "onCancelled: ")
                    CommonUtilities.hideProgressWheel()
                }
            })
    }

    private fun setUserData(pojo: User?) {
        CommonUtilities.putString(activity, Constants.ID, pojo?.id)
        CommonUtilities.putString(activity, Constants.NAME, pojo?.name)
        CommonUtilities.putString(activity, Constants.EMAIL, pojo?.email)
        CommonUtilities.putString(activity, Constants.PHONE, pojo?.phone_number)
        CommonUtilities.putString(activity, Constants.IMAGE, pojo?.image)
        CommonUtilities.putString(activity, Constants.STATUS, pojo?.status)
    }


    private fun sendVerificationEmail(user: FirebaseUser?) {


        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                CommonUtilities.hideProgressWheel()
                if (task.isSuccessful) {
                    //CommonUtilities.showToast(activity,"A verification email has been sent to "+groupDetails.getEmail()+", please verify the email then login.");
                    //startActivity(new Intent(activity,Login.class));
                    FirebaseAuth.getInstance().signOut()
                    CommonUtilities.showAlert(
                        this,
                        "A verification email has been sent to " + user.email + ", please verify the email then login.",
                        false,
                        true
                    )
                } else {
                    CommonUtilities.showToast(this, task.exception!!.message ?: "")
                }
            }.addOnFailureListener { e ->
                CommonUtilities.showAlert(this, e.message ?: "", false, true)
                CommonUtilities.hideProgressWheel()
            }
    }


}
