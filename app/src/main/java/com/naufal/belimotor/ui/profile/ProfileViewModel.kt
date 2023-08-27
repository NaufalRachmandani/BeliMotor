package com.naufal.belimotor.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.naufal.belimotor.data.auth.UserRepository
import com.naufal.belimotor.data.auth.model.request.RegisterRequest
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val imagesRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUser()
                .onStart {
                    _profileState.emit(ProfileState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            response?.apply {
                                val imagePath = imagesRef.child(this.userId)
                                imagePath.downloadUrl.addOnSuccessListener {
                                    response.image = it.toString()

                                    viewModelScope.launch {
                                        _profileState.emit(ProfileState(userResponse = response))
                                    }
                                }.addOnFailureListener {
                                    it.message?.let { error ->
                                        Log.i(
                                            "ProfileViewModel",
                                            "error image $error"
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = { data, code, message ->
                            Log.i("ProfileViewModel", message.toString())
                            _profileState.emit(ProfileState(error = true, message = message))
                        },
                        onError = {
                            Log.i("ProfileViewModel", it?.message.toString())
                            _profileState.emit(ProfileState(error = true, message = it?.message))
                        }
                    )
                }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUser()
                .onStart {}
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _profileState.emit(ProfileState(userResponse = response))
                        },
                        onFailure = { data, code, message ->
                            Log.i("ProfileViewModel", message.toString())
                            _profileState.emit(ProfileState(error = true, message = message))
                        },
                        onError = {
                            Log.i("ProfileViewModel", it?.message.toString())
                            _profileState.emit(ProfileState(error = true, message = it?.message))
                        }
                    )
                }
        }
    }

    data class ProfileState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val message: String? = null,
        val userResponse: UserResponse? = null,
    )
}