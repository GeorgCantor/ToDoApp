package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.ObjectInspectorState
import com.example.todoapp.domain.usecase.GetNodeByIdUseCase
import com.example.todoapp.domain.usecase.InspectObjectUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ObjectInspectorViewModel(
    private val inspectObjectUseCase: InspectObjectUseCase,
    private val getNodeByIdUseCase: GetNodeByIdUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ObjectInspectorState())
    val state: StateFlow<ObjectInspectorState> = _state.asStateFlow()

    private val _expandedNodes = MutableStateFlow<Set<String>>(mutableSetOf())
    val expandedNodes: StateFlow<Set<String>> = _expandedNodes.asStateFlow()

    fun inspectObject(
        obj: Any?,
        name: String = "Root",
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            inspectObjectUseCase(obj, name).fold(
                onSuccess = { node ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            rootNode = node,
                            currentNode = node,
                        )
                    }
                    toggleNode(node.id, true)
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                },
            )
        }
    }

    fun navigateToNode(nodeId: String) {
        getNodeByIdUseCase(nodeId)?.let { node ->
            _state.update { it.copy(currentNode = node) }
        }
    }

    fun toggleNode(
        nodeId: String,
        expanded: Boolean? = null,
    ) {
        _expandedNodes.update { current ->
            val newSet = current.toMutableSet()
            when (expanded) {
                true -> newSet.add(nodeId)
                false -> newSet.remove(nodeId)
                else -> if (nodeId in newSet) newSet.remove(nodeId) else newSet.add(nodeId)
            }
            newSet
        }
    }

    fun clear() {
        _state.update { ObjectInspectorState() }
        _expandedNodes.update { emptySet() }
    }
}
