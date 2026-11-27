package edu.znu.sosna.flashcardslearning.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Passed to the card edit screen as a navigation argument.
 * Not Parcelable: [edu.znu.sosna.flashcardslearning.ui.navigation.CardNavType] stores it in the Bundle as a JSON string.
 */
@Serializable
@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("deckId")],
)
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    /** English word or phrase. */
    val question: String,
    /** Translation. */
    val answer: String,
    /** Comma-separated topics, e.g. "food, verbs". */
    val tags: String = "",
    /** Set by the learning mode when the user marks the card as known. */
    val isLearned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
) {
    val tagList: List<String>
        get() = parseTags(tags)
}

fun parseTags(raw: String): List<String> =
    raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }.distinct()
