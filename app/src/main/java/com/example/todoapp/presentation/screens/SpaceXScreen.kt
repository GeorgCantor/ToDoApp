package com.example.todoapp.presentation.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.todoapp.R
import com.example.todoapp.domain.model.SpaceXLaunch
import com.example.todoapp.domain.model.SpaceXUiState
import com.example.todoapp.presentation.viewmodel.SpaceXViewModel
import com.example.todoapp.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceXScreen(
    viewModel: SpaceXViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()
    val showDetail = viewModel.showDetail.collectAsState()
    val selectedLaunch = viewModel.selectedLaunch.collectAsState()
    val detailLoading = viewModel.detailLoading.collectAsState()
    val detailError = viewModel.detailError.collectAsState()

    if (showDetail.value) {
        LaunchDetailDialog(
            launch = selectedLaunch.value,
            isLoading = detailLoading.value,
            error = detailError.value,
            onDismiss = { viewModel.closeLaunchDetail() },
            onRetry = {
                viewModel.openLaunchDetail(selectedLaunch.value?.id)
            },
        )
    }

    LaunchedEffect(Unit) {
        if (uiState.value is SpaceXUiState.Loading) {
            viewModel.loadLaunches()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = stringResource(R.string.spacex),
                            modifier = Modifier.size(24.dp),
                        )
                        Text(stringResource(R.string.spacex))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.loadLaunches() },
                modifier = Modifier.padding(16.dp),
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.retry),
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            val state = uiState.value
            when (state) {
                is SpaceXUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.loading_spacex))
                    }
                }

                is SpaceXUiState.Success -> {
                    if (state.launches.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                stringResource(R.string.no_launches),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding =
                                androidx.compose.foundation.layout
                                    .PaddingValues(16.dp),
                        ) {
                            items(items = state.launches) { launch ->
                                LaunchCard(
                                    launch = launch,
                                    onClick = { viewModel.openLaunchDetail(launch.id) },
                                )
                            }
                        }
                    }
                }

                is SpaceXUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FloatingActionButton(
                            onClick = { viewModel.loadLaunches() },
                            modifier = Modifier,
                        ) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = stringResource(R.string.retry),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LaunchCard(
    launch: SpaceXLaunch,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = launch.missionName.orEmpty(),
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                LaunchStatusBadge(
                    success = launch.launchSuccess,
                    upcoming = launch.upcoming ?: false,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    InfoRow(
                        label = stringResource(R.string.rocket),
                        value = launch.rocketName,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    InfoRow(
                        label = stringResource(R.string.launch_date),
                        value = launch.launchDateUtc?.toFormattedDate().orEmpty(),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    InfoRow(
                        label = stringResource(R.string.site),
                        value = launch.launchSite.siteName,
                    )
                }

                launch.links.missionPatchSmall?.let { patchUrl ->
                    AsyncImage(
                        model =
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(patchUrl)
                                .crossfade(true)
                                .build(),
                        contentDescription = stringResource(R.string.mission),
                        modifier =
                            Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            launch.details?.takeIf { it.isNotBlank() }?.let { details ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
fun LaunchStatusBadge(
    success: Boolean?,
    upcoming: Boolean,
) {
    val (text, color, icon) =
        when {
            upcoming ->
                Triple(
                    stringResource(R.string.upcoming),
                    MaterialTheme.colorScheme.tertiary,
                    Icons.Filled.Build,
                )
            success == true ->
                Triple(
                    stringResource(R.string.successful),
                    Color(0xFF4CAF50),
                    Icons.Filled.ThumbUp,
                )
            else ->
                Triple(
                    stringResource(R.string.failed),
                    MaterialTheme.colorScheme.error,
                    Icons.Filled.Warning,
                )
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun Info(
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.width(80.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
