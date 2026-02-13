package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.CartEvent
import com.example.todoapp.domain.model.CartItem
import com.example.todoapp.domain.model.CartState
import com.example.todoapp.domain.usecase.CalculateTotalUseCase
import com.example.todoapp.domain.usecase.GetCartItemsUseCase
import com.example.todoapp.domain.usecase.RemoveFromCartUseCase
import com.example.todoapp.domain.usecase.UpdateQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val calculateTotalUseCase: CalculateTotalUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        loadCart()
    }

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.IncrementQuantity -> {
                updateQuantity(event.itemId, event.currentQuantity + 1)
            }
            is CartEvent.DecrementQuantity -> {
                updateQuantity(event.itemId, event.currentQuantity - 1)
            }
            is CartEvent.RemoveItem -> {
                removeItem(event.itemId)
            }
            CartEvent.Checkout -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val items = getCartItemsUseCase()
                updateState(items)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось загрузить корзину",
                    )
                }
            }
        }
    }

    private fun updateQuantity(
        itemId: Int,
        newQuantity: Int,
    ) {
        viewModelScope.launch {
            try {
                updateQuantityUseCase(itemId, newQuantity)

                _state.update { currentState ->
                    val updatedItems =
                        currentState.items
                            .map { item ->
                                if (item.id == itemId) {
                                    item.copy(quantity = newQuantity)
                                } else {
                                    item
                                }
                            }.filter { it.quantity > 0 }

                    currentState.copy(
                        items = updatedItems,
                        totalPrice = calculateTotalUseCase(updatedItems),
                        itemsCount = updatedItems.sumOf { it.quantity },
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Не удалось обновить количество") }
            }
        }
    }

    private fun removeItem(itemId: Int) {
        viewModelScope.launch {
            try {
                removeFromCartUseCase(itemId)

                _state.update { currentState ->
                    val updatedItems = currentState.items.filter { it.id != itemId }

                    currentState.copy(
                        items = updatedItems,
                        totalPrice = calculateTotalUseCase(updatedItems),
                        itemsCount = updatedItems.sumOf { it.quantity },
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Не удалось удалить товар") }
            }
        }
    }

    private fun updateState(items: List<CartItem>) {
        _state.update {
            it.copy(
                items = items,
                totalPrice = calculateTotalUseCase(items),
                itemsCount = items.sumOf { item -> item.quantity },
                isLoading = false,
                error = null,
            )
        }
    }

    fun formatPrice(price: Double): String = NumberFormat.getNumberInstance(Locale("ru", "RU")).format(price) + " ₽"
}
