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
import androidx.core.graphics.toColorInt
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest


class RegisterActivity : AppCompatActivity() {
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
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)

        val backgroundColor = "#FFF176".toColorInt()
        val buttonColor = "#7E57C2".toColorInt()
        val textColor = "#212121".toColorInt()
        val accentColor = "#512DA8".toColorInt()

        val rootLayout = FrameLayout(this).apply {
            setBackgroundColor(backgroundColor)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Flags in top right corner
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

        // Main registration layout - orientation depends on tablet and orientation
        val mainLayout = LinearLayout(this).apply {
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
            text = getString(R.string.register)
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

                addView(TextInputEditText(this@RegisterActivity).apply {
                    setSingleLine()
                    setPadding(40, 30, 40, 30)
                    setTextColor(textColor)
                    if (isPassword) {
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                })
            }
        }

        val nameInput = createRoundedInput(getString(R.string.username))
        val emailInput = createRoundedInput(getString(R.string.email))
        val passwordInput = createRoundedInput(getString(R.string.password), isPassword = true)

        val registerButton = Button(this).apply {
            text = getString(R.string.register)
            setTextColor(Color.WHITE)
            textSize = 18f
            setPadding(40, 30, 40, 30)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 48f
                setColor(buttonColor)
            }
        }

        val loginLink = TextView(this).apply {
            text = getString(R.string.account)
            setTextColor(accentColor)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 0)
        }

        if (isTablet() && isLandscape()) {
            val leftPanel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(title)
                addView(nameInput)
                addView(emailInput)
                addView(passwordInput)
            }

            val rightPanel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(registerButton)
                addView(loginLink)
            }

            mainLayout.addView(leftPanel)
            mainLayout.addView(rightPanel)
        } else {
            mainLayout.addView(title)
            mainLayout.addView(nameInput)
            mainLayout.addView(emailInput)
            mainLayout.addView(passwordInput)
            mainLayout.addView(registerButton)
            mainLayout.addView(loginLink)
        }

        rootLayout.addView(mainLayout)
        rootLayout.addView(flagRow)

        setContentView(rootLayout)

        // Logic
        registerButton.setOnClickListener {
            val name = nameInput.editText?.text.toString().trim()
            val email = emailInput.editText?.text.toString().trim()
            val password = passwordInput.editText?.text.toString().trim()
            if (password.length < 6) {
                Toast.makeText(this, getString(R.string.password_short), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill), Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Optional: Set display name
                            val user = auth.currentUser
                            val profileUpdates = userProfileChangeRequest {
                                displayName = name
                            }
                            user?.updateProfile(profileUpdates)

                            Toast.makeText(this, getString(R.string.successful), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
