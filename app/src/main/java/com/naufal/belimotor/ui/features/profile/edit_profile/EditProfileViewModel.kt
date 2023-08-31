package com.naufal.belimotor.ui.features.profile.edit_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.naufal.belimotor.data.auth.UserRepository
import com.naufal.belimotor.data.auth.model.request.EditUserRequest
import com.naufal.belimotor.data.auth.model.response.UserResponse
import com.naufal.belimotor.data.common.addOnResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val imagesRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    private val _editProfileState = MutableStateFlow(EditProfileState())
    val editProfileState = _editProfileState.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUser()
                .onStart {
                    _editProfileState.emit(EditProfileState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            response?.apply {
                                val imagePath = imagesRef.child(this.userId)
                                imagePath.downloadUrl.addOnSuccessListener {
                                    response.image = it

                                    viewModelScope.launch {
                                        _editProfileState.emit(
                                            EditProfileState(
                                                userResponse = response
                                            )
                                        )
                                    }
                                }.addOnFailureListener {
                                    it.message?.let { error ->
                                        Log.i(
                                            "EditProfileViewModel",
                                            "error image $error"
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = { data, code, message ->
                            Log.i("EditProfileViewModel", message.toString())
                            _editProfileState.emit(
                                EditProfileState(
                                    error = true,
                                    message = message
                                )
                            )
                        },
                        onError = {
                            Log.i("EditProfileViewModel", it?.message.toString())
                            _editProfileState.emit(
                                EditProfileState(
                                    error = true,
                                    message = it?.message
                                )
                            )
                        }
                    )
                }
        }
    }

    fun editUser(editUserRequest: EditUserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.editUser(editUserRequest)
                .onStart {
                    _editProfileState.emit(editProfileState.value.copy(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _editProfileState.emit(EditProfileState(success = response ?: false))
                        },
                        onFailure = { data, code, message ->
                            Log.i("EditProfileViewModel", message.toString())
                            _editProfileState.emit(
                                editProfileState.value.copy(
                                    error = true,
                                    message = message
                                )
                            )
                        },
                        onError = {
                            Log.i("EditProfileViewModel", it?.message.toString())
                            _editProfileState.emit(
                                editProfileState.value.copy(
                                    error = true,
                                    message = it?.message
                                )
                            )
                        }
                    )
                }
        }
    }

    data class EditProfileState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val message: String? = null,
        val success: Boolean? = null,

        //old data
        val userResponse: UserResponse? = null,
    )
}