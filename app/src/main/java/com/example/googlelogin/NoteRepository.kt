package com.example.googlelogin

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    fun getNotesFlow(userId: String): Flow<List<Note>> = callbackFlow {
        val subscription = notesCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notes = snapshot.toObjects(Note::class.java)
                    trySend(notes)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addNote(note: Note) {
        notesCollection.add(note).await()
    }

    suspend fun updateNote(note: Note) {
        if (note.id.isNotEmpty()) {
            notesCollection.document(note.id).set(note).await()
        }
    }

    suspend fun deleteNote(noteId: String) {
        notesCollection.document(noteId).delete().await()
    }
}
