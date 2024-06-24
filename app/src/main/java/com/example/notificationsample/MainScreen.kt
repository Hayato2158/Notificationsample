package com.example.notificationsample

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            showNotification(context)
        }
    }

    //アプリ起動時に通知を表示
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showNotification(context)
            }
        } else {
            showNotification(context)
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        return@Button
                    }
                }
                showNotification(context)
            }) {
                Text("通知を表示")
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun showNotification(context: Context) {
    val manager = NotificationManagerCompat.from(context)
    val channel = NotificationChannelCompat.Builder(
        "channel_id",
        NotificationManagerCompat.IMPORTANCE_HIGH,
    )
        .setName("バックグラウンドでの実行確認")
        .build()
    manager.createNotificationChannel(channel)
    val notification = NotificationCompat.Builder(context, channel.id)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("EnvMonitor")
        .setContentText("あなたの生活空間をセンシングしています")
        .setOngoing(true)
        .build()
    manager.notify(1, notification)
}
