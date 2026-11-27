package edu.znu.sosna.flashcardslearning.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Passed to the card list screen as a navigation argument.
 * Parcelable, so [edu.znu.sosna.flashcardslearning.ui.navigation.DeckNavType] stores it in the Bundle as is.
 */
@Serializable
@Parcelize
@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
) : Parcelable

/** Deck plus aggregated card counters, used for the progress display on the deck list. */
data class DeckWithStats(
    @Embedded val deck: Deck,
    val cardCount: Int,
    val learnedCount: Int,
) {
    val progress: Float
        get() = if (cardCount == 0) 0f else learnedCount.toFloat() / cardCount
}
