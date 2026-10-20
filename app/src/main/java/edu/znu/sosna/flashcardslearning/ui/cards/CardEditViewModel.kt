package edu.znu.sosna.flashcardslearning.ui.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.znu.sosna.flashcardslearning.data.Card
import edu.znu.sosna.flashcardslearning.data.FlashcardsRepository
import edu.znu.sosna.flashcardslearning.data.parseTags
import edu.znu.sosna.flashcardslearning.flashcardsRepository
import edu.znu.sosna.flashcardslearning.ui.navigation.Routes
import kotlinx.coroutines.launch

data class CardEditUiState(
    val question: String = "",
    val answer: String = "",
    val tags: String = "",
    /** Validation errors are shown only after the first save attempt. */
    val showErrors: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
) {
    val isQuestionValid: Boolean get() = question.isNotBlank()
    val isAnswerValid: Boolean get() = answer.isNotBlank()
}

class CardEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FlashcardsRepository,
) : ViewModel() {

    private val deckId: Long = checkNotNull(savedStateHandle[Routes.ARG_DECK_ID])
    private val cardId: Long = savedStateHandle[Routes.ARG_CARD_ID] ?: Routes.NEW_CARD_ID

    val isNewCard: Boolean = cardId == Routes.NEW_CARD_ID

    /** Loaded card when editing; keeps learning progress and creation time on update. */
    private var existing: Card? = null

    var uiState by mutableStateOf(CardEditUiState(isLoading = !isNewCard))
        private set

    init {
        if (!isNewCard) {
            viewModelScope.launch {
                val card = repository.getCard(cardId)
                existing = card
                uiState = if (card == null) {
                    uiState.copy(isLoading = false)
                } else {
                    uiState.copy(
                        question = card.question,
                        answer = card.answer,
                        tags = card.tags,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onQuestionChange(value: String) {
        uiState = uiState.copy(question = value)
    }

    fun onAnswerChange(value: String) {
        uiState = uiState.copy(answer = value)
    }

    fun onTagsChange(value: String) {
        uiState = uiState.copy(tags = value)
    }

    fun save() {
        val state = uiState
        if (!state.isQuestionValid || !state.isAnswerValid) {
            uiState = state.copy(showErrors = true)
            return
        }
        val base = existing ?: Card(deckId = deckId, question = "", answer = "")
        val card = base.copy(
            question = state.question.trim(),
            answer = state.answer.trim(),
            tags = parseTags(state.tags).joinToString(", "),
        )
        viewModelScope.launch {
            repository.saveCard(card)
            uiState = uiState.copy(isSaved = true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { CardEditViewModel(createSavedStateHandle(), flashcardsRepository()) }
        }
    }
}
