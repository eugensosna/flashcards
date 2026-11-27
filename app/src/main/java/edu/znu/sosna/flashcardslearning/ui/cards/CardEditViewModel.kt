package edu.znu.sosna.flashcardslearning.ui.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.znu.sosna.flashcardslearning.data.Card
import edu.znu.sosna.flashcardslearning.data.FlashcardsRepository
import edu.znu.sosna.flashcardslearning.data.parseTags
import edu.znu.sosna.flashcardslearning.flashcardsRepository
import kotlinx.coroutines.launch

data class CardEditUiState(
    val question: String = "",
    val answer: String = "",
    val tags: String = "",
    /** Validation errors are shown only after the first save attempt. */
    val showErrors: Boolean = false,
    val isSaved: Boolean = false,
) {
    val isQuestionValid: Boolean get() = question.isNotBlank()
    val isAnswerValid: Boolean get() = answer.isNotBlank()
}

/**
 * [existing] is the object passed through the type-safe route [edu.znu.sosna.flashcardslearning.ui.navigation.Route.CardEdit];
 * it is null for [edu.znu.sosna.flashcardslearning.ui.navigation.Route.CardCreate].
 */
class CardEditViewModel(
    private val deckId: Long,
    /** Card being edited; keeps learning progress and creation time on update. */
    private val existing: Card?,
    private val repository: FlashcardsRepository,
) : ViewModel() {

    val isNewCard: Boolean = existing == null

    /** The passed object already holds all data, so no repository lookup is needed. */
    var uiState by mutableStateOf(
        CardEditUiState(
            question = existing?.question.orEmpty(),
            answer = existing?.answer.orEmpty(),
            tags = existing?.tags.orEmpty(),
        )
    )
        private set

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
        fun factory(deckId: Long, card: Card?): ViewModelProvider.Factory = viewModelFactory {
            initializer { CardEditViewModel(deckId, card, flashcardsRepository()) }
        }
    }
}
