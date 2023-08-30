package com.naufal.belimotor.data.motor

import android.content.Context
import android.util.Log
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
import com.naufal.belimotor.data.common.TransactionStatus
import com.naufal.belimotor.data.motor.model.MotorDetail
import com.naufal.belimotor.data.motor.model.Transaction
import com.naufal.belimotor.ui.util.getRandomString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MotorRepository @Inject constructor(
    private val context: Context,
) {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = Firebase.firestore.collection("users")
    private val dbMotors = Firebase.firestore.collection("motors")
    private val dbTransaction = Firebase.firestore.collection("transaction")

    suspend fun getMotorList(): Flow<AppResult<List<MotorDetail>>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            dbMotors.addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val motorList = mutableListOf<MotorDetail>()
                    snapshot.forEach {
                        try {
                            val field = it.toObject(MotorDetail::class.java)
                            motorList.add(field)
                        } catch (e: Exception) {
                            Log.e("MotorRepository", e.message.toString())
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

    suspend fun getMotor(motorId: String): Flow<AppResult<MotorDetail>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            dbMotors.document(motorId).addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    try {
                        val motor = snapshot.toObject(MotorDetail::class.java)
                        AppResult.OnSuccess(motor)
                    } catch (e: Exception) {
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

    suspend fun buyMotor(motorDetail: MotorDetail): Flow<AppResult<Boolean>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            val transactionId = getRandomString()
            dbTransaction.document(transactionId).set(
                mapOf(
                    "transactionId" to transactionId,
                    "motorId" to motorDetail.motorId,
                    "motorImage" to motorDetail.motorImage,
                    "motorName" to motorDetail.motorName,
                    "motorPrice" to motorDetail.motorPrice,
                    "status" to TransactionStatus.Waiting.status,
                )
            ).addOnSuccessListener {
                trySend(AppResult.OnSuccess(true)).isSuccess
            }.addOnFailureListener { e ->
                trySend(AppResult.OnFailure(message = e.message ?: ""))
            }
        }

        awaitClose { snapshotListener?.isCanceled() }
    }

    suspend fun getTransactionList(): Flow<AppResult<List<Transaction>>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            dbTransaction.addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val transactionList = mutableListOf<Transaction>()
                    snapshot.forEach {
                        try {
                            val field = it.toObject(Transaction::class.java)
                            transactionList.add(field)
                        } catch (e: Exception) {
                            Log.e("MotorRepository", e.message.toString())
                        }
                    }

                    AppResult.OnSuccess(transactionList.toList())
                } else {
                    AppResult.OnFailure(message = e?.message ?: "")
                }

                trySend(response).isSuccess
            }
        }

        awaitClose { snapshotListener?.remove() }
    }

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