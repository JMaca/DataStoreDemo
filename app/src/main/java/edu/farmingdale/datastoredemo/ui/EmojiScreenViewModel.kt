package edu.farmingdale.datastoredemo.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.farmingdale.datastoredemo.R
import edu.farmingdale.datastoredemo.EmojiReleaseApplication
import edu.farmingdale.datastoredemo.data.local.UserPreferencesRepository
import edu.farmingdale.datastoredemo.ui.theme.ThemeState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class EmojiScreenViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    // UI states access for various
    val uiState:
            StateFlow<EmojiReleaseUiState> =
        userPreferencesRepository.isLinearLayout.map { isLinearLayout ->
            EmojiReleaseUiState(isLinearLayout)
        }.stateIn(
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmojiReleaseUiState()
        )

    //new variable to load theme preferences from datastore
    val themeState:
            StateFlow<EmojiReleaseThemeState> =
        userPreferencesRepository.isDarkTheme.map { isDarkTheme ->
            EmojiReleaseThemeState(isDarkTheme)
        }.stateIn(
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmojiReleaseThemeState()
        )

    /*
     * [selectLayout] change the layout and icons accordingly and
     * save the selection in DataStore through [userPreferencesRepository]
     */
    fun selectLayout(isLinearLayout: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveLayoutPreference(isLinearLayout)
        }
    }

    //copy of above function for saving theme
    fun selectTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(isDarkTheme)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as EmojiReleaseApplication)
                EmojiScreenViewModel(application.userPreferencesRepository)
            }
        }
    }
}

/*
 * Data class containing various UI States for Emoji Release screens
 */
data class EmojiReleaseUiState(
    val isLinearLayout: Boolean = true,
    val toggleContentDescription: Int =
        if (isLinearLayout) R.string.grid_layout_toggle else R.string.linear_layout_toggle,
    val toggleIcon: Int =
        if (isLinearLayout) R.drawable.ic_grid_layout else R.drawable.ic_linear_layout
)

//created new data class for loading theme
data class EmojiReleaseThemeState(
    val isDarkTheme: Boolean = false
)



