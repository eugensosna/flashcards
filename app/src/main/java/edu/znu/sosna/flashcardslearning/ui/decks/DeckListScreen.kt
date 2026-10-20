package edu.znu.sosna.flashcardslearning.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.znu.sosna.flashcardslearning.R
import edu.znu.sosna.flashcardslearning.data.Deck
import edu.znu.sosna.flashcardslearning.data.DeckWithStats
import edu.znu.sosna.flashcardslearning.ui.components.ConfirmDeleteDialog
import edu.znu.sosna.flashcardslearning.ui.components.DeleteButton
import edu.znu.sosna.flashcardslearning.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    onDeckClick: (Long) -> Unit,
    viewModel: DeckListViewModel = viewModel(factory = DeckListViewModel.Factory),
) {
    val decks by viewModel.decks.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var deckToDelete by remember { mutableStateOf<Deck?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.decks_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = stringResource(R.string.deck_new))
            }
        },
    ) { padding ->
        val list = decks
        when {
            list == null -> Unit
            list.isEmpty() -> EmptyState(stringResource(R.string.decks_empty), Modifier.padding(padding))
            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 88.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(list, key = { it.deck.id }) { item ->
                    DeckItem(
                        item = item,
                        onClick = { onDeckClick(item.deck.id) },
                        onDelete = { deckToDelete = item.deck },
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateDeckDialog(
            onCreate = { name, description ->
                viewModel.createDeck(name, description)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }

    deckToDelete?.let { deck ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.deck_delete_title),
            message = stringResource(R.string.deck_delete_message, deck.name),
            onConfirm = {
                viewModel.deleteDeck(deck)
                deckToDelete = null
            },
            onDismiss = { deckToDelete = null },
        )
    }
}

@Composable
private fun DeckItem(item: DeckWithStats, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.deck.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                DeleteButton(onClick = onDelete)
            }
            Column(Modifier.padding(end = 12.dp)) {
                if (item.deck.description.isNotBlank()) {
                    Text(
                        text = item.deck.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                }
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.deck_progress, item.learnedCount, item.cardCount),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun CreateDeckDialog(onCreate: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.deck_new)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.deck_name)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.deck_description)) },
                    minLines = 2,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name, description) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
