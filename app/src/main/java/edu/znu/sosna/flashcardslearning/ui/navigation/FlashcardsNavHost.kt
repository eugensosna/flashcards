package edu.znu.sosna.flashcardslearning.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.znu.sosna.flashcardslearning.ui.cards.CardEditScreen
import edu.znu.sosna.flashcardslearning.ui.cards.CardListScreen
import edu.znu.sosna.flashcardslearning.ui.decks.DeckListScreen

object Routes {
    const val ARG_DECK_ID = "deckId"
    const val ARG_CARD_ID = "cardId"
    const val NEW_CARD_ID = -1L

    const val DECKS = "decks"
    const val CARDS = "decks/{$ARG_DECK_ID}/cards"
    const val CARD_EDIT = "decks/{$ARG_DECK_ID}/cards/edit?$ARG_CARD_ID={$ARG_CARD_ID}"

    fun cards(deckId: Long) = "decks/$deckId/cards"
    fun cardEdit(deckId: Long, cardId: Long = NEW_CARD_ID) = "decks/$deckId/cards/edit?$ARG_CARD_ID=$cardId"
}

@Composable
fun FlashcardsNavHost() {
    val navController = rememberNavController()
    val deckIdArg = navArgument(Routes.ARG_DECK_ID) { type = NavType.LongType }

    NavHost(navController = navController, startDestination = Routes.DECKS) {
        composable(Routes.DECKS) {
            DeckListScreen(onDeckClick = { deckId -> navController.navigate(Routes.cards(deckId)) })
        }

        composable(Routes.CARDS, arguments = listOf(deckIdArg)) { entry ->
            val deckId = entry.arguments!!.getLong(Routes.ARG_DECK_ID)
            CardListScreen(
                onBack = { navController.popBackStack() },
                onAddCard = { navController.navigate(Routes.cardEdit(deckId)) },
                onCardClick = { cardId -> navController.navigate(Routes.cardEdit(deckId, cardId)) },
            )
        }

        composable(
            Routes.CARD_EDIT,
            arguments = listOf(
                deckIdArg,
                navArgument(Routes.ARG_CARD_ID) {
                    type = NavType.LongType
                    defaultValue = Routes.NEW_CARD_ID
                },
            ),
        ) {
            CardEditScreen(onDone = { navController.popBackStack() })
        }
    }
}
