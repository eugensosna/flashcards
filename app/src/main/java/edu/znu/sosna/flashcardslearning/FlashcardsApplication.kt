package edu.znu.sosna.flashcardslearning

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import edu.znu.sosna.flashcardslearning.data.FlashcardsDatabase
import edu.znu.sosna.flashcardslearning.data.FlashcardsRepository
import edu.znu.sosna.flashcardslearning.data.InMemoryFlashcardsRepository
import edu.znu.sosna.flashcardslearning.data.RoomFlashcardsRepository

/** Holds app-wide singletons (simple manual DI). */
class FlashcardsApplication : Application() {
    // In-memory fake data for demos; swap for createRoomRepository() to persist to the database.
    val repository: FlashcardsRepository by lazy { InMemoryFlashcardsRepository() }

    @Suppress("unused")
    private fun createRoomRepository(): FlashcardsRepository {
        val db = FlashcardsDatabase.create(this)
        return RoomFlashcardsRepository(db.deckDao(), db.cardDao())
    }
}

/** Repository lookup for ViewModel factories. */
fun CreationExtras.flashcardsRepository(): FlashcardsRepository =
    (this[APPLICATION_KEY] as FlashcardsApplication).repository
