package com.example.myapp.todo.ui.items

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.R
import com.example.myapp.todo.ui.networkstatus.MyNetworkStatusViewModel
import com.example.myapp.util.MyJobsViewModel
import com.example.myapp.util.maps.MyLocation
import com.example.myapp.util.proximity.ProximitySensorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class TabPage {
    Beehives, Map, Info
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(onItemClick: (id: String?) -> Unit, onAddItem: () -> Unit, onLogout: () -> Unit) {
    Log.d("ItemsScreen", "recompose")


    // TABS
    var tabPage by remember { mutableStateOf(TabPage.Beehives) }

    Scaffold(
        topBar = {

            HomeTabBar(
                tabPage = tabPage,
                onTabSelected = { tabPage = it }
            )
        },
        content = {
            when (tabPage) {
                TabPage.Beehives -> {
                    BeehivesTab(onItemClick, onAddItem, onLogout);
                }
                TabPage.Map -> {
                    MyLocation()
                }
                TabPage.Info -> {
                    InfoTab();
                }
            }
        }
    )

}

@Preview
@Composable
fun PreviewItemsScreen() {
    ItemsScreen(onItemClick = {}, onAddItem = {}, onLogout = {})
}


@Composable
private fun HomeTabBar(
    tabPage: TabPage,
    onTabSelected: (tabPage: TabPage) -> Unit
) {
    TabRow(
        selectedTabIndex = tabPage.ordinal,
    ) {
        MyTab(
            icon = Icons.Default.Home,
            title = "Beehives",
            onClick = { onTabSelected(TabPage.Beehives) }
        )
        MyTab(
            icon = Icons.Default.AccountBox,
            title = "Map",
            onClick = { onTabSelected(TabPage.Map) }
        )
        MyTab(
            icon = Icons.Default.AccountBox,
            title = "Info",
            onClick = {
                onTabSelected(TabPage.Info);
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeehivesTab(onItemClick: (id: String?) -> Unit, onAddItem: () -> Unit, onLogout: () -> Unit) {

    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = listOf()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.items)) },
                actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onLogout) {
                    Text("Logout")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("ItemsScreen", "add")
                    onAddItem()
                },
            ) { Icon(Icons.Rounded.Add, "Add") }
        },
        content = {
            ItemList(
                itemList = itemsUiState,
                onItemClick = onItemClick,
                modifier = Modifier.padding(it)
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTab() {

    // NETWORK STATUS
    val myNewtworkStatusViewModel = viewModel<MyNetworkStatusViewModel>(
        factory = MyNetworkStatusViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )


    // JOBS
    val myJobsViewModel = viewModel<MyJobsViewModel>(
        factory = MyJobsViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )


    // PROXIMITY SENSOR
    val proximitySensorViewModel = viewModel<ProximitySensorViewModel>(
        factory = ProximitySensorViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )

    // Dealayed loading
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    suspend fun loadItem() {
        if (!loading) {
            loading = true
            delay(3000L)
            loading = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        if (loading) {
            LoadingRow()
        } else {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                PopUpMessage(shown = myJobsViewModel.uiState.isRunning)
                Text("Network status --> Is online: ${myNewtworkStatusViewModel.uiState}")
                Text(text = "ProximitySensor --> ${proximitySensorViewModel.uiState} cms away")
                Text("Job seconds --> ${myJobsViewModel.uiState.progress}")
                Text("Job done --> ${!myJobsViewModel.uiState.isRunning}")
                Button(onClick = {
                    myJobsViewModel.cancelJob();
                    coroutineScope.launch {
                    loadItem()
                    }
                }) {
                    Text("Cancel")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        loadItem()
                    }
                }) {
                    Text("Refresh")
                }
            }



        }
    }


}


@Composable
private fun PopUpMessage(shown: Boolean) {
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondary,
            elevation = 4.dp
        ) {
            androidx.compose.material.Text(
                text = "Job not finished",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}



@Composable
private fun LoadingRow() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = alpha))
        )

    }
}