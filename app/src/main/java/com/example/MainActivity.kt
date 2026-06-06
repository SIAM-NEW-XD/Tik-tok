package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.data.api.TikWmService
import com.example.data.local.AppDatabase
import com.example.data.repository.VideoRepository
import com.example.ui.DashboardScreen
import com.example.ui.VideoViewModel
import com.example.ui.VideoViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize local persistent database and networking contracts
        val database = AppDatabase.getDatabase(this)
        val apiService = TikWmService.create()
        val repository = VideoRepository(apiService, database.downloadHistoryDao())

        // 2. Load ViewModels through standard builders
        val viewModel: VideoViewModel by viewModels {
            VideoViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DashboardScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

