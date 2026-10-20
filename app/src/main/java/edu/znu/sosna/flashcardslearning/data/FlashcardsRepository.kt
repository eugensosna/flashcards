package edu.znu.sosna.flashcardslearning.data

import kotlinx.coroutines.flow.Flow

interface FlashcardsRepository {
    fun observeDecksWithStats(): Flow<List<DeckWithStats>>

    fun observeDeck(id: Long): Flow<Deck?>

    suspend fun createDeck(name: String, description: String): Long

    suspend fun deleteDeck(deck: Deck)

    fun observeCards(deckId: Long): Flow<List<Card>>

    suspend fun getCard(id: Long): Card?

    suspend fun saveCard(card: Card)

    suspend fun deleteCard(card: Card)
}

class RoomFlashcardsRepository(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
) : FlashcardsRepository {
    override fun observeDecksWithStats(): Flow<List<DeckWithStats>> = deckDao.observeDecksWithStats()

    override fun observeDeck(id: Long): Flow<Deck?> = deckDao.observeDeck(id)

    override suspend fun createDeck(name: String, description: String): Long =
        deckDao.insert(Deck(name = name.trim(), description = description.trim()))

    override suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck)

    override fun observeCards(deckId: Long): Flow<List<Card>> = cardDao.observeCards(deckId)

    override suspend fun getCard(id: Long): Card? = cardDao.getCard(id)

    override suspend fun saveCard(card: Card) = cardDao.upsert(card)

    override suspend fun deleteCard(card: Card) = cardDao.delete(card)
}
