package edu.znu.sosna.flashcardslearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.znu.sosna.flashcardslearning.ui.navigation.FlashcardsNavHost
import edu.znu.sosna.flashcardslearning.ui.theme.FlashcardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardsTheme {
                FlashcardsNavHost()
            }
        }
    }
}
