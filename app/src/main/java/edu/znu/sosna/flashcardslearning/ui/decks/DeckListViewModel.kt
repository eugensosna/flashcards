package edu.znu.sosna.flashcardslearning.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.znu.sosna.flashcardslearning.data.Deck
import edu.znu.sosna.flashcardslearning.data.DeckWithStats
import edu.znu.sosna.flashcardslearning.data.FlashcardsRepository
import edu.znu.sosna.flashcardslearning.flashcardsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeckListViewModel(private val repository: FlashcardsRepository) : ViewModel() {

    /** null while the first database emission is pending. */
    val decks: StateFlow<List<DeckWithStats>?> = repository.observeDecksWithStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun createDeck(name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.createDeck(name, description) }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch { repository.deleteDeck(deck) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { DeckListViewModel(flashcardsRepository()) }
        }
    }
}
