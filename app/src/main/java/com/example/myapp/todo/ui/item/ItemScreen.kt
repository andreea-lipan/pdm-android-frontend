package com.example.myapp.todo.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.R
import com.example.myapp.core.Result
import com.example.myapp.todo.ui.item.ItemViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(itemId: String?, onClose: () -> Unit) {
    val itemViewModel = viewModel<ItemViewModel>(factory = ItemViewModel.Factory(itemId))
    val itemUiState = itemViewModel.uiState

    var managerName by rememberSaveable { mutableStateOf(itemUiState.item.managerName) }
    var index by rememberSaveable { mutableIntStateOf(itemUiState.item.index) }
    var autumnTreatment by rememberSaveable { mutableStateOf(itemUiState.item.autumnTreatment) }
    var dateCreated by rememberSaveable { mutableStateOf(itemUiState.item.dateCreated) }


    Log.d("index", "index is:  $index");
    Log.d("ItemScreen", "recompose, text = $managerName")

    LaunchedEffect(itemUiState.submitResult) {
        Log.d("ItemScreen", "Submit = ${itemUiState.submitResult}");
        if (itemUiState.submitResult is Result.Success) {
            Log.d("ItemScreen", "Closing screen");
            onClose();
        }
    }

    var textInitialized by remember { mutableStateOf(itemId == null) }
    LaunchedEffect(itemId, itemUiState.loadResult) {
        Log.d("ItemScreen", "Text initialized = ${itemUiState.loadResult}");
        if (textInitialized) {
            return@LaunchedEffect
        }
        if (!(itemUiState.loadResult is Result.Loading)) {
            managerName = itemUiState.item.managerName
            index = itemUiState.item.index;
            autumnTreatment = itemUiState.item.autumnTreatment;
            dateCreated = itemUiState.item.dateCreated;
            textInitialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.item)) },
                actions = {
                    Button(onClick = {
                        Log.d("ItemScreen", "save item text = $managerName");
                        itemViewModel.saveOrUpdateItem(managerName, index, autumnTreatment, dateCreated)
                    }) { Text("Save") }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (itemUiState.loadResult is Result.Loading) {
                CircularProgressIndicator()
                return@Scaffold
            }
            if (itemUiState.submitResult is Result.Loading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { LinearProgressIndicator() }
            }
            if (itemUiState.loadResult is Result.Error) {
                Text(text = "Failed to load item - ${(itemUiState.loadResult as Result.Error).exception?.message}")
            }

            Row {
                TextField(
                    value = index.toString(),
                    onValueChange = { index = it.toInt() }, label = { Text("Index") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row {
                TextField(
                    value = managerName,
                    onValueChange = { managerName = it }, label = { Text("Manger Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row {
                TextField(
                    value = dateCreated,
                    onValueChange = { dateCreated = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row {
                Checkbox(
                    checked = autumnTreatment,
                    onCheckedChange = { autumnTreatment = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (itemUiState.submitResult is Result.Error) {
                Text(
                    text = "Failed to submit item - ${(itemUiState.submitResult as Result.Error).exception?.message}",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewItemScreen() {
    ItemScreen(itemId = "0", onClose = {})
}
