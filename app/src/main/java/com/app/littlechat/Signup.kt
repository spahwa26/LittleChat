package com.app.littlechat


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.app.littlechat.utility.CommonUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_signup.*

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()


        listeners()
    }

    private fun listeners() {
        btnSignUp.setOnClickListener {
            if(isValid())
                sighnUp()
            else
                Toast.makeText(this, "Please fill required information.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValid(): Boolean {
        if(TextUtils.isEmpty(etEmail.text) || !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches())
            return false


        return etPassword.text.isNotEmpty()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

    fun sighnUp() {

        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Enter Your Email")
            etEmail.requestFocus()
            return
        }

        if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError("Enter Your Password")
            etPassword.requestFocus()
            return
        }

        if (etConfirmPassword.getText().toString().isEmpty()) {
            etConfirmPassword.setError("Confirm Password")
            etConfirmPassword.requestFocus()
            return
        }

        if (!CommonUtilities.isValidEmail(etEmail.getText().toString())) {
            etEmail.setError("Enter Valid Email")
            etEmail.requestFocus()
            return
        }

        if (etPassword.getText().toString() == etConfirmPassword.getText().toString()) {

            CommonUtilities.showProgressWheel(this)

            auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in otherUser's information
                            Log.d("aaaaaa", "createUserWithEmail:success")
                            val user = auth.currentUser

                            sendVerificationEmail(user)
                        } else {
                            CommonUtilities.hideProgressWheel()
                            // If sign in fails, display a message to the otherUser.
                            Log.w("aaaaaa", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
        }
        else
            CommonUtilities.showToast(this, "Passwords does not match.")
    }


    private fun sendVerificationEmail(user : FirebaseUser?) {


        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                CommonUtilities.hideProgressWheel()
                if (task.isSuccessful) {
                    //CommonUtilities.showToast(activity,"A verification email has been sent to "+otherUser.getEmail()+", please verify the email then login.");
                    //startActivity(new Intent(activity,Login.class));
                    FirebaseAuth.getInstance().signOut()
                    CommonUtilities.showAlert(this, "A verification email has been sent to " + user.email + ", please verify the email then login.", true)
                } else {
                    CommonUtilities.showToast(this, task.exception!!.message?:"")
                }
            }.addOnFailureListener { e ->
                    CommonUtilities.showAlert(this, e.message?:"", false)
                    CommonUtilities.hideProgressWheel()
                }
    }



}
