package com.yuanshan.routeros.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CircularProgressCard(
    progress: Double,
    title: String,
    modifier: Modifier = Modifier
) {

    // Use MaterialTheme colors for consistency with other components
    val progresstextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    val progressColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    val backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    StatusCard(
        title = title,
        value = ""
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator(context).apply {
                        setProgressTextAdapter { currentProgress ->
                            "${currentProgress.toInt()}%"
                        }
                        // Use MaterialTheme colors converted to Android Color
                        setProgressColor(progressColor.toArgb())
                        setDotColor(progressColor.toArgb())
                        setProgressStrokeWidthDp(4)
                        setTextSizeSp(14)
                        textColor = progresstextColor.toArgb()
                        setProgressBackgroundColor(backgroundColor.toArgb())
                    }
                },
                modifier = Modifier.size(70.dp),
                update = { view ->
                    view.setCurrentProgress(progress * 100)
                }
            )
        }
    }
}

