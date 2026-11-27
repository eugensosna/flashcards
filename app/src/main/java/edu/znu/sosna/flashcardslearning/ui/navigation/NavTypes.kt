package edu.znu.sosna.flashcardslearning.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import edu.znu.sosna.flashcardslearning.data.Card
import edu.znu.sosna.flashcardslearning.data.Deck
import kotlinx.serialization.json.Json

/**
 * NavType with Parcelable support: [Deck] is @Parcelize, so it is put into
 * the argument Bundle directly with putParcelable/getParcelable.
 * The string form (used in the route URI) is URL-encoded JSON.
 */
object DeckNavType : NavType<Deck>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Deck? =
        bundle.getParcelable(key, Deck::class.java)

    override fun put(bundle: Bundle, key: String, value: Deck) =
        bundle.putParcelable(key, value)

    override fun parseValue(value: String): Deck =
        Json.decodeFromString(Uri.decode(value))

    override fun serializeAsValue(value: Deck): String =
        Uri.encode(Json.encodeToString(value))

    override val name: String = "Deck"
}

/**
 * NavType without Parcelable support: [Card] is only @Serializable, so it is
 * stored in the argument Bundle as a JSON string.
 */
object CardNavType : NavType<Card>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Card? =
        bundle.getString(key)?.let { Json.decodeFromString<Card>(it) }

    override fun put(bundle: Bundle, key: String, value: Card) =
        bundle.putString(key, Json.encodeToString(value))

    override fun parseValue(value: String): Card =
        Json.decodeFromString(Uri.decode(value))

    override fun serializeAsValue(value: Card): String =
        Uri.encode(Json.encodeToString(value))

    override val name: String = "Card"
}
