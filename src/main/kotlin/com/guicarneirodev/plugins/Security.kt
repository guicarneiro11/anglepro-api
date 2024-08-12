package com.guicarneirodev.plugins

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.io.FileInputStream

fun Application.configureSecurity() {
    val serviceAccount = FileInputStream("/app/firebase-credentials.json")

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options)
    }

    install(Authentication) {
        bearer("firebaseAuth") {
            authenticate { tokenCredential ->
                try {
                    val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(tokenCredential.token)
                    MyAuthenticatedUser(firebaseToken.uid)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}

data class MyAuthenticatedUser(val id: String) : Principal
