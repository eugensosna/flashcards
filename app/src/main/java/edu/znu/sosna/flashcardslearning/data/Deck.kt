package edu.znu.sosna.flashcardslearning.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

/** Deck plus aggregated card counters, used for the progress display on the deck list. */
data class DeckWithStats(
    @Embedded val deck: Deck,
    val cardCount: Int,
    val learnedCount: Int,
) {
    val progress: Float
        get() = if (cardCount == 0) 0f else learnedCount.toFloat() / cardCount
}
