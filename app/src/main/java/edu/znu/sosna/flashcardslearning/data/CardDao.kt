package edu.znu.sosna.flashcardslearning.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY createdAt DESC")
    fun observeCards(deckId: Long): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCard(id: Long): Card?

    @Upsert
    suspend fun upsert(card: Card)

    @Delete
    suspend fun delete(card: Card)
}
