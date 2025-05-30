package com.example.orchidease00

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.orchidease00.ui.createNotificationChannel
import com.example.orchidease00.ui.theme.OrchidEase00Theme
import android.Manifest
import android.widget.Toast



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags( //risolve problemi con grafica
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

         super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) { //logs per cadute
            Thread.setDefaultUncaughtExceptionHandler { _, e ->
                e.printStackTrace()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }

        enableEdgeToEdge()
        setContent {
            OrchidEase00Theme {
                OrchidApp()
            }
        }
    }
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Manca il consenso per le notifiche", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrchidAppPreview() {
    OrchidEase00Theme {
        OrchidApp()
    }
}



