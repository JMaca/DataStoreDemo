package edu.farmingdale.datastoredemo.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.farmingdale.datastoredemo.R
import edu.farmingdale.datastoredemo.data.local.LocalEmojiData
import edu.farmingdale.datastoredemo.ui.theme.DataStoreDemoTheme

/*
 * Screen level composable
 */
@Composable
fun EmojiReleaseApp(
    emojiViewModel: EmojiScreenViewModel = viewModel(
        factory = EmojiScreenViewModel.Factory
    )
) {
    EmojiScreen(
        uiState = emojiViewModel.uiState.collectAsState().value,
        selectLayout = emojiViewModel::selectLayout,
        themeState = emojiViewModel.themeState.collectAsState().value,
        selectTheme = emojiViewModel::selectTheme
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmojiScreen(
    uiState: EmojiReleaseUiState,
    themeState: EmojiReleaseThemeState,
    selectTheme: (Boolean) -> Unit,
    selectLayout: (Boolean) -> Unit
) {
    val isLinearLayout = uiState.isLinearLayout
    //load darkMode variable from preferences
    var isDarkMode = themeState.isDarkTheme
    //Apply preference to current theme
    DataStoreDemoTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.top_bar_name)) },
                    actions = {
                        //Added a Switch to the top bar for easy accessibility
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = {
                                isDarkMode = it
                                //when switched change themes then save it
                                selectTheme(isDarkMode)
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                        IconButton(
                            onClick = {
                                selectLayout(!isLinearLayout)
                            }
                        ) {
                            Icon(
                                painter = painterResource(uiState.toggleIcon),
                                contentDescription = stringResource(uiState.toggleContentDescription),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.inversePrimary
                    )
                )
            }
        ) { innerPadding ->
            val modifier = Modifier
                .padding(
                    top = dimensionResource(R.dimen.padding_medium),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                )
            if (isLinearLayout) {
                EmojiReleaseLinearLayout(
                    modifier = modifier.fillMaxWidth(),
                    contentPadding = innerPadding
                )
            } else {
                EmojiReleaseGridLayout(
                    modifier = modifier,
                    contentPadding = innerPadding,
                )
            }
        }
    }
}

@Composable
fun EmojiReleaseLinearLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val cntxt = LocalContext.current
    //created map to reference emoji to text descriptions
    val map = LocalEmojiData.EmojiToText
    DataStoreDemoTheme {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            items(
                items = LocalEmojiData.EmojiList,
                key = { e -> e }
            ) { e ->
                Card(
                    //Created onClick to make each card clickable with Toast comment
                    onClick = {
                        Toast.makeText(cntxt, map.get(e), Toast.LENGTH_SHORT).show()
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {

                    Text(
                        text = e, fontSize = 50.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_medium)),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun EmojiReleaseGridLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val cntxt = LocalContext.current
    val map = LocalEmojiData.EmojiToText

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        items(
            items = LocalEmojiData.EmojiList,
            key = { e -> e }
        ) { e ->
            Card(
                //Created onClick to make each card clickable with Toast comment
                onClick = {
                    Toast.makeText(cntxt, map.get(e), Toast.LENGTH_SHORT).show()
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.height(110.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = e, fontSize = 50.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}