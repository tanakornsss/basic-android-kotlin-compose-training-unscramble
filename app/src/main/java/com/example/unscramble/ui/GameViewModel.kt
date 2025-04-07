package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private var currentWord: String = ""

    // Set of words used in the game
    private var usedWords: MutableSet<String> = mutableSetOf()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()

        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        }
        else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()

        // Scramble the words.
        tempWord.shuffle()

        // Keep randomizing words until the result does not match the original string
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }

        return String(tempWord)
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambleWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (!userGuess.equals(currentWord, ignoreCase = true)) {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }

        updateUserGuess("")
    }

}

data class GameUiState (
    val currentScrambleWord: String = "",
    val isGuessedWordWrong: Boolean = false,
)
