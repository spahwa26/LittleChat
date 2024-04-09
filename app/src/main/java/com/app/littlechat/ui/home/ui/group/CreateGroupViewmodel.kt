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
import com.app.littlechat.utility.Constants.Companion.NULL
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

    var grpId: String? = savedStateHandle[HomeArgs.GROUP_ID_ARG]

    private val _createGroupUiState: MutableState<CreateGroupUiState?> = mutableStateOf(null)

    val createGroupUiState: State<CreateGroupUiState?> = _createGroupUiState

    val groupName = mutableStateOf("")

    var usersList = mutableStateListOf<User>()

    val selectedMembers = mutableStateListOf<User>()

    val imageUri = mutableStateOf<Bitmap?>(null)

    val progressState = mutableStateOf(false)


    init {
        getFriends()
    }

    fun getImageName() = repository.getGroupImageName(getGroupId())

    private fun getGroupId(): String =
        if (grpId == null || grpId.equals(NULL)) "${userPreferences.id}__${currentTime}" else "${grpId}__${currentTime}"

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
                        usersList.addAll(it.data)
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

        val participantsList = selectedMembers.map {
            it.id
        }.toMutableList()
        viewModelScope.launch {
            progressState.value = true
            repository.createGroup(
                participantsList,
                groupName = groupName.value,
                groupID = getGroupId(),
                isUploadImage = imageUri.value != null,
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