package com.example.googlelogin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEntryScreen(
    noteRepository: NoteRepository,
    googleAuthRepository: GoogleAuthRepository,
    noteId: String? = null,
    initialContent: String = "",
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf(initialContent) }
    var isLoading by remember { mutableStateOf(noteId != null) }

    val user = googleAuthRepository.getCurrentUser()

    LaunchedEffect(noteId) {
        if (noteId != null) {
            val note = noteRepository.getNotesFlow(user?.uid ?: "").firstOrNull()?.find { it.id == noteId }
            note?.let {
                title = it.title
                content = it.content
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "New Note" else "Edit Note") },
                actions = {
                    Button(onClick = {
                        scope.launch {
                            val newNote = Note(
                                id = noteId ?: "",
                                title = title,
                                content = content,
                                userId = user?.uid ?: ""
                            )
                            if (noteId == null) {
                                noteRepository.addNote(newNote)
                            } else {
                                noteRepository.updateNote(newNote)
                            }
                            onNavigateBack()
                        }
                    }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        //.weight(1f)
                )
            }
        }
    }
}
