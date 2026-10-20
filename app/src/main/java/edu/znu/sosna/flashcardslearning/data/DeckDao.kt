package edu.znu.sosna.flashcardslearning.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query(
        """
        SELECT d.*, COUNT(c.id) AS cardCount, COALESCE(SUM(c.isLearned), 0) AS learnedCount
        FROM decks d LEFT JOIN cards c ON c.deckId = d.id
        GROUP BY d.id
        ORDER BY d.createdAt DESC
        """
    )
    fun observeDecksWithStats(): Flow<List<DeckWithStats>>

    @Query("SELECT * FROM decks WHERE id = :id")
    fun observeDeck(id: Long): Flow<Deck?>

    @Insert
    suspend fun insert(deck: Deck): Long

    @Delete
    suspend fun delete(deck: Deck)
}
