package com.manurung.covidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty

private const val REQUEST_LOCATION = 10
private const val REQUEST_CALL = 1

class LoginActivity : AppCompatActivity() {

    private lateinit var buttonRegister: RelativeLayout
    private var floatingActionButtonLogin: FloatingActionButton? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var mAuth: FirebaseAuth? = null
    var bgapp: ImageView? = null
    var clover: ImageView? = null
    var textsplash: LinearLayout? = null
    var texthome:LinearLayout? = null
    var menus:RelativeLayout? = null
    var frombottom: Animation? = null
    private var handler: Handler? = null

    //    inisialisasi untuk location tracker permission yang akan dicheck
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CALL_PHONE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        buttonRegister = findViewById(R.id.buttonRegister)
        floatingActionButtonLogin = findViewById(R.id.fabLogin)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom)

        handler = Handler()

        bgapp = findViewById<ImageView>(R.id.bgapp)
        clover = findViewById<ImageView>(R.id.clover)
        textsplash = findViewById<LinearLayout>(R.id.textsplash)
        texthome = findViewById<LinearLayout>(R.id.texthome)
        menus = findViewById<RelativeLayout>(R.id.relativeLayoutLogin)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {

            } else {
                requestPermissions(permissions, REQUEST_LOCATION)
                requestPermissions(permissions, REQUEST_CALL)
            }
        }

        bgapp!!.animate().translationY(-2100f).setDuration(1000).startDelay = 1500
        clover!!.animate().alpha(0f).setDuration(1000).startDelay = 1500
//        textsplash!!.animate().translationY(140f).alpha(0f).setDuration(1000).startDelay = 6000

        handler!!.postDelayed({
            textsplash!!.visibility = View.INVISIBLE
        }, 1500)

        texthome!!.startAnimation(frombottom)
        menus!!.startAnimation(frombottom)

        buttonRegister!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        })

        floatingActionButtonLogin!!.setOnClickListener(View.OnClickListener {
            val email = editTextEmail!!.getText().toString()
            val password: String = editTextPassword!!.text.toString()

            if (email == "") {
                Toasty.warning(this@LoginActivity, "Silahkan isi email Anda", Toast.LENGTH_SHORT).show()
            } else if (password == "") {
                Toasty.warning(this@LoginActivity, "Silahkan isi password Anda", Toast.LENGTH_SHORT).show()
            } else {
                mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    this@LoginActivity) { task ->
                    if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                        val user = mAuth!!.currentUser
                        Toasty.success(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else { // If sign in fails, display a message to the user.
                        Toasty.error(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser

        if (currentUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (this.checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }
}
