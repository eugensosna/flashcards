package edu.znu.sosna.flashcardslearning.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.znu.sosna.flashcardslearning.R
import edu.znu.sosna.flashcardslearning.data.Card
import edu.znu.sosna.flashcardslearning.ui.components.BackButton
import edu.znu.sosna.flashcardslearning.ui.components.ConfirmDeleteDialog
import edu.znu.sosna.flashcardslearning.ui.components.DeleteButton
import edu.znu.sosna.flashcardslearning.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(
    onBack: () -> Unit,
    onAddCard: () -> Unit,
    onCardClick: (Long) -> Unit,
    viewModel: CardListViewModel = viewModel(factory = CardListViewModel.Factory),
) {
    val deck by viewModel.deck.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    var cardToDelete by remember { mutableStateOf<Card?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(deck?.name.orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCard) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = stringResource(R.string.card_add))
            }
        },
    ) { padding ->
        val list = cards
        when {
            list == null -> Unit
            list.isEmpty() -> EmptyState(stringResource(R.string.cards_empty), Modifier.padding(padding))
            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 88.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(list, key = { it.id }) { card ->
                    CardItem(
                        card = card,
                        onClick = { onCardClick(card.id) },
                        onDelete = { cardToDelete = card },
                    )
                }
            }
        }
    }

    cardToDelete?.let { card ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.card_delete_title),
            message = stringResource(R.string.card_delete_message, card.question),
            onConfirm = {
                viewModel.deleteCard(card)
                cardToDelete = null
            },
            onDismiss = { cardToDelete = null },
        )
    }
}

@Composable
private fun CardItem(card: Card, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 4.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(card.question, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = card.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val tags = card.tagList
                if (tags.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { tag -> AssistChip(onClick = onClick, label = { Text(tag) }) }
                    }
                }
            }
            DeleteButton(onClick = onDelete)
        }
    }
}
