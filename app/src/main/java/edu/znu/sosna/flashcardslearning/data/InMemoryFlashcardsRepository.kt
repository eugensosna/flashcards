package edu.znu.sosna.flashcardslearning.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

/** Keeps everything in memory for quick demos; data is lost when the process dies. */
class InMemoryFlashcardsRepository(
    initialDecks: List<Deck> = SampleData.decks,
    initialCards: List<Card> = SampleData.cards,
) : FlashcardsRepository {
    private val decks = MutableStateFlow(initialDecks)
    private val cards = MutableStateFlow(initialCards)
    private val nextId = AtomicLong(
        maxOf(initialDecks.maxOfOrNull { it.id } ?: 0, initialCards.maxOfOrNull { it.id } ?: 0) + 1
    )

    override fun observeDecksWithStats(): Flow<List<DeckWithStats>> =
        combine(decks, cards) { decks, cards ->
            val byDeck = cards.groupBy { it.deckId }
            decks.sortedByDescending { it.createdAt }.map { deck ->
                val deckCards = byDeck[deck.id].orEmpty()
                DeckWithStats(deck, deckCards.size, deckCards.count { it.isLearned })
            }
        }

    override fun observeDeck(id: Long): Flow<Deck?> =
        decks.map { list -> list.find { it.id == id } }

    override suspend fun createDeck(name: String, description: String): Long {
        val deck = Deck(id = nextId.getAndIncrement(), name = name.trim(), description = description.trim())
        decks.update { it + deck }
        return deck.id
    }

    override suspend fun deleteDeck(deck: Deck) {
        decks.update { list -> list.filterNot { it.id == deck.id } }
        cards.update { list -> list.filterNot { it.deckId == deck.id } }
    }

    override fun observeCards(deckId: Long): Flow<List<Card>> =
        cards.map { list -> list.filter { it.deckId == deckId }.sortedByDescending { it.createdAt } }

    override suspend fun getCard(id: Long): Card? = cards.value.find { it.id == id }

    override suspend fun saveCard(card: Card) {
        val saved = if (card.id == 0L) card.copy(id = nextId.getAndIncrement()) else card
        cards.update { list ->
            if (list.any { it.id == saved.id }) list.map { if (it.id == saved.id) saved else it }
            else list + saved
        }
    }

    override suspend fun deleteCard(card: Card) {
        cards.update { list -> list.filterNot { it.id == card.id } }
    }
}

/** Demo decks and cards. */
object SampleData {
    private val now = System.currentTimeMillis()

    val decks = listOf(
        Deck(id = 1, name = "Food", description = "Everyday food and drinks", createdAt = now - 2_000),
        Deck(id = 2, name = "Travel", description = "Useful words for trips", createdAt = now - 1_000),
    )

    val cards = listOf(
        Card(id = 101, deckId = 1, question = "apple", answer = "яблуко", tags = "fruit", isLearned = true, createdAt = now - 600),
        Card(id = 102, deckId = 1, question = "bread", answer = "хліб", tags = "bakery", createdAt = now - 500),
        Card(id = 103, deckId = 1, question = "cheese", answer = "сир", tags = "dairy", createdAt = now - 400),
        Card(id = 104, deckId = 1, question = "to drink", answer = "пити", tags = "verbs, drinks", isLearned = true, createdAt = now - 300),
        Card(id = 201, deckId = 2, question = "ticket", answer = "квиток", tags = "transport", createdAt = now - 600),
        Card(id = 202, deckId = 2, question = "luggage", answer = "багаж", tags = "airport", createdAt = now - 500),
        Card(id = 203, deckId = 2, question = "to book a room", answer = "забронювати номер", tags = "hotel, verbs", isLearned = true, createdAt = now - 400),
    )
}
