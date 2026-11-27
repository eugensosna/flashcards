package edu.znu.sosna.flashcardslearning.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.znu.sosna.flashcardslearning.ui.cards.CardEditScreen
import edu.znu.sosna.flashcardslearning.ui.cards.CardListScreen
import edu.znu.sosna.flashcardslearning.ui.decks.DeckListScreen

/** Navigation graph built with the type-safe (Kotlin Serialization based) API. */
@Composable
fun FlashcardsNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.DeckList) {

        composable<Route.DeckList> {
            DeckListScreen(onDeckClick = { deck -> navController.navigate(Route.CardList(deck)) })
        }

        composable<Route.CardList>(typeMap = Route.CardList.typeMap) {
            CardListScreen(
                onBack = { navController.popBackStack() },
                onAddCard = { deckId -> navController.navigate(Route.CardCreate(deckId)) },
                onCardClick = { card -> navController.navigate(Route.CardEdit(card)) },
            )
        }

        composable<Route.CardCreate> { entry ->
            val route: Route.CardCreate = entry.toRoute()
            CardEditScreen(
                deckId = route.deckId,
                card = null,
                onDone = { navController.popBackStack() },
            )
        }

        composable<Route.CardEdit>(typeMap = Route.CardEdit.typeMap) { entry ->
            val route: Route.CardEdit = entry.toRoute()
            CardEditScreen(
                deckId = route.card.deckId,
                card = route.card,
                onDone = { navController.popBackStack() },
            )
        }
    }
}
