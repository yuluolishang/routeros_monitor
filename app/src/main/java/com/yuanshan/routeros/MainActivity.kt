package com.yuanshan.routeros

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yuanshan.routeros.view.ConfigScreen
import com.yuanshan.routeros.view.RouterDashboard
import com.yuanshan.routeros.view.model.RouterViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: RouterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏状态栏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color.Black,
                    surface = Color.Black,
                    surfaceVariant = Color.Black,
                ),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        RouterApp(viewModel)
                    }
                }
            )
        }
    }
}

@Composable
fun RouterApp(viewModel: RouterViewModel) {
    var showConfig by remember { mutableStateOf(false) }
    var showSettingsButton by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Add background image
        Image(
            painter = painterResource(id = R.drawable.backgroud),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f  // Adjust opacity to ensure content remains readable
        )

        if (showConfig) {
            ConfigScreen(
                onConfigSaved = {
                    showConfig = false
                    viewModel.reconnect()
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                showSettingsButton = true
                            }
                        )
                    }
            ) {
                RouterDashboard(viewModel)

                // 隐藏设置按钮
                LaunchedEffect(showSettingsButton) {
                    if (showSettingsButton) {
                        delay(10000) // 10 秒
                        showSettingsButton = false
                    }
                }

                val textColor = Color(0xFF4CAF50)  // Material Design Green 500
                AnimatedVisibility(
                    visible = showSettingsButton,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { showConfig = true },
                        containerColor = Color(0x404CAF50),  // 添加 25% 透明度
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}




