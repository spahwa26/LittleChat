package com.app.littlechat.ui.onbording.login

import android.util.Log
import android.widget.Toast
import com.app.littlechat.model.CustomResult
import com.app.littlechat.model.User
import com.app.littlechat.ui.onbording.login.OnboardingViewModel.Companion.AUTH_FAIL
import com.app.littlechat.ui.onbording.login.OnboardingViewModel.Companion.FAILURE_ERROR
import com.app.littlechat.ui.onbording.login.OnboardingViewModel.Companion.GOTO_PROFILE
import com.app.littlechat.ui.onbording.login.OnboardingViewModel.Companion.VERIFICATION_EMAIL
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.LocalisedException
import com.app.littlechat.utility.SomethingWentWrongException
import com.app.littlechat.utility.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OnboardingRepository @Inject constructor(private val userPreferences: UserPreferences) {

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }


    suspend fun firebaseSignInWithEmailPassword(
        email: String,
        password: String,
        onResult: (CustomResult<Boolean>) -> Unit
    ) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                CoroutineScope(IO).launch {
                    mAuth.currentUser?.let {
                        when (mAuth.currentUser?.isEmailVerified) {
                            true -> checkNameNumber(it.uid, onResult)

                            false -> sendVerificationEmail(it, onResult)

                            else -> onResult.invoke(CustomResult.Error(exception = SomethingWentWrongException()))
                        }
                    }

                }
            } else
                onResult.invoke(
                    CustomResult.Error(
                        errorCode = FAILURE_ERROR,
                        exception = LocalisedException(task.exception?.message)
                    )
                )
        }
            .addOnFailureListener { e ->
                onResult.invoke(
                    CustomResult.Error(
                        errorCode = FAILURE_ERROR,
                        exception = LocalisedException(e.message)
                    )
                )
            }
    }

    private suspend fun checkNameNumber(
        userId: String,
        onResult: (CustomResult<Boolean>) -> Unit
    ) {
        val data = db.child("users/$userId").get().await()
        if (data.value != null) {
            try {
                db.child("users").child(userId).child("device_token")
                    .setValue(userPreferences.deviceToken)
                val pojo = data.getValue(User::class.java)
                setUserData(pojo)
                onResult.invoke(CustomResult.Success(true))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            onResult.invoke(CustomResult.Error(GOTO_PROFILE, LocalisedException(null)))
        }
    }

    private fun setUserData(pojo: User?) {
        userPreferences.isLoggedIn = true
        userPreferences.id = pojo?.id
        userPreferences.name = pojo?.name
        userPreferences.email = pojo?.email
        userPreferences.phone = pojo?.phone_number
        userPreferences.image = pojo?.image
        userPreferences.status = pojo?.status
    }

    fun signUp(email: String, password: String, onResult: (CustomResult<Boolean>) -> Unit){
        mAuth.createUserWithEmailAndPassword(email, password )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in groupDetails's information
                    Log.d("aaaaaa", "createUserWithEmail:success")
                    mAuth.currentUser?.let {
                        sendVerificationEmail(it, onResult)
                    }
                } else {
                    onResult.invoke(
                        CustomResult.Error(
                            errorCode = AUTH_FAIL,
                            exception = LocalisedException(task.exception?.message)
                        )
                    )
                }
            }
    }


    private fun sendVerificationEmail(
        user: FirebaseUser,
        onResult: (CustomResult<Boolean>) -> Unit
    ) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                CommonUtilities.hideProgressWheel()
                if (task.isSuccessful) {
                    //CommonUtilities.showToast(activity,"A verification email has been sent to "+groupDetails.getEmail()+", please verify the email then login.");
                    //startActivity(new Intent(activity,Login.class));
                    FirebaseAuth.getInstance().signOut()
                    onResult.invoke(
                        CustomResult.Error(
                            VERIFICATION_EMAIL,
                            LocalisedException(user.email)
                        )
                    )
                } else {
                    onResult.invoke(
                        CustomResult.Error(
                            errorCode = FAILURE_ERROR,
                            exception = LocalisedException(task.exception?.message)
                        )
                    )
                }
            }.addOnFailureListener { e ->
                onResult.invoke(
                    CustomResult.Error(
                        errorCode = FAILURE_ERROR,
                        exception = LocalisedException(e.message)
                    )
                )
            }
    }

}