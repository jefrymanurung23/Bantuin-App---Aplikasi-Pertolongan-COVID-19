package com.manurung.covidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty

class RegisterActivity : AppCompatActivity() {

    lateinit var buttonLogin: RelativeLayout
    var floatingActionButtonRegister: FloatingActionButton? = null
    var editTextEmail: EditText? = null
    var editTextPassword:EditText? = null
    private var mAuth: FirebaseAuth? = null
    var bgapp: ImageView? = null
    var texthome: LinearLayout? = null
    var menus:RelativeLayout? = null
    var frombottom: Animation? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        buttonLogin = findViewById(R.id.buttonLogin)
        floatingActionButtonRegister = findViewById(R.id.fabRegister)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom)

        bgapp = findViewById<ImageView>(R.id.bgapp)
        texthome = findViewById<LinearLayout>(R.id.texthome)

        bgapp!!.animate().translationY(-2100f).setDuration(500).startDelay = 300

        texthome!!.animate().translationY(0f).setDuration(500).startDelay = 300

        buttonLogin!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })

        floatingActionButtonRegister!!.setOnClickListener(View.OnClickListener {
            val email = editTextEmail!!.text.toString()
            val password: String = editTextPassword!!.text.toString()
            if (email == "") {
                Toasty.warning(this@RegisterActivity, "Silahkan isi email Anda", Toast.LENGTH_SHORT).show()
            } else if (password == "") {
                Toasty.warning(this@RegisterActivity, "Silahkan isi password Anda", Toast.LENGTH_SHORT).show()
            } else {
                mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                this@RegisterActivity) { task ->
                    if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                        val user = mAuth!!.getCurrentUser()
                        Toasty.success(this@RegisterActivity, "Register Berhasil", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else { // If sign in fails, display a message to the user.
                        Toasty.error(this@RegisterActivity, "Register Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
    }

    override fun onBackPressed() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
