package com.example.dailytaskmanagement.presentation.login_screen

import android.content.Intent



import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.dailytaskmanagement.navigation.Screens
import com.example.dailytaskmanagement.presentation.HomeActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(viewModel: SignInViewModel = hiltViewModel(),navController: NavController){
    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.signInState.collectAsState(initial = null)
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(start = 30.dp, end = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = "Enter your credentials to sign in",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.Gray
        )

        TextField(value = email, onValueChange = {
            email = it

        }, modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFADD8E6),
                cursorColor = Color.Black,
                disabledLabelColor = Color(0xFFADD8E6),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = {
                Text(text="Email")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = password, onValueChange = {
            password = it

        }, modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFADD8E6),
                cursorColor = Color.Black,
                disabledLabelColor = Color(0xFFADD8E6),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = {
                Text(text="Password")
            },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick={
            scope.launch{
                viewModel.loginUser(email,password)
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 30.dp, end = 30.dp),
            colors= ButtonDefaults.buttonColors(containerColor = Color.Black,
                contentColor = Color.White),
            shape = RoundedCornerShape(15.dp)
        ){
            Text(text="Sign In", color = Color.White, modifier= Modifier.padding(7.dp))
        }
        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            if(state.value?.isLoading ==true){
                CircularProgressIndicator()
            }
        }
        Text(text="Do you not have An Account? Sign Up",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.clickable {
                navController.navigate(Screens.SignUpScreen.route)
            }
        )
        LaunchedEffect(key1 = state.value?.isSuccess){
            scope.launch{
                if(state.value?.isSuccess?.isNotEmpty()==true){
                    val success = state.value?.isSuccess
                    Toast.makeText(context, "${success}", Toast.LENGTH_LONG).show()


                    val intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("username_key", email)
                    context.startActivity(intent)
                }


            }
        }
        LaunchedEffect(key1 = state.value?.isError){
            scope.launch{
                if(state.value?.isError?.isNotEmpty()==true){
                    val error = state.value?.isError
                    Toast.makeText(context, "${error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


