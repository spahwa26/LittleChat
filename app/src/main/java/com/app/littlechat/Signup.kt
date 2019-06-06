package com.app.littlechat

import android.app.Activity
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
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
        progressBar.visibility= View.VISIBLE
        auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("aaaaaa", "createUserWithEmail:success")
                    val user = auth.currentUser

                    sendVerificationEmail(user)
                } else {
                    progressBar.visibility= View.GONE
                    // If sign in fails, display a message to the user.
                    Log.w("aaaaaa", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun sendVerificationEmail(user : FirebaseUser?) {


        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                progressBar.visibility= View.GONE
                if (task.isSuccessful) {
                    //CommonUtilities.showToast(activity,"A verification email has been sent to "+user.getEmail()+", please verify the email then login.");
                    //startActivity(new Intent(activity,Login.class));
                    FirebaseAuth.getInstance().signOut()
                    showAlert(this, "A verification email has been sent to " + user.email + ", please verify the email then login.", true)
                } else {
                    showToast(this, task.exception!!.message?:"")
                }
            }.addOnFailureListener { e -> showAlert(this, e.message?:"", false) }
    }


    fun showToast(activity: Activity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showAlert(activity: Activity, msg: String, isFinish: Boolean) {
        val alert = AlertDialog.Builder(activity)

        alert.setCancelable(true)

        alert.setMessage(msg)

        alert.setPositiveButton("Ok") { dialog, whichButton ->
            dialog.cancel()
            if (isFinish)
                activity.finish()
        }

        alert.show()
    }
}
