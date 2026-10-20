package edu.znu.sosna.flashcardslearning

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import edu.znu.sosna.flashcardslearning.data.FlashcardsDatabase
import edu.znu.sosna.flashcardslearning.data.FlashcardsRepository

/** Holds app-wide singletons (simple manual DI). */
class FlashcardsApplication : Application() {
    val repository: FlashcardsRepository by lazy {
        val db = FlashcardsDatabase.create(this)
        FlashcardsRepository(db.deckDao(), db.cardDao())
    }
}

/** Repository lookup for ViewModel factories. */
fun CreationExtras.flashcardsRepository(): FlashcardsRepository =
    (this[APPLICATION_KEY] as FlashcardsApplication).repository
