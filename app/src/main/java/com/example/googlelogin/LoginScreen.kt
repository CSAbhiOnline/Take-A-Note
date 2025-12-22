package com.example.googlelogin

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    googleAuthRepository: GoogleAuthRepository,
    onLoginSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Scaffold{
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to Google Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                scope.launch {
                    val result = googleAuthRepository.signInWithGoogle()
                    if (result.isSuccess) {
                        onLoginSuccess()
                    } else {
                        // Handle error (e.g., show toast)
                        println("Login failed: ${result.exceptionOrNull()?.message}")
                    }
                }
            }) {
                Text(text = "Sign in with Google")
            }
        }
    }

}
