package com.cristopher.inventariopersonalapp.data.repository

import com.cristopher.inventariopersonalapp.data.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ItemRepository(private val firestore: FirebaseFirestore) {

    private val itemCollection = firestore.collection("item")

    suspend fun addItem(item: Item): Result<Boolean> {
        return try {
            itemCollection.add(item).await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun getItemForOwner(ownerId: String): Flow<Result<List<Item>>> = callbackFlow {
        val subscription = itemCollection
            .whereEqualTo("ownerId", ownerId)
            .orderBy("nombre", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.failure(e))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Item::class.java)?.apply {
                            this.id = doc.id
                        }
                    }
                    trySend(Result.success(items))
                } else {
                    trySend(Result.success(emptyList()))
                }
            }

        awaitClose { subscription.remove() }
    }

    suspend fun updateItem(item: Item): Result<Boolean> {
        return try {
            itemCollection.document(item.id).set(item).await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteItem(itemID: String): Result<Boolean> {
        return try {
            itemCollection.document(itemID).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}