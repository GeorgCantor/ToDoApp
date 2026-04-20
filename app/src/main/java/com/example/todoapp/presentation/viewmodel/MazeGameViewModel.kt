package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.SensorRepositoryImpl
import com.example.todoapp.domain.model.MazeGameState
import com.example.todoapp.domain.repository.SensorRepository
import com.example.todoapp.domain.usecase.GameUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MazeGameViewModel(
    private val repository: SensorRepository,
    private val useCase: GameUseCase
) : ViewModel() {
    val gameState: StateFlow<MazeGameState> = useCase.state

    fun startGame() {
        viewModelScope.launch {
            var lastTime = System.currentTimeMillis()
            repository.getGravityFlow().collect { gravity ->
                val now = System.currentTimeMillis()
                val dt = (now - lastTime) / 1000F
                lastTime = now
                if (dt < 0.05F) {
                    useCase.update(dt.coerceAtMost(0.05F), gravity)
                }
            }
        }
    }

    fun resetGame() {
        useCase.resetGame()
        startGame()
    }

    override fun onCleared() {
        (repository as? SensorRepositoryImpl)?.unregister()
        super.onCleared()
    }
}
