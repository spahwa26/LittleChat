package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.CreateGroupAdapter
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.GroupDetails
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.layout_participants.view.*
import java.io.File
import java.util.ArrayList

class CreateGroup : AppCompatActivity(), AppInterface {


    lateinit var activity: Activity

    internal var friendList = ArrayList<User>()

    lateinit var adapter: CreateGroupAdapter

    private var userID: String = ""

    private var imagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        init()

        listeners()
    }

    private fun listeners() {
        ivIcon.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
        }

        ivDone.setOnClickListener {

            val participantsList = getParicipents()

            if (etGrpName.text.isEmpty()) {
                CommonUtilities.showToast(activity, "Please enter group name.")
                return@setOnClickListener
            }
            if (participantsList.size<=1) {
                CommonUtilities.showToast(activity, "Please select participants.")
                return@setOnClickListener
            }

            val createdAt = System.currentTimeMillis()

           uploadImages(participantsList, createdAt)
        }
    }

    private fun getParicipents(): ArrayList<String> {

        var list = ArrayList<String>()

        list.add(userID)

        for (user in friendList) {
            if (user.isAdded)
                list.add(user.id)
        }

        return list

    }


    private fun init() {

        activity = this
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = CreateGroupAdapter()
        adapter.setData(this@CreateGroup, friendList, this)
        rvFriends.adapter = adapter

        CommonUtilities.setLayoutManager(rvFriends, LinearLayoutManager(this))

        getFriends()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                imagePath = result.uri.path
                imagePath = CommonUtilities.getResizedBitmap(imagePath, 800, userID + "__group_icon.jpg", this, false)
                ivIcon.setImageURI(result.uri)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun getFriends() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS).child(userID)
        ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CommonUtilities.hideProgressWheel()
                        friendList.clear()
                        if (dataSnapshot.getValue() != null) {
                            try {
                                for (user in dataSnapshot.children) {
                                    val savedUser = user.getValue(User::class.java)
                                            ?: User("", "", "", "", "", "")
                                    if (user.key.equals(dataSnapshot.children.last().key))
                                        getUsersData(user.key ?: "", true, savedUser.status)
                                    else
                                        getUsersData(user.key ?: "", false, savedUser.status)
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            adapter.notifyDataSetChanged()
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        CommonUtilities.hideProgressWheel()
                        //handle databaseError
                    }
                })
    }

    private fun getUsersData(id: String, notify: Boolean, status: String) {
        FirebaseDatabase.getInstance().getReference().child("users/$id")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            try {
                                val user = dataSnapshot.getValue(User::class.java)
                                        ?: User("", "", "", "", "", "")
                                user.status = status
                                friendList.add(user)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (notify)
                            adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("onCancelled", "onCancelled: ")
                    }
                })
    }


    private fun uploadImages(participantsList : ArrayList<String>, createdAt : Long) {
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
                createGroup(participantsList, createdAt)
            } else {

                Log.e("Failiure", "")
            }
        }
    }

    private fun createGroup(participantsList : ArrayList<String>, createdAt : Long) {
        CommonUtilities.showProgressWheel(activity)
        FirebaseDatabase.getInstance().reference.child("groups").child(userID + "__" + createdAt)
                .child("group_details").setValue(GroupDetails(userID + "__" + createdAt, etGrpName.text.toString(), imagePath, userID, createdAt))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseDatabase.getInstance().reference.child("groups").child(userID + "__" + createdAt)
                                .child("participants").setValue(participantsList).addOnCompleteListener { task ->
                                    CommonUtilities.hideProgressWheel()
                                    if (task.isSuccessful) {
                                        CommonUtilities.showAlert(activity, "Group created successfully.", true)
                                        for (id in participantsList)
                                            FirebaseDatabase.getInstance().reference.child("users").child(id).child("my_groups")
                                                    .push().setValue(userID + "__" + createdAt)
                                    } else
                                        CommonUtilities.showAlert(activity, task.exception!!.message, false)
                                }
                    } else {
                        CommonUtilities.hideProgressWheel()
                        CommonUtilities.showAlert(activity, task.exception!!.message, false)
                    }
                }?.addOnFailureListener { e ->
                    CommonUtilities.hideProgressWheel()
                    CommonUtilities.showAlert(activity, e.message, false)
                }
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {

        if (act == -1)
            addRemoveView(pos, true)
        else if (act == -2)
            addRemoveView(pos, false)

    }

    private fun addRemoveView(pos: Int, isAdd: Boolean) {

        if (isAdd) {
            val view = layoutInflater.inflate(R.layout.layout_participants, null)
            if (!friendList.get(pos).image.isEmpty())
                Picasso.get().load(friendList.get(pos).image).placeholder(R.mipmap.ic_launcher).into(view.ivParticipant)

            view.tvName.setText(friendList[pos].name)
            view.setTag(friendList[pos].id)
            view.btnCross.setOnClickListener {
                for (i in friendList.indices) {
                    if (friendList[i].id.equals(view.tag))
                        friendList[i].isAdded = false
                }
                llParticipants.removeView(view)
                adapter.notifyDataSetChanged()
            }

            llParticipants.addView(view)
        } else {


            for (i in 0 until llParticipants.childCount) {
                if (friendList[pos].id.equals(llParticipants.getChildAt(i).tag)) {
                    llParticipants.removeViewAt(i)
                    break
                }
            }
        }
    }

}
