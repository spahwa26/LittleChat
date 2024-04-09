package com.app.littlechat.ui.home.ui.group

import android.graphics.Bitmap
import android.support.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.data.network.HomeRepository
import com.app.littlechat.ui.home.navigation.HomeArgs
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.haveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewmodel @Inject constructor(
    private val repository: HomeRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentTime = System.currentTimeMillis()

    val grpId: String? = savedStateHandle[HomeArgs.GROUP_ID_ARG]

    var name: String? = savedStateHandle[HomeArgs.NAME_ARG]

    var image: String? = savedStateHandle[HomeArgs.IMAGE_ARG]

    private val _createGroupUiState: MutableState<CreateGroupUiState?> = mutableStateOf(null)

    val createGroupUiState: State<CreateGroupUiState?> = _createGroupUiState

    val groupName = mutableStateOf("")

    var usersList = mutableStateListOf<User>()

    private var oldMembers = listOf<User>()

    val selectedMembers = mutableStateListOf<User>()

    val imageUri = mutableStateOf<Bitmap?>(null)

    val progressState = mutableStateOf(false)


    fun getGroupData(isInfoOnly: Boolean=false) {
        groupName.value = if (name.isNullOrBlank()) "" else name ?: ""
        if (grpId.haveData()) {
            repository.getParticipantsData(grpId!!) { result ->
                when (result) {
                    is CustomResult.Success -> {
                        oldMembers = if (isInfoOnly) result.data else result.data.filter {
                            (it.id != userPreferences.id)
                        }
                        selectedMembers.clear()
                        selectedMembers.addAll(oldMembers)
                        getFriends()
                    }

                    is CustomResult.Error -> {
                        _createGroupUiState.value =
                            CreateGroupUiState.Error(result.exception.message ?: "")
                    }
                }
            }
        } else
            getFriends()
    }

    fun getImageName() = repository.getGroupImageName(getGroupId())
    fun isMyGroup(id: String) = (grpId?.contains(id) == true)

    private fun getGroupId(): String =
        if (grpId.haveData()) grpId!! else "${userPreferences.id}__${currentTime}"

    fun updateItem(user: User, index: Int) {
        usersList.removeAt(index)
        usersList.add(index, user)
        if (user.isAdded) selectedMembers.add(user)
        else {
            selectedMembers.removeIf {
                it.id == user.id
            }
        }

    }

    private fun getFriends() {
        _createGroupUiState.value = CreateGroupUiState.Loading
        viewModelScope.launch {
            repository.getFriends {
                when (it) {
                    is CustomResult.Success -> {
                        val newList = it.data.map { user ->
                            val isAvailable =
                                selectedMembers.find { selectedUser -> selectedUser.id == user.id }
                            user.isAdded = isAvailable != null
                            return@map user
                        }
                        usersList.addAll(newList)
                    }

                    is CustomResult.Error -> {
                        _createGroupUiState.value =
                            CreateGroupUiState.Error(it.exception.message ?: "")
                    }
                }
            }
        }
    }


    fun createGroup() {

        if (groupName.value.isBlank()) {
            _createGroupUiState.value = CreateGroupUiState.LocalMessage(R.string.enter_valid_name)
            return
        }

        if (selectedMembers.size < 2) {
            _createGroupUiState.value =
                CreateGroupUiState.LocalMessage(R.string.must_be_two_members)
            return
        }

        val participantsList = selectedMembers.associateBy({ it.id }, { it.id })
        viewModelScope.launch {
            progressState.value = true
            repository.createEditGroup(
                participantsList.toMutableMap(),
                oldUsersList = oldMembers,
                groupName = groupName.value,
                groupID = getGroupId(),
                isUploadImage = imageUri.value != null,
                isEdit = (!grpId.isNullOrBlank()),
            ) {
                when (it) {
                    is CustomResult.Success -> {
                        _createGroupUiState.value = CreateGroupUiState.Success(false)
                    }

                    is CustomResult.Error -> {
                        _createGroupUiState.value =
                            CreateGroupUiState.Error(it.exception.message ?: "")
                    }
                }
            }
        }

    }


    //todo: user proper exception in Error class of every UI state instead of string
    sealed class CreateGroupUiState {
        data object Loading : CreateGroupUiState()
        data class Success(val isEdit: Boolean) : CreateGroupUiState()
        data class Error(val e: String) : CreateGroupUiState()
        data class LocalMessage(@StringRes val msg: Int) : CreateGroupUiState()
    }

    fun setIdle() {
        _createGroupUiState.value = null
        progressState.value = false
    }

}