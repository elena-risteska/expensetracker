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

class LoginActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backgroundColor = "#FFF176".toColorInt()    // Bright Yellow
        val buttonColor = "#7E57C2".toColorInt()         // Purple
        val textColor = "#212121".toColorInt()           // Dark Gray
        val accentColor = "#512DA8".toColorInt()         // Deep Purple

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
            text = "Login to Expense Tracker"
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
                        inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                })
            }
        }

        val emailInput = createRoundedInput("Email")
        val passwordInput = createRoundedInput("Password", isPassword = true)

        val loginButton = Button(this).apply {
            text = "Login"
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
            text = "Don't have an account? Register"
            setTextColor(accentColor)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 0)
        }

        layout.apply {
            addView(title)
            addView(emailInput)
            addView(passwordInput)
            addView(loginButton)
            addView(registerLink)
        }

        setContentView(layout)

        // Logic
        loginButton.setOnClickListener {
            val email = emailInput.editText?.text.toString().trim()
            val password = passwordInput.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
