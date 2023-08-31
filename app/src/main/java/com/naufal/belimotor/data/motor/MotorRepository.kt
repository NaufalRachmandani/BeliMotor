package com.naufal.belimotor.data.motor

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naufal.belimotor.data.common.AppResult
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
    private val transactionCollectionName = "transaction"
    private val motorsCollectionName = "motors"

    suspend fun getMotorList(): Flow<AppResult<List<MotorDetail>>> = callbackFlow {
        val snapshotListener = dbMotors.addSnapshotListener { snapshot, e ->
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

        awaitClose { snapshotListener.remove() }
    }

    suspend fun getMotor(motorId: String): Flow<AppResult<MotorDetail>> = callbackFlow {
        val snapshotListener = dbMotors.document(motorId).addSnapshotListener { snapshot, e ->
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

        awaitClose { snapshotListener.remove() }
    }

    suspend fun buyMotor(motorDetail: MotorDetail): Flow<AppResult<Boolean>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            val transactionId = getRandomString()
            dbUsers.document(it.uid).collection(transactionCollectionName).document(transactionId)
                .set(
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
            dbUsers.document(it.uid).collection(transactionCollectionName)
                .addSnapshotListener { snapshot, e ->
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

    //update transaction to cancel
    suspend fun cancelTransaction(transactionId: String): Flow<AppResult<Boolean>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let {
            dbUsers.document(it.uid).collection(transactionCollectionName).document(transactionId)
                .update(
                    mapOf(
                        "status" to TransactionStatus.Cancel.status,
                    )
                ).addOnSuccessListener {
                    trySend(AppResult.OnSuccess(true)).isSuccess
                }.addOnFailureListener { e ->
                    trySend(AppResult.OnFailure(message = e.message ?: ""))
                }
        }

        awaitClose { snapshotListener?.isCanceled() }
    }

    //check if motor qty if available
    //reduce qty motor
    //update transaction to success
    //check if user already have that motor or not
    //if doesnt have insert motor else increase qty
    suspend fun proceedTransaction(transactionId: String): Flow<AppResult<Boolean>> = callbackFlow {
        val snapshotListener = auth.currentUser?.let { firebaseUser ->
            db.runTransaction { transaction ->
                val transactionDocRef =
                    dbUsers.document(firebaseUser.uid).collection(transactionCollectionName)
                        .document(transactionId)
                val userMotorsDocRef =
                    dbUsers.document(firebaseUser.uid).collection(motorsCollectionName)

                val snapshotTransaction = transaction.get(transactionDocRef)
                try {
                    val transactionModel = snapshotTransaction.toObject(Transaction::class.java)

                    transactionModel?.let {
                        //check motor qty
                        it.motorId?.let { motorId ->
                            val snapshotMotorDetail = transaction.get(dbMotors.document(motorId))
                            val motorDetailModel =
                                snapshotMotorDetail.toObject(MotorDetail::class.java)

                            val snapshotUserMotor =
                                transaction.get(userMotorsDocRef.document(motorId))
                            val userMotorModel =
                                snapshotUserMotor.toObject(MotorDetail::class.java)

                            motorDetailModel?.motorQty?.let { motorQty ->
                                if (motorQty > 0) {
                                    //update motor qty on motor list
                                    transaction.update(
                                        dbMotors.document(motorId), mapOf(
                                            "motorQty" to motorQty - 1,
                                        )
                                    )

                                    //check if user already have motor or not
                                    //if doesnt have insert motor else increase qty
                                    if (snapshotUserMotor.exists()) {
                                        val userMotorQty = userMotorModel?.motorQty ?: 0

                                        transaction.update(
                                            userMotorsDocRef.document(motorId),
                                            mapOf(
                                                "motorQty" to userMotorQty + 1,
                                            )
                                        )
                                    } else {
                                        transaction.set(
                                            userMotorsDocRef.document(motorId),
                                            mapOf(
                                                "motorId" to motorId,
                                                "motorDesc" to motorDetailModel.motorDesc,
                                                "motorImage" to motorDetailModel.motorImage,
                                                "motorName" to motorDetailModel.motorName,
                                                "motorPrice" to motorDetailModel.motorPrice,
                                                "motorQty" to 1,
                                            ),
                                        )
                                    }

                                    //update status transaction to success
                                    transaction.update(
                                        transactionDocRef, mapOf(
                                            "status" to TransactionStatus.Success.status,
                                        )
                                    )
                                } else {
                                    throw FirebaseFirestoreException(
                                        "Motor sudah tidak tersedia",
                                        FirebaseFirestoreException.Code.ABORTED,
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MotorRepository", e.message.toString())
                    throw FirebaseFirestoreException(
                        e.message.toString(),
                        FirebaseFirestoreException.Code.ABORTED,
                    )
                }
            }.addOnSuccessListener {
                trySend(AppResult.OnSuccess(true)).isSuccess
            }.addOnFailureListener { e ->
                trySend(AppResult.OnFailure(message = e.message ?: ""))
            }
        }

        awaitClose { snapshotListener?.isCanceled() }
    }

}