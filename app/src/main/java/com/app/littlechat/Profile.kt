package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.littlechat.pojo.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity() {


    lateinit var activity: Activity
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var name: String = ""
    private var email: String = ""
    private var userID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()

        setData()

        listeners()
    }

    private fun listeners() {
        iv_back.setOnClickListener { finish() }
        btn_submit.setOnClickListener { submitProfile() }
        btn_logout.setOnClickListener { CommonUtilities.showLogoutPopup(activity) }
    }

    private fun init() {

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance().reference

        activity = this@Profile

    }

    private fun setData() {
        if (intent.hasExtra("name")) {
            btn_logout.setVisibility(View.GONE)
            name = intent.getStringExtra("name")
            email = intent.getStringExtra("email")
            userID = intent.getStringExtra("uid")

            if (!name.isEmpty()) {
                et_name.setText(name)
                et_name.setEnabled(false)
            }
            if (!email.isEmpty()) {
                et_email.setText(email)
                et_email.setEnabled(false)
            }
        } else {
            userID = mAuth?.getCurrentUser()!!.uid

            getUserDetail()

        }
    }


    private fun submitProfile() {
        if (!CommonUtilities.isValidEmail(et_email.getText().toString())) {
            et_email.setError("Enter a Valid Email")
            et_email.requestFocus()
            return
        }
        if (et_name.getText().toString().isEmpty()) {
            et_name.setError("Enter Your Name")
            et_name.requestFocus()
            return
        }
        if (et_phone.getText().toString().length != 10) {
            et_phone.setError("Enter a 10 Digits Mobile Number.")
            et_phone.requestFocus()
            return
        }

        CommonUtilities.showProgressWheel(activity)
        mDatabase?.child("users")?.child(userID)?.setValue(
            User(et_name.getText().toString(), et_email.getText().toString(), et_phone.getText().toString())
        )?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                if (intent.hasExtra("name")) {
                    CommonUtilities.putString(activity, "isLoggedIn", "yes")
                    startActivity(Intent(activity, HomeScreen::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else
                    CommonUtilities.showToast(activity, "Profile Updated Successfully")
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false)
        }
    }

    private fun getUserDetail() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(userID)
        ref.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    CommonUtilities.hideProgressWheel()

                    val pojo = dataSnapshot.getValue<User>(User::class.java!!)

                    et_name.setText(pojo!!.name)

                    et_email.setText(pojo!!.email)

                    et_phone.setText(pojo!!.phone_number)

                    et_email.setEnabled(false)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CommonUtilities.hideProgressWheel()
                    //handle databaseError
                }
            })
    }
}
