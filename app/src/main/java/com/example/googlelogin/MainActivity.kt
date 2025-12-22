package com.example.googlelogin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.googlelogin.ui.theme.GoogleLoginTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleAuthRepository = GoogleAuthRepository(this)
        val noteRepository = NoteRepository()

        val sharedText = if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }

        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(true) }
            GoogleLoginTheme(darkTheme = darkTheme) {
                MainApp(
                    googleAuthRepository = googleAuthRepository,
                    noteRepository = noteRepository,
                    darkTheme = darkTheme,
                    onThemeChange = { darkTheme = it },
                    sharedText = sharedText
                )
            }
        }
    }
}

@Composable
fun MainApp(
    googleAuthRepository: GoogleAuthRepository,
    noteRepository: NoteRepository,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    sharedText: String? = null
) {
    val navController = rememberNavController()
    val startDestination = if (googleAuthRepository.getCurrentUser() != null) {
        if (sharedText != null) "note_entry?sharedText=${Uri.encode(sharedText)}" else "home"
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                googleAuthRepository = googleAuthRepository,
                onLoginSuccess = {
                    if (sharedText != null) {
                        navController.navigate("note_entry?sharedText=${Uri.encode(sharedText)}") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                googleAuthRepository = googleAuthRepository,
                noteRepository = noteRepository,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange,
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onAddNote = {
                    navController.navigate("note_entry")
                },
                onNoteClick = { noteId ->
                    navController.navigate("note_entry?noteId=$noteId")
                }
            )
        }
        composable(
            "note_entry?noteId={noteId}&sharedText={sharedText}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("sharedText") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            val text = backStackEntry.arguments?.getString("sharedText")
            NoteEntryScreen(
                noteRepository = noteRepository,
                googleAuthRepository = googleAuthRepository,
                noteId = noteId,
                initialContent = text ?: "",
                onNavigateBack = {
                    if (text != null) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
