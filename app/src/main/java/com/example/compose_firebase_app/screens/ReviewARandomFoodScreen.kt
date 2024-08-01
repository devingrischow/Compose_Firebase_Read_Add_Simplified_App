package com.example.compose_firebase_app.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.compose_firebase_app.data.models.Food
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


//Text(text = "Review a random document from firebase",
//color = Color.Black,
//fontSize = MaterialTheme.typography.headlineSmall.fontSize,
//fontWeight = FontWeight.SemiBold,
//textAlign = TextAlign.Center,
//modifier = Modifier
//.padding(bottom = 25.dp)
//.width(350.dp)
//
//)
//
//
//
//
//Button(onClick = { Log.d("OUT", "OuterRide")}) {
//    Text("Review a Random Doc")
//    //Bottom Of Button
//}
//
//
////Underneath the button are present
////3 parts from a food card
////food name
////food image (start using the COIL LIBRARY)
////and the calories
//
////only display once the button is pressed

@Composable
fun ReviewARandomFoodScreen(coroutineScope: CoroutineScope){


    val geoConfig = LocalConfiguration.current

    //State variable for the currently being viewed Foods
    var beingPresentedFood by remember { mutableStateOf<Food>(Food()) }

    //A Launch Effect for the screen,
    //When FIRST Composed (on appear), call to firebase
    LaunchedEffect(Unit) {

        //Call to Firebase Here
        coroutineScope.launch {
            beingPresentedFood = fetchRandomFoodFromFirebase()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){




        Text(text = "Review a Food!",
            color = Color.Black,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 25.dp, top = 100.dp)
                .width(350.dp)

        )


        Button(onClick = {
            Log.d("Pressed Button", "Pressed View Another Food ")
            //Fetch and Review Another Food
            coroutineScope.launch {
                beingPresentedFood = fetchRandomFoodFromFirebase()
            }
                         },
            modifier = Modifier
                .padding(bottom = 50.dp)

        ) {
            Text("Review another Random Doc")
            //Bottom Of Button
        }



        //Underneath the button are present
        //3 parts from a food card
        //food name
        //food image (start using the COIL LIBRARY)
        //and the calories

        //only display once the button is pressed

        //Food Name Area
        Text(text = beingPresentedFood.foodName.toString(),
            color = Color.Black,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.SemiBold,

        )


        //Image Area

        AsyncImage(
            //Load in the Image
            model = beingPresentedFood.image ,
            contentDescription = null,
            modifier = Modifier
                .height((geoConfig.screenHeightDp / 2.5).dp))




        //Calories Area
        Text(text = beingPresentedFood.calories.toString(),
            color = Color.Black,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.SemiBold,

            )





    }

}





//Retrieve a random Food Object From Firebase and return it

suspend fun fetchRandomFoodFromFirebase(): Food {

    //Initialize Firebase Firestore
    val fs = FirebaseFirestore.getInstance()

    //Create a variable reference to the Foods Collections
    val foodsCollection = fs.collection("Foods")


    //Get ALL the documents from the Collection
    //Create a foods Storage to hold the Docs

    var randomFood = Food()
    //go through the foods collections, get all the docs, and for the ones retrieved convert them to a food
    // then convert the doc to a food and add it to `foodDocs`
    val retrievedFoodSnapShot = foodsCollection.get().await()

    //if the document is empty, just return null and log a error
    try {


        if (retrievedFoodSnapShot.isEmpty) {
            null //do nothing
        } else {
            //Return is NOT NULL

            //convert snapshot to docs
            val foodDocs = retrievedFoodSnapShot.documents

            //Select a random doc from the food docs
            val randomFoodDoc = foodDocs.random()

            //convert the random item to a FOOD ITEM

            randomFood = randomFoodDoc.toObject<Food>()!!


        }

    }catch (e:Exception){
        Log.e("Error While Getting All Food Docs For Random Doc Review", e.message.toString())

    }

            // Code here runs on the main thread









    return randomFood
}


@Preview(showBackground = true)
@Composable
fun PreviewFoodReviewScreen(){
    ReviewARandomFoodScreen(rememberCoroutineScope())
}