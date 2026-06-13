                    package com.example.app

                    import android.os.Bundle
                    import androidx.appcompat.app.AppCompatActivity
                    import android.widget.TextView
                    import android.widget.LinearLayout
                    import android.view.Gravity

                    class MainActivity : AppCompatActivity() {
                        override fun onCreate(savedInstanceState: Bundle?) {
                            super.onCreate(savedInstanceState)
                            
                            val layout = LinearLayout(this).apply {
                                orientation = LinearLayout.VERTICAL
                                gravity = Gravity.CENTER
                                setPadding(48, 48, 48, 48)
                            }

                            val titleView = TextView(this).apply {
                                text = "🚀 MyAwesomeApp"
                                textSize = 28f
                                setTextColor(android.graphics.Color.parseColor("#8B5CF6"))
                                gravity = Gravity.CENTER
                            }

                            val descView = TextView(this).apply {
                                text = "Prompt implementation: ek tectactoi game banayo
Features and platform logic integrated for ."
                                textSize = 16f
                                gravity = Gravity.CENTER
                                setPadding(0, 24, 0, 0)
                            }

                            layout.addView(titleView)
                            layout.addView(descView)
                            setContentView(layout)
                        }
                    }