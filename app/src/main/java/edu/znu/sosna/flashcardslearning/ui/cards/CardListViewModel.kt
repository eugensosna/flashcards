package edu.znu.sosna.flashcardslearning.ui.cards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.znu.sosna.flashcardslearning.data.Card
import edu.znu.sosna.flashcardslearning.data.Deck
import edu.znu.sosna.flashcardslearning.data.FlashcardsRepository
import edu.znu.sosna.flashcardslearning.flashcardsRepository
import edu.znu.sosna.flashcardslearning.ui.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardListViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FlashcardsRepository,
) : ViewModel() {

    private val deckId: Long = checkNotNull(savedStateHandle[Routes.ARG_DECK_ID])

    val deck: StateFlow<Deck?> = repository.observeDeck(deckId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** null while the first database emission is pending. */
    val cards: StateFlow<List<Card>?> = repository.observeCards(deckId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun deleteCard(card: Card) {
        viewModelScope.launch { repository.deleteCard(card) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { CardListViewModel(createSavedStateHandle(), flashcardsRepository()) }
        }
    }
}
