package com.manurung.covidapp

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AccountSession : Application() {

    private var firebaseUser: FirebaseUser? = null
    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate() {
        super.onCreate()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser

        if (firebaseUser != null) {
            val intent = Intent(this@AccountSession, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}