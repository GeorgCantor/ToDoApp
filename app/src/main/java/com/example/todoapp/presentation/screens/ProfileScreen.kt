package com.example.todoapp.presentation.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.todoapp.R
import com.example.todoapp.domain.manager.ImageManager
import com.example.todoapp.domain.model.AppTheme
import com.example.todoapp.domain.model.UserPreferences
import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.model.UserStatistics
import com.example.todoapp.presentation.components.ImagePickerDialog
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.utils.rememberImagePicker
import com.example.todoapp.presentation.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }
    var editedProfile by remember { mutableStateOf(profileState ?: UserProfile().createDefaultProfile()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showImagePickerDialog by remember { mutableStateOf(false) }
    val imageManager = ImageManager(context)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker =
        rememberImagePicker(
            onImageSelected = { uri ->
                selectedImageUri = uri
                showImagePickerDialog = false
            },
            onError = { showImagePickerDialog = false },
        )

    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            try {
                val imagePath = imageManager.saveImageFromUri(uri)
                imagePath?.let { path ->
                    editedProfile = editedProfile.copy(photoPath = path)
                    viewModel.saveProfile(editedProfile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                selectedImageUri = null
            }
        }
    }

    LaunchedEffect(profileState) {
        profileState?.let {
            editedProfile = it
            isEditing = false
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.profile_saved_successfully),
                duration = SnackbarDuration.Short,
            )
            isEditing = false
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long,
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                viewModel.saveProfile(editedProfile)
                            },
                            enabled = !isLoading,
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = stringResource(R.string.save))
                            }
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                        }
                    }

                    IconButton(
                        onClick = { navController.navigate(NavRoutes.TicTacToe.route) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = stringResource(R.string.tictactoe_title),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            if (profileState == null && !isLoading) {
                EmptyProfileState(
                    onCreateProfile = {
                        isEditing = true
                        editedProfile = UserProfile().createDefaultProfile()
                    },
                )
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ProfileHeaderSection(
                        profile = editedProfile,
                        isEditing = isEditing,
                        onProfileImageChange = { action ->
                            if (action == "open_dialog") showImagePickerDialog = true
                        },
                        imageManager = imageManager,
                    )

                    PersonalInfoSection(
                        profile = editedProfile,
                        isEditing = isEditing,
                        onProfileChange = { newProfile ->
                            editedProfile = newProfile
                        },
                    )

                    StatisticsSection(profile = editedProfile)

                    PreferencesSection(
                        preferences = editedProfile.preferences,
                        isEditing = isEditing,
                        onPreferencesChange = { newPreferences ->
                            editedProfile = editedProfile.copy(preferences = newPreferences)
                        },
                    )

                    if (isEditing) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Button(
                                onClick = {
                                    isEditing = false
                                    profileState?.let { editedProfile = it }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading,
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            Button(
                                onClick = {
                                    viewModel.saveProfile(editedProfile)
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading,
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                } else {
                                    Text(stringResource(R.string.save_changes))
                                }
                            }
                        }
                    }
                }
            }

            if (showImagePickerDialog) {
                ImagePickerDialog(
                    onDismiss = { showImagePickerDialog = false },
                    onGallerySelected = { imagePicker.openGallery() },
                    onCameraSelected = { imagePicker.openCamera() },
                )
            }
        }
    }
}

@Composable
private fun ProfileHeaderSection(
    profile: UserProfile,
    isEditing: Boolean,
    onProfileImageChange: (String) -> Unit,
    imageManager: ImageManager,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                AsyncImage(
                    model =
                        if (profile.photoPath.isNotEmpty()) {
                            imageManager.getImageUri(profile.photoPath)
                        } else {
                            R.drawable.ic_launcher_foreground
                        },
                    contentDescription = stringResource(R.string.profile_picture),
                    modifier =
                        Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop,
                )
                if (isEditing) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.change_photo),
                        modifier =
                            Modifier
                                .size(36.dp)
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(8.dp)
                                .clickable {
                                    onProfileImageChange("open_dialog")
                                },
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = profile.displayName.ifEmpty { stringResource(R.string.anonymous_user) },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text =
                    stringResource(
                        R.string.member_since,
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(profile.joinDate)),
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun PersonalInfoSection(
    profile: UserProfile,
    isEditing: Boolean,
    onProfileChange: (UserProfile) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.personal_information),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            if (isEditing) {
                OutlinedTextField(
                    value = profile.displayName,
                    onValueChange = { newName ->
                        onProfileChange(profile.copy(displayName = newName))
                    },
                    label = { Text(stringResource(R.string.display_name)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = profile.bio,
                    onValueChange = { newBio ->
                        onProfileChange(profile.copy(bio = newBio))
                    },
                    label = { Text(stringResource(R.string.bio)) },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = profile.phoneNumber,
                    onValueChange = { newPhone ->
                        onProfileChange(profile.copy(phoneNumber = newPhone))
                    },
                    label = { Text(stringResource(R.string.phone_number)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = profile.location,
                    onValueChange = { newLocation ->
                        onProfileChange(profile.copy(location = newLocation))
                    },
                    label = { Text(stringResource(R.string.location)) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                InfoRow(
                    label = stringResource(R.string.display_name),
                    value = profile.displayName.ifEmpty { stringResource(R.string.not_set) },
                )
                InfoRow(
                    label = stringResource(R.string.bio),
                    value = profile.bio.ifEmpty { stringResource(R.string.no_bio) },
                )
                InfoRow(
                    label = stringResource(R.string.phone),
                    value = profile.phoneNumber.ifEmpty { stringResource(R.string.not_set) },
                )
                InfoRow(
                    label = stringResource(R.string.location),
                    value = profile.location.ifEmpty { stringResource(R.string.not_set) },
                )
            }
        }
    }
}

@Composable
private fun StatisticsSection(profile: UserProfile) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.statistics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(
                    count = profile.statistics.newsRead.toString(),
                    label = stringResource(R.string.news_read),
                    modifier = Modifier.weight(1f),
                )
                StatItem(
                    count = profile.statistics.messagesSent.toString(),
                    label = stringResource(R.string.messages),
                    modifier = Modifier.weight(1f),
                )
                StatItem(
                    count = profile.statistics.calculationsMade.toString(),
                    label = stringResource(R.string.calculations),
                    modifier = Modifier.weight(1f),
                )
                StatItem(
                    count = profile.statistics.sessionsCount.toString(),
                    label = stringResource(R.string.sessions),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            SessionTimeInfo(statistics = profile.statistics)
        }
    }
}

@Composable
private fun PreferencesSection(
    preferences: UserPreferences,
    isEditing: Boolean,
    onPreferencesChange: (UserPreferences) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.preferences),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            PreferenceRow(
                title = stringResource(R.string.dark_mode),
                subtitle = stringResource(R.string.enable_dark_theme),
                checked = preferences.theme == AppTheme.DARK,
                enabled = isEditing,
                onCheckedChange = { checked ->
                    onPreferencesChange(
                        preferences.copy(
                            theme = if (checked) AppTheme.DARK else AppTheme.LIGHT,
                        ),
                    )
                },
            )

            PreferenceRow(
                title = stringResource(R.string.notifications),
                subtitle = stringResource(R.string.receive_push_notifications),
                checked = preferences.notificationsEnabled,
                enabled = isEditing,
                onCheckedChange = { checked ->
                    onPreferencesChange(preferences.copy(notificationsEnabled = checked))
                },
            )

            PreferenceRow(
                title = stringResource(R.string.biometric_auth),
                subtitle = stringResource(R.string.use_fingerprint_or_face_id),
                checked = preferences.biometricAuthEnabled,
                enabled = isEditing,
                onCheckedChange = { checked ->
                    onPreferencesChange(preferences.copy(biometricAuthEnabled = checked))
                },
            )

            PreferenceRow(
                title = stringResource(R.string.auto_save),
                subtitle = stringResource(R.string.automatically_save_changes),
                checked = preferences.autoSave,
                enabled = isEditing,
                onCheckedChange = { checked ->
                    onPreferencesChange(preferences.copy(autoSave = checked))
                },
            )
        }
    }
}

@Composable
private fun EmptyProfileState(onCreateProfile: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = stringResource(R.string.no_profile),
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_profile_found),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.create_profile_to_get_started),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCreateProfile) {
            Text(stringResource(R.string.create_profile))
        }
    }
}

@Composable
private fun StatItem(
    count: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = if (enabled) onCheckedChange else null,
            enabled = enabled,
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SessionTimeInfo(statistics: UserStatistics) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        InfoRow(
            label = stringResource(R.string.total_session_time),
            value = formatSessionTime(statistics.totalSessionTime),
        )
        InfoRow(
            label = stringResource(R.string.last_session),
            value = formatSessionTime(statistics.lastSessionDuration),
        )
        InfoRow(
            label = stringResource(R.string.average_session),
            value = formatSessionTime(statistics.calculateAverageSessionTime()),
        )
    }
}

private fun formatSessionTime(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
