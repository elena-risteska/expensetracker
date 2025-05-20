package com.example.expensetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.graphics.toColorInt

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bold colors
        val backgroundColor = "#FFF176".toColorInt()     // Bright yellow
        val buttonColor = "#7E57C2".toColorInt()          // Purple
        val textColor = "#212121".toColorInt()            // Dark gray
        val accentColor = "#512DA8".toColorInt()          // Deep purple

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(backgroundColor)
            setPadding(64)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val title = TextView(this).apply {
            text = "Create Account"
            textSize = 32f
            setTypeface(null, Typeface.BOLD)
            setTextColor(textColor)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 80)
        }

        fun createRoundedInput(hint: String): TextInputLayout {
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
                })
            }
        }

        val nameInput = createRoundedInput("Full Name")
        val emailInput = createRoundedInput("Email")
        val passwordInput = createRoundedInput("Password")

        val registerButton = Button(this).apply {
            text = "Register"
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
            text = "Already have an account? Login"
            setTextColor(accentColor)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 0)
        }

        layout.apply {
            addView(title)
            addView(nameInput)
            addView(emailInput)
            addView(passwordInput)
            addView(registerButton)
            addView(loginLink)
        }

        setContentView(layout)

        // Logic
        registerButton.setOnClickListener {
            val name = nameInput.editText?.text.toString().trim()
            val email = emailInput.editText?.text.toString().trim()
            val password = passwordInput.editText?.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
