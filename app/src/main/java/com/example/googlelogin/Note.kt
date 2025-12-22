package com.example.googlelogin

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Note(
    @DocumentId val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val userId: String = ""
)
