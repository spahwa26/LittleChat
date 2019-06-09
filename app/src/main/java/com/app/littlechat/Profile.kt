package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.app.littlechat.pojo.Friends
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.Constants
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File


class Profile : AppCompatActivity() {


    lateinit var activity: Activity
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var name: String = ""
    private var email: String = ""
    private var userID: String = ""
    private var imagePath: String = ""
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()

        setData()

        listeners()
    }

    private fun init() {

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance().reference

        activity = this@Profile

    }

    private fun setData() {

        if (intent.hasExtra("data")) {
            userID = mAuth?.getCurrentUser()!!.uid
            user = intent.getParcelableExtra("data")
            et_name.setText(user.name)
            et_email.setText(user.email)
            et_phone.setText(user.phone_number)
            Picasso.get().load(user.image).placeholder(R.mipmap.ic_launcher).into(ivImage)

            et_name.isFocusable = false
            et_email.isFocusable = false
            et_phone.isFocusable = false
            ivImage.isEnabled = false

            findUserInRequestList()
        } else if (intent.hasExtra("name")) {
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
            btn_submit.visibility = VISIBLE
        } else {
            userID = mAuth?.getCurrentUser()!!.uid

            getUserDetail()

            btn_submit.visibility = VISIBLE
            btn_submit.text = "Update"
        }
    }

    private fun listeners() {
        iv_back.setOnClickListener { finish() }
        btn_submit.setOnClickListener {

            if (isInformationFilled()) {
                if (imagePath.isEmpty())
                    submitProfile()
                else
                    uploadImages()
            }
        }
        btn_logout.setOnClickListener { CommonUtilities.showLogoutPopup(activity) }

        ivImage.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
        }

        btn_send.setOnClickListener { sendRequest() }

        btn_cancel.setOnClickListener { cancelRequest() }
    }

    private fun isInformationFilled(): Boolean {
        if (!CommonUtilities.isValidEmail(et_email.getText().toString())) {
            et_email.setError("Enter a Valid Email")
            et_email.requestFocus()
            return false
        }
        if (et_name.getText().toString().isEmpty()) {
            et_name.setError("Enter Your Name")
            et_name.requestFocus()
            return false
        }
        if (et_phone.getText().toString().length != 10) {
            et_phone.setError("Enter a 10 Digits Mobile Number.")
            et_phone.requestFocus()
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                imagePath = result.uri.path
                imagePath = CommonUtilities.getResizedBitmap(imagePath, 800, mAuth?.uid + "__ProfilePic.jpg", this, false)
                ivImage.setImageURI(result.uri)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    //////////////////////////////////////////////////////Firebase Calls///////////////////////////////////////////////////////////



    private fun sendRequest() {
        CommonUtilities.showProgressWheel(activity)
        mDatabase?.child(Constants.REQUESTS)?.child(userID)?.child(user.id)?.setValue(
                Friends(user.id, user.name, user.email, user.phone_number, user.image, Constants.SENT)
        )?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                CommonUtilities.showToast(activity, "Request Sent")
                mDatabase?.child(Constants.REQUESTS)?.child(user.id)?.child(userID)?.setValue(
                    Friends(user.id, user.name, user.email, user.phone_number, user.image, Constants.SENT)
                )
                btn_send.visibility= GONE
                btn_cancel.visibility = VISIBLE
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false)
        }
    }

    private fun cancelRequest() {
        CommonUtilities.showProgressWheel(activity)
        mDatabase?.child(Constants.REQUESTS)?.child(userID)?.child(user.id)?.removeValue()?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                mDatabase?.child(Constants.REQUESTS)?.child(user.id)?.child(userID)?.removeValue()
                CommonUtilities.showToast(activity, "Request Canceled")
                btn_send.visibility= VISIBLE
                btn_cancel.visibility = GONE
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false)
        }
    }

    private fun findUserInRequestList() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS).child(userID).child(user.id)
        ref.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            CommonUtilities.hideProgressWheel()
                            val data = dataSnapshot.getValue(Friends::class.java)
                                    ?: Friends("", "", "", "", "", "")
                            if (data.status.equals(Constants.RECEIVED))
                                btn_accept.visibility = VISIBLE
                            else
                                btn_cancel.visibility = VISIBLE
                        } else
                            findUserFriendList()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        CommonUtilities.hideProgressWheel()
                        //handle databaseError
                    }
                })
    }

    private fun findUserFriendList() {
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS).child(userID).child(user.id)
        ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        CommonUtilities.hideProgressWheel()

                        if (dataSnapshot.getValue() != null) {
                            val data = dataSnapshot.getValue(Friends::class.java)
                                    ?: Friends("", "", "", "", "", "")
                            btn_message.visibility = VISIBLE
                        } else
                            btn_send.visibility = VISIBLE
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        CommonUtilities.hideProgressWheel()
                        //handle databaseError
                    }
                })
    }

    private fun submitProfile() {


        CommonUtilities.showProgressWheel(activity)
        mDatabase?.child("users")?.child(userID)?.setValue(
                User(userID, et_name.getText().toString(), et_email.getText().toString(), et_phone.getText().toString(), imagePath)
        )?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                if (intent.hasExtra("name")) {
                    CommonUtilities.putString(activity, "isLoggedIn", "yes")
                    startActivity(
                            Intent(
                                    activity,
                                    HomeScreen::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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

                        val pojo = dataSnapshot.getValue<User>(User::class.java)

                        et_name.setText(pojo?.name)

                        et_email.setText(pojo?.email)

                        et_phone.setText(pojo?.phone_number)

                        et_email.setEnabled(false)

                        Picasso.get().load(pojo?.image).placeholder(R.mipmap.ic_launcher).into(ivImage)

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        CommonUtilities.hideProgressWheel()
                        //handle databaseError
                    }
                })
    }

    private fun uploadImages() {
        val file = Uri.fromFile(File(imagePath))
        val storageRef = FirebaseStorage.getInstance().reference
        val riversRef = storageRef.child("images/" + file.lastPathSegment)
        val uploadTask = riversRef.putFile(file)
        CommonUtilities.showProgressWheel(activity)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation riversRef.downloadUrl
        }).addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                imagePath = task.result.toString()
                submitProfile()
            } else {

                Log.e("Failiure", "")
            }
        }
    }
}
