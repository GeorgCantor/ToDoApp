package com.example.todoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.todoapp.R
import com.example.todoapp.domain.model.BadgeType
import com.example.todoapp.domain.model.CartEvent
import com.example.todoapp.domain.model.CartItem
import com.example.todoapp.domain.usecase.CalculateTotalUseCase
import com.example.todoapp.presentation.viewmodel.CartViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onCheckout: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Корзина",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = "${state.itemsCount} товаров • ${viewModel.formatPrice(state.totalPrice)}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                },
                navigationIcon = {},
                actions = {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF0F0F0))
                                .clickable {}
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "Очистить все",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
            )
        },
        bottomBar = {
            CartSummary(
                totalPrice = state.totalPrice,
                itemsCount = state.itemsCount,
                onCheckout = {
                    viewModel.onEvent(CartEvent.Checkout)
                    onCheckout()
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (state.items.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                    Text(
                        text = "Корзина пуста",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                item {
                    Text(
                        text = "Товары (${state.itemsCount})",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    )
                }

                items(
                    items = state.items,
                    key = { it.id },
                ) { item ->
                    SwipeToDeleteContainer(
                        item = item,
                        onDelete = {
                            viewModel.onEvent(CartEvent.RemoveItem(it.id))
                        },
                    ) { cartItem ->
                        CartItemCard(
                            item = cartItem,
                            onIncrement = {
                                viewModel.onEvent(
                                    CartEvent.IncrementQuantity(
                                        cartItem.id,
                                        cartItem.quantity,
                                    ),
                                )
                            },
                            onDecrement = {
                                viewModel.onEvent(
                                    CartEvent.DecrementQuantity(
                                        cartItem.id,
                                        cartItem.quantity,
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(
    type: BadgeType,
    modifier: Modifier = Modifier,
) {
    val (text, backgroundColor, textColor) =
        when (type) {
            BadgeType.PROMOTION -> Triple("Акция!", Color(0xFFFF3B30), Color.White)
            BadgeType.NEW -> Triple("Новинка", Color(0xFF34C759), Color.White)
            BadgeType.BESTSELLER -> Triple("Лидер продаж", Color(0xFFFF9500), Color.White)
        }

    Text(
        text = text,
        fontSize = 11.sp,
        color = textColor,
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .background(backgroundColor)
                .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF92CBF8))
                    .clickable { onDecrement() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "−",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1634F0),
            )
        }

        Box(
            modifier =
                Modifier
                    .height(36.dp)
                    .width(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$quantity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
            )
        }

        Box(
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF92CBF8))
                    .clickable { onIncrement() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1634F0),
            )
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    calculateTotalUseCase: CalculateTotalUseCase = CalculateTotalUseCase(),
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = item.image,
            contentDescription = null,
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_launcher_foreground),
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            if (item.badges.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 4.dp),
                ) {
                    item.badges.forEach { badge ->
                        Badge(type = badge)
                    }
                }
            }

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = calculateTotalUseCase.formatPrice(item.price * item.quantity),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                QuantitySelector(
                    quantity = item.quantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                )
            }
        }
    }
}

@Composable
fun CartSummary(
    totalPrice: Double,
    itemsCount: Int,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier,
    calculateTotalUseCase: CalculateTotalUseCase = CalculateTotalUseCase(),
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Итого:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = calculateTotalUseCase.formatPrice(totalPrice),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "$itemsCount товара",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCheckout,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Оформить заказ",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    content: @Composable (T) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val deleteOffset = with(LocalDensity.current) { 80.dp.toPx() }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isSwiped by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clipToBounds(),
    ) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(end = 16.dp)
                    .align(Alignment.CenterEnd),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(80.dp)
                        .background(MaterialTheme.colorScheme.error)
                        .align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.White,
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state =
                            rememberDraggableState { delta ->
                                if (!isSwiped) {
                                    offsetX = (offsetX + delta).coerceIn(-deleteOffset, 0f)
                                }
                            },
                        onDragStopped = {
                            scope.launch {
                                if (offsetX < -deleteOffset / 2) {
                                    onDelete(item)
                                    isSwiped = true
                                }
                                offsetX = 0f
                                isSwiped = false
                            }
                        },
                    ),
        ) {
            content(item)
        }
    }
}
