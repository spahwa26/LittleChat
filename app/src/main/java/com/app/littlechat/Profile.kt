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
import com.app.littlechat.databinding.ActivityProfileBinding
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File


class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    lateinit var activity: Activity
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var name: String = ""
    private var email: String = ""
    private var userID: String = ""
    private var imagePath: String = ""
    lateinit var otherUser: User
    lateinit var myData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        init()

        setData()

        listeners()
        setContentView(binding.root)
    }

    private fun init() {

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance().reference

        activity = this@Profile

    }

    private fun setData() {
        binding.run {

            if (intent.hasExtra("data")) {
                userID = mAuth?.getCurrentUser()!!.uid
                getUserDetail(false)
                otherUser = intent.getParcelableExtra("data")!!
                etName.setText(otherUser.name)
                etEmail.setText(otherUser.email)
                etPhone.setText(otherUser.phone_number)
                if (!otherUser.image.isEmpty())
                    Picasso.get().load(otherUser.image).placeholder(R.mipmap.ic_launcher)
                        .into(ivImage)
                etName.isFocusable = false
                etEmail.isFocusable = false
                etPhone.isFocusable = false
                ivImage.isEnabled = false
                findUserInRequestList()
            } else if (intent.hasExtra("name")) {
                btnLogout.visibility = View.GONE
                name = intent.getStringExtra("name") ?: ""
                email = intent.getStringExtra("email") ?: ""
                userID = intent.getStringExtra("uid") ?: ""

                if (!name.isEmpty()) {
                    etName.setText(name)
                    etName.setEnabled(false)
                }
                if (!email.isEmpty()) {
                    etEmail.setText(email)
                    etEmail.setEnabled(false)
                }
                btnSubmit.visibility = VISIBLE
            } else {
                userID = mAuth?.getCurrentUser()!!.uid
                getUserDetail(true)
                btnSubmit.visibility = VISIBLE
                btnSubmit.text = "Update"
            }
        }
    }

    private fun listeners() {
        binding.run {
            ivBack.setOnClickListener { finish() }
            btnSubmit.setOnClickListener {

                if (isInformationFilled()) {
                    if (imagePath.isEmpty())
                        submitProfile()
                    else
                        uploadImages()
                }
            }
            btnLogout.setOnClickListener { CommonUtilities.showLogoutPopup(activity) }
            ivImage.setOnClickListener {
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity)
            }
            btnSend.setOnClickListener { sendRequest() }
            btnCancel.setOnClickListener { cancelRequest() }
        }
    }

    private fun isInformationFilled(): Boolean {
        binding.run {
            if (!CommonUtilities.isValidEmail(etEmail.getText().toString())) {
                etEmail.setError("Enter a Valid Email")
                etEmail.requestFocus()
                return false
            }
            if (etName.getText().toString().isEmpty()) {
                etName.setError("Enter Your Name")
                etName.requestFocus()
                return false
            }
            if (etPhone.getText().toString().length != 10) {
                etPhone.setError("Enter a 10 Digits Mobile Number.")
                etPhone.requestFocus()
                return false
            }
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                imagePath = result.uri.path ?: ""
                imagePath = CommonUtilities.getResizedBitmap(
                    imagePath,
                    800,
                    mAuth?.uid + "__ProfilePic.jpg",
                    this,
                    false
                )
                binding.ivImage.setImageURI(result.uri)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun setUserData(pojo: User?) {
        CommonUtilities.putString(activity, Constants.ID, pojo?.id)
        CommonUtilities.putString(activity, Constants.NAME, pojo?.name)
        CommonUtilities.putString(activity, Constants.EMAIL, pojo?.email)
        CommonUtilities.putString(activity, Constants.PHONE, pojo?.phone_number)
        CommonUtilities.putString(activity, Constants.IMAGE, pojo?.image)
        CommonUtilities.putString(activity, Constants.STATUS, pojo?.status)
    }

    //////////////////////////////////////////////////////Firebase Calls///////////////////////////////////////////////////////////


    private fun sendRequest() {
        CommonUtilities.showProgressWheel(activity)
        mDatabase?.child(Constants.REQUESTS)?.child(userID)?.child(otherUser.id)?.setValue(
            User(
                otherUser.id,
                otherUser.name,
                otherUser.email,
                otherUser.phone_number,
                otherUser.image,
                Constants.SENT
            )
        )?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                CommonUtilities.showToast(activity, "Request Sent")
                mDatabase?.child(Constants.REQUESTS)?.child(otherUser.id)?.child(userID)?.setValue(
                    User(
                        myData.id,
                        myData.name,
                        myData.email,
                        myData.phone_number,
                        myData.image,
                        Constants.RECEIVED
                    )
                )
                binding.btnSend.visibility = GONE
                binding.btnCancel.visibility = VISIBLE
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false, true)
        }
    }

    private fun cancelRequest() {
        CommonUtilities.showProgressWheel(activity)
        mDatabase?.child(Constants.REQUESTS)?.child(userID)?.child(otherUser.id)?.removeValue()
            ?.addOnCompleteListener { task ->
                CommonUtilities.hideProgressWheel()
                if (task.isSuccessful) {
                    mDatabase?.child(Constants.REQUESTS)?.child(otherUser.id)?.child(userID)
                        ?.removeValue()
                    CommonUtilities.showToast(activity, "Request Canceled")
                    binding.btnSend.visibility = VISIBLE
                    binding.btnCancel.visibility = GONE
                } else
                    CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
            }?.addOnFailureListener { e ->
                CommonUtilities.hideProgressWheel()
                CommonUtilities.showAlert(activity, e.message, false, true)
            }
    }

    private fun findUserInRequestList() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS).child(userID)
            .child(otherUser.id)
        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        CommonUtilities.hideProgressWheel()
                        val data = dataSnapshot.getValue(User::class.java)
                            ?: User("", "", "", "", "", "")
                        if (data.status.equals(Constants.RECEIVED))
                            binding.btnAccept.visibility = VISIBLE
                        else
                            binding.btnCancel.visibility = VISIBLE
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
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS).child(userID)
            .child(otherUser.id)
        ref.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    CommonUtilities.hideProgressWheel()

                    if (dataSnapshot.getValue() != null) {
                        val data = dataSnapshot.getValue(User::class.java)
                            ?: User("", "", "", "", "", "")
                        binding.btnMessage.visibility = VISIBLE
                    } else
                        binding.btnSend.visibility = VISIBLE
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CommonUtilities.hideProgressWheel()
                    //handle databaseError
                }
            })
    }

    private fun submitProfile() {


        CommonUtilities.showProgressWheel(activity)

        val user = User(
            userID,
            binding.etName.getText().toString(),
            binding.etEmail.getText().toString(),
            binding.etPhone.getText().toString(),
            imagePath,
            ""
        )

        mDatabase?.child("users")?.child(userID)?.setValue(user)?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            task.result
            if (task.isSuccessful) {
                setUserData(user)
                mDatabase?.child("users")?.child(userID)?.child("device_token")
                    ?.setValue(CommonUtilities.getToken(activity))
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
                CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false, true)
        }
    }

    private fun getUserDetail(setData: Boolean) {
        binding.run {
            val pojo = CommonUtilities.getUserData(activity)

            if (setData) {
                CommonUtilities.hideProgressWheel()

                etName.setText(pojo.name)

                etEmail.setText(pojo.email)

                etPhone.setText(pojo.phone_number)

                imagePath = pojo.image ?: ""

                etEmail.setEnabled(false)
                if (!pojo.image.equals(""))
                    Picasso.get().load(pojo.image).placeholder(R.mipmap.ic_launcher).into(ivImage)
            } else
                myData = pojo
        }


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
