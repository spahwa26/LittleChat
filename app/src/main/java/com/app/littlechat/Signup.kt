package com.app.littlechat


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.littlechat.databinding.ActivitySignupBinding
import com.app.littlechat.utility.CommonUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()


        listeners()
        setContentView(binding.root)
    }

    private fun listeners() {
        binding.btnSignUp.setOnClickListener {
            if (isValid())
                sighnUp()
            else
                Toast.makeText(this, "Please fill required information.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValid(): Boolean {
        if (TextUtils.isEmpty(binding.etEmail.text) || !android.util.Patterns.EMAIL_ADDRESS.matcher(
                binding.etEmail.text
            ).matches()
        )
            return false


        return binding.etPassword.text.isNotEmpty()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

    fun sighnUp() {
        binding.run {
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

                CommonUtilities.showProgressWheel(this@Signup)

                auth.createUserWithEmailAndPassword(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
                    .addOnCompleteListener(this@Signup) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in groupDetails's information
                            Log.d("aaaaaa", "createUserWithEmail:success")
                            val user = auth.currentUser

                            sendVerificationEmail(user)
                        } else {
                            CommonUtilities.hideProgressWheel()
                            // If sign in fails, display a message to the groupDetails.
                            Log.w("aaaaaa", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else
                CommonUtilities.showToast(this@Signup, "Passwords does not match.")
        }
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
                        true,
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
