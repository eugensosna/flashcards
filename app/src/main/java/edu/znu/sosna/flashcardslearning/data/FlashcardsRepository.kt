package edu.znu.sosna.flashcardslearning.data

import kotlinx.coroutines.flow.Flow

class FlashcardsRepository(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
) {
    fun observeDecksWithStats(): Flow<List<DeckWithStats>> = deckDao.observeDecksWithStats()

    fun observeDeck(id: Long): Flow<Deck?> = deckDao.observeDeck(id)

    suspend fun createDeck(name: String, description: String): Long =
        deckDao.insert(Deck(name = name.trim(), description = description.trim()))

    suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck)

    fun observeCards(deckId: Long): Flow<List<Card>> = cardDao.observeCards(deckId)

    suspend fun getCard(id: Long): Card? = cardDao.getCard(id)

    suspend fun saveCard(card: Card) = cardDao.upsert(card)

    suspend fun deleteCard(card: Card) = cardDao.delete(card)
}
