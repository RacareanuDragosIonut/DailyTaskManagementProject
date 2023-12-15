package com.example.dailytaskmanagement.presentation
import android.content.Intent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dailytaskmanagement.R
import com.example.dailytaskmanagement.navigation.Screens
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
                    val navController = rememberNavController()

                    val username = intent.getStringExtra(USERNAME_KEY)


                    HomeScreen(username, navController)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(username: String?, navController: NavController) {
    var isLogoutDialogVisible = remember { mutableStateOf(false) }

    BackHandler {

        isLogoutDialogVisible.value = true
    }


    if (isLogoutDialogVisible.value) {
        LogoutDialog(onConfirmLogout = {

            navController.navigate(Screens.SignInScreen.route)
        }, onDismiss = {

            isLogoutDialogVisible.value = false
        })
    }

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
                IconButton(
                    onClick = {

                        isLogoutDialogVisible.value = true
                    },
                    modifier = Modifier
                        .absolutePadding(top = 8.dp, right = 16.dp)
                        .size(80.dp)
                        .background(color = Color.Transparent)
                ) {
                    HomeButton(
                        "Logout",
                        painterResource(id = R.drawable.baseline_logout_24),
                        {isLogoutDialogVisible.value = true}
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
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
                                painterResource(id = R.drawable.baseline_folder_shared_24),
                                openTaskCategoryPage(username = username.orEmpty(),taskType = "shared tasks with me")
                            )
                        }
                        item {
                            HomeButton(
                                "WORK",
                                painterResource(id = R.drawable.baseline_home_work_24),
                                openTaskCategoryPage(username = username.orEmpty(),taskType = "work")
                            )
                        }
                        item {
                            HomeButton(
                                "GYM",
                                painterResource(id = R.drawable.baseline_fitness_center_24),
                                openTaskCategoryPage(username = username.orEmpty(),taskType = "gym")
                            )
                        }
                        item {
                            HomeButton(
                                "READING",
                                painterResource(id = R.drawable.baseline_menu_book_24),
                                openTaskCategoryPage(username = username.orEmpty(),taskType = "reading")
                            )
                        }
                        item {
                            HomeButton(
                                "SELF LEARNING",
                                painterResource(id = R.drawable.baseline_self_improvement_24),
                                openTaskCategoryPage(username = username.orEmpty(),taskType = "self learning")
                            )
                        }
                        item {
                            HomeButton("OTHER TASKS", Icons.Default.Person,
                                openTaskCategoryPage(username = username.orEmpty(),taskType = "other tasks"))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeButton(title: String, icon: Any, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (icon is ImageVector) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black,
                )
            }
        } else if (icon is Painter) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = Color.Black,
                )
            }
        }
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}


@Composable
fun LogoutDialog(onConfirmLogout: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Logout") },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmLogout()
                    onDismiss()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun openTaskCategoryPage(username: String, taskType: String): () -> Unit {
    val context = LocalContext.current
    return {
        val intent = Intent(context, TaskCategoryPageActivity::class.java)
        intent.putExtra("taskType", taskType)
        intent.putExtra("username", username)
        context.startActivity(intent)
    }
}

