package com.naufal.belimotor.data.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.naufal.belimotor.data.auth.model.request.EditUserRequest
import com.naufal.belimotor.data.auth.model.request.RegisterRequest
import com.naufal.belimotor.data.auth.model.response.UserResponse
import com.naufal.belimotor.data.common.AppResult
import com.naufal.belimotor.data.common.GetFile
import com.naufal.belimotor.data.motor.model.MotorDetail
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val context: Context,
) {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = Firebase.firestore.collection("users")
    private val imagesRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")
    private val motorsCollectionName = "motors"

    suspend fun register(registerRequest: RegisterRequest): Flow<AppResult<Boolean>> =
        callbackFlow {
            val snapshotListener = db.runBatch {

                //create user
                auth.createUserWithEmailAndPassword(registerRequest.email, registerRequest.password)
                    .addOnSuccessListener {
                        val userId = it.user?.uid ?: ""

                        //upload photo
                        val file = GetFile.getFile(context, registerRequest.image.toUri())
                        val storageRef = imagesRef.child(userId)
                        storageRef.putFile(file.toUri())

                        //put in firestore
                        dbUsers.document(userId).set(
                            mapOf(
                                "email" to registerRequest.email,
                                "name" to registerRequest.name,
                                "userId" to userId,
                                "favMotor" to registerRequest.favMotor,
                            )
                        )
                    }
            }.addOnSuccessListener {
                Log.i("UserRepository", "register success")
                trySend(AppResult.OnSuccess(true)).isSuccess
            }.addOnFailureListener { error ->
                Log.i("UserRepository", "register failed")
                trySend(AppResult.OnFailure(message = error.message))
            }

            awaitClose { snapshotListener.isCanceled() }
        }

    suspend fun login(email: String, password: String): Flow<AppResult<Boolean>> =
        callbackFlow {
            val snapshotListener = auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.i("UserRepository", "login success")
                        trySend(AppResult.OnSuccess(true))
                    } else {
                        Log.i("UserRepository", "login failed")
                        trySend(AppResult.OnFailure(message = it.exception?.message ?: ""))
                    }
                }

            awaitClose { snapshotListener.isCanceled() }
        }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUser(): Flow<AppResult<UserResponse>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            dbUsers.document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        try {
                            val dataUser = snapshot.toObject(UserResponse::class.java)
                            AppResult.OnSuccess(dataUser)
                        } catch (e: Exception) {
                            Log.e("UserRepository", e.message.toString())
                            AppResult.OnFailure(message = e.message ?: "")
                        }
                    } else {
                        AppResult.OnFailure(message = e?.message ?: "")
                    }

                    trySend(response).isSuccess
                }
        }

        awaitClose { snapshotListener?.remove() }
    }

    suspend fun editUser(editUserRequest: EditUserRequest): Flow<AppResult<Boolean>> =
        callbackFlow {
            val snapshotListener = db.runBatch {
                auth.currentUser?.let {
                    dbUsers.document(it.uid).update(
                        mapOf(
                            "name" to editUserRequest.name,
                            "favMotor" to editUserRequest.favMotor
                        )
                    )

                    //upload photo
                    if (editUserRequest.image != Uri.EMPTY) {
                        val file = GetFile.getFile(context, editUserRequest.image)
                        val storageRef = imagesRef.child(it.uid)
                        storageRef.putFile(file.toUri())
                    }
                }
            }.addOnSuccessListener {
                Log.i("UserRepository", "editUser success")
                trySend(AppResult.OnSuccess(true))
            }.addOnFailureListener { error ->
                Log.i("UserRepository", "editUser failed")
                trySend(AppResult.OnFailure(message = error.message))
            }

            awaitClose { snapshotListener.isCanceled() }
        }

    suspend fun getUserMotorList(): Flow<AppResult<List<MotorDetail>>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            dbUsers.document(it.uid).collection(motorsCollectionName)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val motorList = mutableListOf<MotorDetail>()
                        snapshot.forEach {
                            try {
                                val field = it.toObject(MotorDetail::class.java)
                                motorList.add(field)
                            } catch (e: Exception) {
                                Log.e("UserRepository", e.message.toString())
                            }
                        }

                        AppResult.OnSuccess(motorList.toList())
                    } else {
                        AppResult.OnFailure(message = e?.message ?: "")
                    }

                    trySend(response).isSuccess
                }
        }

        awaitClose { snapshotListener?.remove() }
    }
}