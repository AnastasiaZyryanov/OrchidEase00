package com.example.orchidease00

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.orchidease00.ui.theme.OrchidEase00Theme

class MainActivity : ComponentActivity() {
  /*  override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.setFormat(PixelFormat.RGBA_8888)
    }

   */
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags( //errore di graphic rendering
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        enableEdgeToEdge()
        setContent {
            OrchidEase00Theme {
                OrchidApp()
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

