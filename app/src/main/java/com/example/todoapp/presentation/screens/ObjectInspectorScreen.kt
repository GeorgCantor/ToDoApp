package com.example.todoapp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.todoapp.R
import com.example.todoapp.domain.model.InspectionNode
import com.example.todoapp.presentation.utils.TestData
import com.example.todoapp.presentation.viewmodel.ObjectInspectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectInspectorScreen(viewModel: ObjectInspectorViewModel) {
    val state by viewModel.state.collectAsState()
    val expandedNodes by viewModel.expandedNodes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.object_inspector)) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                actions = {
                    IconButton(onClick = { viewModel.clear() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            TestObjectSelector(
                onObjectSelected = { obj, name ->
                    viewModel.inspectObject(obj, name)
                },
                modifier = Modifier.padding(16.dp),
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    ErrorBanner(
                        error = state.error.orEmpty(),
                        onDismiss = { viewModel.clear() },
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            state.rootNode?.let { rootNode ->
                InspectionTreeView(
                    node = if (state.currentNode != null) state.currentNode!! else rootNode,
                    expandedNodes = expandedNodes,
                    onNodeClick = { node ->
                        viewModel.navigateToNode(node.id)
                    },
                    onNodeToggle = { nodeId, expanded ->
                        viewModel.toggleNode(nodeId, expanded)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                )
            } ?: run {
                EmptyState(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestObjectSelector(
    onObjectSelected: (Any, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Select test object") }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Test Objects",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Simple String") },
                        onClick = {
                            selectedOption = "Simple String"
                            onObjectSelected("Hello World!", "String")
                            expanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Number (42)") },
                        onClick = {
                            selectedOption = "Number"
                            onObjectSelected(42, "Int")
                            expanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("List of numbers") },
                        onClick = {
                            selectedOption = "List"
                            onObjectSelected(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), "List<Int>")
                            expanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Map") },
                        onClick = {
                            selectedOption = "Map"
                            onObjectSelected(
                                mapOf(
                                    "name" to "Android",
                                    "age" to 20,
                                    "city" to "Moscow",
                                    "hobbies" to listOf("reading", "swimming"),
                                ),
                                "Map<String, Any>",
                            )
                            expanded = false
                        },
                    )
                    Divider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Complex Person Object",
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        onClick = {
                            selectedOption = "Complex Person"
                            onObjectSelected(TestData.complexObject, "Person")
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun InspectionTreeView(
    node: InspectionNode,
    expandedNodes: Set<String>,
    onNodeClick: (InspectionNode) -> Unit,
    onNodeToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    indentLevel: Int = 0,
) {
    val isExpanded = expandedNodes.contains(node.id)
    val indent = indentLevel * 20.dp

    Column(modifier = modifier) {
        TreeNode(
            node = node,
            isExpanded = isExpanded,
            onClick = { onNodeClick(node) },
            onToggle = { onNodeToggle(node.id, !isExpanded) },
            modifier = Modifier.padding(start = indent),
        )

        if (isExpanded && node.hasChildren) {
            Column(
                modifier = Modifier.padding(start = 8.dp),
            ) {
                node.children.forEach { childNode ->
                    if (childNode.isRecursive) {
                        RecursiveNode(
                            node = childNode,
                            onNodeClick = { onNodeClick(childNode) },
                            modifier = Modifier.padding(start = (indentLevel + 1) * 20.dp),
                        )
                    } else {
                        InspectionTreeView(
                            node = childNode,
                            expandedNodes = expandedNodes,
                            onNodeClick = onNodeClick,
                            onNodeToggle = onNodeToggle,
                            indentLevel = indentLevel + 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TreeNode(
    node: InspectionNode,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (node.modifiers.contains("private")) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            androidx.compose.ui.graphics.Color.Transparent
        }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (node.hasChildren) {
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }

            node.modifiers.takeIf { it.isNotEmpty() }?.let { modifiers ->
                Text(
                    text = modifiers.joinToString(" ") { "[$it]" },
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(end = 4.dp),
                )
            }

            Text(
                text = node.name,
                fontWeight = if (node.isPrimitive) FontWeight.Normal else FontWeight.Bold,
                color = if (node.isNull) androidx.compose.ui.graphics.Color.Gray else MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = ": ${node.type}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            if (node.value != null) {
                Text(
                    text = "= ${node.value}",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun RecursiveNode(
    node: InspectionNode,
    onNodeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clickable { onNodeClick() },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Recursive",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp),
            )

            Text(
                text = " ${node.name} → (recursive to ${node.recursiveId ?: "same object"})",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
fun ErrorBanner(
    error: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select an object to inspect",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
            Text(
                text = "Choose from the dropdown above",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
        }
    }
}
