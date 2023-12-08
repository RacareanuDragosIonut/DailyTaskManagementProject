package com.example.dailytaskmanagement.presentation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytaskmanagement.R
import com.example.dailytaskmanagement.ui.theme.DailyTaskManagementTheme

class HomeActivity : ComponentActivity() {
    val USERNAME_KEY = "username_key"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTaskManagementTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val username = intent.getStringExtra(USERNAME_KEY)


                    HomeScreen(username)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(username: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            color = Color.LightGray,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Welcome, $username!",
                    style = LocalTextStyle.current.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Daily Task Management",
                    style = LocalTextStyle.current.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
                    color = Color.Black,
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    content = {
                        item {
                            HomeButton(
                                "SHARED TASKS WITH ME",
                                painterResource(id = R.drawable.baseline_folder_shared_24)
                            )
                        }
                        item {
                            HomeButton(
                                "WORK",
                                painterResource(id = R.drawable.baseline_home_work_24)
                            )
                        }
                        item {
                            HomeButton(
                                "GYM",
                                painterResource(id = R.drawable.baseline_fitness_center_24)
                            )
                        }
                        item {
                            HomeButton(
                                "READING",
                                painterResource(id = R.drawable.baseline_menu_book_24)
                            )
                        }
                        item {
                            HomeButton(
                                "SELF LEARNING",
                                painterResource(id = R.drawable.baseline_self_improvement_24)
                            )
                        }
                        item {
                            HomeButton("OTHER TASKS", Icons.Default.Person)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeButton(title: String, icon: Any) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (icon is ImageVector) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            )
        } else if (icon is Painter) {
            Icon(
                painter = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            )
        }
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DailyTaskManagementTheme {
        HomeScreen(username = "")
    }
}
