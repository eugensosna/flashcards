package edu.znu.sosna.flashcardslearning.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.znu.sosna.flashcardslearning.R
import edu.znu.sosna.flashcardslearning.ui.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditScreen(
    onDone: () -> Unit,
    viewModel: CardEditViewModel = viewModel(factory = CardEditViewModel.Factory),
) {
    val state = viewModel.uiState

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onDone()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (viewModel.isNewCard) R.string.card_new else R.string.card_edit))
                },
                navigationIcon = { BackButton(onClick = onDone) },
            )
        },
    ) { padding ->
        if (state.isLoading) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val questionError = state.showErrors && !state.isQuestionValid
            OutlinedTextField(
                value = state.question,
                onValueChange = viewModel::onQuestionChange,
                label = { Text(stringResource(R.string.card_question)) },
                isError = questionError,
                supportingText = if (questionError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )

            val answerError = state.showErrors && !state.isAnswerValid
            OutlinedTextField(
                value = state.answer,
                onValueChange = viewModel::onAnswerChange,
                label = { Text(stringResource(R.string.card_answer)) },
                isError = answerError,
                supportingText = if (answerError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.tags,
                onValueChange = viewModel::onTagsChange,
                label = { Text(stringResource(R.string.card_tags)) },
                placeholder = { Text(stringResource(R.string.card_tags_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
