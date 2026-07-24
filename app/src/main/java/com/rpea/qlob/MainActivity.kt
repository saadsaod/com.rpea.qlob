package com.rpea.qlob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rpea.qlob.ui.screens.HomeScreen
import com.rpea.qlob.ui.screens.MainViewModel
import com.rpea.qlob.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeScreen(viewModel = viewModel)
        }
      }
    }
  }
}
