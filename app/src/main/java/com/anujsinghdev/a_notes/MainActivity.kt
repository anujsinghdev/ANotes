package com.anujsinghdev.a_notes

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.anujsinghdev.a_notes.presentation.navigation.NavGraph
import com.anujsinghdev.a_notes.presentation.theme.ANotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember {
                mutableStateOf(sharedPref.getBoolean("is_dark_theme", systemDark))
            }

            ANotesTheme(darkTheme = isDarkTheme) {
                // FIX: Wrap everything in a Surface with the background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavGraph(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = {
                            val newTheme = !isDarkTheme
                            isDarkTheme = newTheme
                            with(sharedPref.edit()) {
                                putBoolean("is_dark_theme", newTheme)
                                apply()
                            }
                        }
                    )
                }
            }
        }
    }
}