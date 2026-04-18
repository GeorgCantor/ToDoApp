package com.example.todoapp.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.domain.model.WidgetIntentData
import com.example.todoapp.domain.model.WidgetIntentType
import com.example.todoapp.presentation.navigation.MainNavigation
import com.example.todoapp.presentation.theme.YourAppTheme
import com.example.todoapp.presentation.viewmodel.ThemeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val themeViewModel: ThemeViewModel by viewModel()
    private var widgetIntentData: WidgetIntentData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        widgetIntentData = processWidgetIntent()

        setContent {
            val themeColorInt by themeViewModel.themeColor.collectAsStateWithLifecycle()
            val primaryColor = Color(themeColorInt)

            YourAppTheme(primaryColor = primaryColor) {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars),
                ) {
                    val intentData = remember { widgetIntentData }

                    MainNavigation(
                        widgetIntentData = intentData,
                        onIntentProcessed = { widgetIntentData = null },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        widgetIntentData = processWidgetIntent()
    }

    private fun processWidgetIntent(): WidgetIntentData? =
        when {
            intent?.action == "NEW_MESSAGE" -> {
                WidgetIntentData(
                    type = WidgetIntentType.NEW_MESSAGE,
                    targetScreen = "chat",
                )
            }
            intent?.hasExtra("open_chat") == true -> {
                WidgetIntentData(
                    type = WidgetIntentType.OPEN_CHAT,
                    targetScreen = "chat",
                )
            }
            else -> null
        }
}
