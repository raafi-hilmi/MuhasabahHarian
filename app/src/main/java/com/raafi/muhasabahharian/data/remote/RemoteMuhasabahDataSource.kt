package com.raafi.muhasabahharian.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteMuhasabahDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    /** users/{uid}/reflections */
    private fun userCollection() =
        firestore.collection("users")
            .document(auth.currentUser?.uid ?: "guest")
            .collection("reflections")

    suspend fun upsert(entity: MuhasabahEntity) {
        userCollection().document(entity.id.toString())
            .set(entity.toMap())
            .await()
    }

    suspend fun delete(id: Int) {
        userCollection().document(id.toString()).delete().await()
    }

    suspend fun deleteAll() {
        val batch = firestore.batch()
        userCollection().get().await().documents.forEach { doc ->
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }

    private fun MuhasabahEntity.toMap() = mapOf(
        "date"      to date,
        "type"      to type,
        "activity"  to activity,
        "obstacle"  to obstacle,
        "note"      to note,
        "mood"      to mood,
        "score"     to score,
        "createdAt" to com.google.firebase.Timestamp.now()
    )
}
