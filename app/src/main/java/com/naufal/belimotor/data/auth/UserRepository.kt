package com.naufal.belimotor.data.auth

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.naufal.belimotor.data.auth.model.request.RegisterRequest
import com.naufal.belimotor.data.auth.model.response.UserResponse
import com.naufal.belimotor.data.common.AppResult
import com.naufal.belimotor.data.common.GetFile
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

    fun logout(): Flow<AppResult<Boolean>> = callbackFlow {
        auth.signOut()
        trySend(AppResult.OnSuccess(true)).isSuccess
    }

    suspend fun getUser(): Flow<AppResult<UserResponse>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            dbUsers.document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val dataUser = snapshot.toObject(UserResponse::class.java)

                        AppResult.OnSuccess(dataUser)
                    } else {
                        AppResult.OnFailure(message = e?.message ?: "")
                    }

                    trySend(response).isSuccess
                }
        }

        awaitClose { snapshotListener?.remove() }
    }

//    override suspend fun editUser(editUserEntity: EditUserEntity): Flow<Resource<Boolean>> =
//        callbackFlow {
//            val snapshotListener = db.runBatch {
//                val userEntity = editUserEntity.userEntity
//                dbUsers.document(userEntity.userId).update(
//                    mapOf(
//                        "fullName" to userEntity.fullName,
//                        "major" to userEntity.major,
//                        "nim" to userEntity.nim,
//                        "noHp" to userEntity.noHp,
//                        "training" to userEntity.training,
//                        "role" to userEntity.role
//                    )
//                )
//
//                //upload photo
//                if (editUserEntity.pathImage != "") {
//                    val file = Uri.fromFile(File(editUserEntity.pathImage))
//                    val storageRef = imagesRef.child(userEntity.userId)
//                    storageRef.putFile(file)
//                }
//            }.addOnSuccessListener {
//                Log.i("UserRepositoryImpl", "editUser success")
//                trySend(Resource.Success(true)).isSuccess
//            }.addOnFailureListener { error ->
//                Log.i("UserRepositoryImpl", "editUser failed")
//                trySend(Resource.Error(error.checkFirebaseError()))
//            }
//            awaitClose { snapshotListener.isCanceled() }
//        }


    //check the training participantNow < participant max
    //update participantNow in training
    //store user data inside training user document
    //update training and trainingId in user document
//    override suspend fun registerTraining(
//        trainingId: String,
//        userEntity: UserEntity
//    ): Flow<Resource<Boolean>> =
//        callbackFlow {
//            Log.i("UserRepositoryImpl", "registerTraining $trainingId")
//            val snapshotListener = db.runTransaction { transaction ->
//                val trainingRef = dbTraining.document(trainingId)
//                val participant = dbTraining.document(trainingId).collection("participant")
//                val snapshot = transaction.get(trainingRef)
//
//                val trainingName: String = snapshot.getString("trainingName").toString()
//                val participantNow: Int = snapshot.getDouble("participantNow")?.toInt() ?: 0
//                val participantMax: Int = snapshot.getDouble("participantMax")?.toInt() ?: 0
//
//                Log.i(
//                    "UserRepositoryImpl",
//                    "registerTraining check participantNow $participantNow < participantMax $participantMax"
//                )
//                //if participantNow < participantMax register user to training
//                if (participantNow < participantMax) {
//                    //update participantNow in training
//                    transaction.update(trainingRef, "participantNow", participantNow + 1)
//
//                    //store user data inside training user document
//                    transaction.set(
//                        participant.document(userEntity.userId), mapOf(
//                            "fullName" to userEntity.fullName,
//                            "userId" to userEntity.userId,
//                            "nim" to userEntity.nim
//                        )
//                    )
//
//                    //update training and trainingId in user document
//                    transaction.update(
//                        dbUsers.document(userEntity.userId), mapOf(
//                            "training" to trainingName,
//                            "trainingId" to trainingId
//                        )
//                    )
//                    true
//                } else {
//                    throw FirebaseFirestoreException(
//                        "Training participant is full",
//                        FirebaseFirestoreException.Code.ABORTED
//                    )
//                }
//            }.addOnSuccessListener {
//                Log.i("UserRepositoryImpl", "registerTraining success")
//                trySend(Resource.Success(true)).isSuccess
//            }.addOnFailureListener { error ->
//                Log.i("UserRepositoryImpl", "registerTraining failed")
//                trySend(Resource.Error(error.checkFirebaseError()))
//            }
//            awaitClose { snapshotListener.isCanceled() }
//        }

//    override suspend fun getParticipants(trainingId: String): Flow<Resource<List<UserEntity>>> =
//        callbackFlow {
//            val snapshotListener = dbTraining.document(trainingId).collection("participant")
//                .addSnapshotListener { snapshot, e ->
//                    val response = if (snapshot != null) {
//                        val dataUsers = mutableListOf<UserDto>()
//                        snapshot.forEach {
//                            val field = it.toObject(UserDto::class.java)
//                            dataUsers.add(field)
//                            Log.i(
//                                "TrainingRepositoryImpl",
//                                "getParticipants with id ${field.userId} success"
//                            )
//                        }
//                        Resource.Success(dataUsers.map { it.toUserEntity() })
//                    } else {
//                        Resource.Error(e.checkFirebaseError())
//                    }
//                    trySend(response).isSuccess
//                }
//            awaitClose { snapshotListener.remove() }
//        }

}