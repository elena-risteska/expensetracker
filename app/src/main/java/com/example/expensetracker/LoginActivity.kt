package com.example.expensetracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    private fun changeLanguage(language: String) {
        LocaleHelper.setLocale(this, language)
        recreate()
    }

    private fun isTablet(): Boolean {
        val screenLayout = resources.configuration.screenLayout
        val screenSizeMask = screenLayout and android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK
        return screenSizeMask >= android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit { putBoolean("isLoggedIn", true) }

                    Toast.makeText(this, getString(R.string.successful), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // From google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (auth.currentUser != null && isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        super.onCreate(savedInstanceState)

        val backgroundColor = "#FFF176".toColorInt()
        val buttonColor = "#7E57C2".toColorInt()
        val textColor = "#212121".toColorInt()
        val accentColor = "#512DA8".toColorInt()

        // Root FrameLayout
        val rootLayout = FrameLayout(this).apply {
            setBackgroundColor(backgroundColor)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Flag Row - Positioned in top-right
        val flagRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.END
            ).apply {
                topMargin = 32
                marginEnd = 32
            }
        }

        val macedonianFlag = ImageView(this).apply {
            setImageResource(R.drawable.ic_flag_mk)
            layoutParams = LinearLayout.LayoutParams(96, 96).apply {
                marginEnd = 16
            }
            setOnClickListener { changeLanguage("mk") }
        }

        val englishFlag = ImageView(this).apply {
            setImageResource(R.drawable.ic_flag_en)
            layoutParams = LinearLayout.LayoutParams(96, 96)
            setOnClickListener { changeLanguage("en") }
        }

        flagRow.addView(macedonianFlag)
        flagRow.addView(englishFlag)

        // Main Login Layout - Centered
        val loginLayout = LinearLayout(this).apply {
            orientation = if (isTablet() && isLandscape()) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            val padding = if (isTablet()) 128 else 64
            setPadding(padding, padding, padding, padding)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }


        val title = TextView(this).apply {
            text = getString(R.string.login)
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(textColor)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 64)
        }

        fun createRoundedInput(hint: String, isPassword: Boolean = false): TextInputLayout {
            return TextInputLayout(this).apply {
                boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
                boxBackgroundColor = Color.WHITE
                setBoxCornerRadii(24f, 24f, 24f, 24f)
                boxStrokeColor = accentColor
                this.hint = hint
                setPadding(0, 0, 0, 24)

                addView(TextInputEditText(this@LoginActivity).apply {
                    setSingleLine()
                    setPadding(40, 30, 40, 30)
                    setTextColor(textColor)
                    if (isPassword) {
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                })
            }
        }

        val emailInput = createRoundedInput(getString(R.string.email))
        val passwordInput = createRoundedInput(getString(R.string.password), isPassword = true)

        val loginButton = Button(this).apply {
            text = getString(R.string.login)
            setTextColor(Color.WHITE)
            textSize = 18f
            setPadding(40, 30, 40, 30)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 48f
                setColor(buttonColor)
            }
        }

        val registerLink = TextView(this).apply {
            text = getString(R.string.no_account)
            setTextColor(accentColor)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 0)
        }
        val guestButton = Button(this).apply {
            text = getString(R.string.guest)
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(40, 30, 40, 30)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 48f
                setColor("#7E57C2".toColorInt())
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 50
            }
        }

        val googleButton = Button(this).apply {
            text = getString(R.string.google)
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(40, 30, 40, 30)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 48f
                setColor("#7E57C2".toColorInt())
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }

            setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
        val fbButton = Button(this).apply {
            text = getString(R.string.facebook)
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(40, 30, 40, 30)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 48f
                setColor("#7E57C2".toColorInt())
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }

            setOnClickListener {
                LoginManager.getInstance().logInWithReadPermissions(
                    this@LoginActivity,
                    listOf("email", "public_profile")
                )
            }
        }


        if (isTablet() && isLandscape()) {
            val leftPanel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(title)
                addView(emailInput)
                addView(passwordInput)
            }

            val rightPanel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(loginButton)
                addView(registerLink)

            }
            rightPanel.addView(guestButton)
            loginLayout.addView(leftPanel)
            loginLayout.addView(rightPanel)
        } else {
            loginLayout.addView(title)
            loginLayout.addView(emailInput)
            loginLayout.addView(passwordInput)
            loginLayout.addView(loginButton)
            loginLayout.addView(registerLink)
            loginLayout.addView(guestButton)
            loginLayout.addView(googleButton)
            loginLayout.addView(fbButton)

        }


        // Add both to root
        rootLayout.addView(loginLayout)
        rootLayout.addView(flagRow)

        setContentView(rootLayout)

        // Logic
        loginButton.setOnClickListener {
            FirebaseMessaging.getInstance().getToken()
            val email = emailInput.editText?.text.toString().trim()
            val password = passwordInput.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill), Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            prefs.edit { putBoolean("isLoggedIn", true) }

                            Toast.makeText(this, getString(R.string.successful), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.localizedMessage ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        guestButton.setOnClickListener {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        prefs.edit { putBoolean("isLoggedIn", true) }

                        Toast.makeText(this, getString(R.string.anonymous), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.localizedMessage ?: "Anonymous login failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}
