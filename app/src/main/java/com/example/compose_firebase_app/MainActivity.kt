package com.example.compose_firebase_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose_firebase_app.screens.ReviewARandomFoodScreen
import com.example.compose_firebase_app.ui.theme.Compose_Firebase_AppTheme
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            //Declare a Co Routine Scope for the App

            // Use rememberCoroutineScope to launch the function in a coroutine
            val coroutineScope = rememberCoroutineScope()

            Compose_Firebase_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FirebaseDataPreviewScreen(coroutineScope,innerPadding)
                }
            }
        }
    }
}



@Composable
fun FirebaseDataPreviewScreen(coroutineScope: CoroutineScope,innerPadding: PaddingValues) {



    //Boolean value to control the user pressing on the review a random doc button
    var isReviewPressed by remember { mutableStateOf<Boolean>(true) }





    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Two Buttons, 1 for review (default selected), another for add review
        Text(
            text = "Choose what to do",
            color = Color.Black,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 50.dp)
                .width(350.dp)
        )
        //Row to contain the 2 Buttons
        Row(
            modifier = Modifier
                .padding(bottom = 50.dp)
        ) {
            Spacer(modifier = Modifier.padding())

            Button(
                onClick = {
                    Log.d("Pressed Menu Option", "Look At Food")

                    //set is review pressed to true
                    isReviewPressed = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = changeButtonColor(true, isReviewPressed)),
                modifier = Modifier
                    .width(140.dp)
                    .height(50.dp)
            ) {
                Text(
                    "Look at a Food",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                )
                //Bottom Of Button
            }

            Spacer(modifier = Modifier.padding(20.dp))

            Button(
                onClick = {

                    Log.d("Pressed Menu Option", "Review a Food")

                    //set is review pressed to false, (which makees review a screen appear)
                    isReviewPressed = false

                },
                colors = ButtonDefaults.buttonColors(containerColor = changeButtonColor(false, isReviewPressed)),
                modifier = Modifier
                    .width(140.dp)
                    .height(50.dp)
            ) {
                Text(
                    "Review a Food",
                    textAlign = TextAlign.Center
                )
                //Bottom Of Button
            }

            Spacer(modifier = Modifier.padding())


        }


        //This screen houses the two choices, review random recipe or add a review to a food

        //This Area Houses the If Chain and what to do next

        if (isReviewPressed) {
            ReviewARandomFoodScreen(coroutineScope)
        } else {
            Text(
                text = "Monkee",
                color = Color.Blue
            )
        }


    }


}




@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    Compose_Firebase_AppTheme {
        FirebaseDataPreviewScreen(rememberCoroutineScope(),PaddingValues.Absolute())
    }
}


//Function to change the top Buttons Color
//whichButton takes a `Boolean` Value,
//True for 'Look at a Food'
//False for 'Review a Food'
fun changeButtonColor(whichButton:Boolean, selectValue:Boolean):Color{

    return if (whichButton && selectValue || !whichButton && !selectValue){
        //Button is: Look at a Food,
        //Return the purple color
        Color.Unspecified
    }else{
        Color.Gray
    }

}