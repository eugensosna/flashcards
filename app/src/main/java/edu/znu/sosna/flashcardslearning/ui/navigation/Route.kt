package edu.znu.sosna.flashcardslearning.ui.navigation

import edu.znu.sosna.flashcardslearning.data.Card
import edu.znu.sosna.flashcardslearning.data.Deck
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

/** All destinations of the app. Each route is a serializable type, so arguments are checked at compile time. */
@Serializable
sealed interface Route {

    /** Start screen: the list of decks. No arguments. */
    @Serializable
    data object DeckList : Route

    /** Cards of one deck. The whole [Deck] object is passed. */
    @Serializable
    data class CardList(val deck: Deck) : Route {
        companion object {
            val typeMap = mapOf(typeOf<Deck>() to DeckNavType)
        }
    }

    /** Form for a new card in the deck with [deckId]. Only a primitive argument. */
    @Serializable
    data class CardCreate(val deckId: Long) : Route

    /** Form for editing an existing card. The whole [Card] object is passed. */
    @Serializable
    data class CardEdit(val card: Card) : Route {
        companion object {
            val typeMap = mapOf(typeOf<Card>() to CardNavType)
        }
    }
}
