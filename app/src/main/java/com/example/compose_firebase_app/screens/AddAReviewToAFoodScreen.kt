package com.example.compose_firebase_app.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose_firebase_app.data.models.Food
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun AddAReviewToAFoodScreen() {

    val coroutineScope = rememberCoroutineScope()


    //Screen Space Configuration
    val config = LocalConfiguration.current
    val context = LocalContext.current


    var shouldDropDown by remember { mutableStateOf<Boolean>(false) }

    var listOfFoodOptions by remember { mutableStateOf<ArrayList<Food>>(ArrayList()) }


    var selectedFood by remember { mutableStateOf<Food?>(null) }


    var reviewText by remember { mutableStateOf<String>("") }



    //A Launch Effect for the screen,
    //When FIRST Composed (on appear), call to firebase for food dropdown options
    LaunchedEffect(Unit) {

        //Call to Firebase Here
        coroutineScope.launch {
            listOfFoodOptions = retrieveAllFoodsFromFireStore()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(text = "Add a Review to a Food",
            color = Color.Black,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 10.dp, top = 50.dp)
                .width(350.dp)
        )



        //MARK: Drop Down Box Area
        Box(
            modifier = Modifier
//                .fillMaxSize()
                .clickable {
                    Log.d("Clickable", "Clicked Review Food Button")
                    shouldDropDown = true
                }
                .padding(bottom = 20.dp)
                .height(20.dp)
                .width(config.screenWidthDp.dp)
                .background(Color.Cyan),

        ) {
            if (selectedFood == null){
                Text(text = "Choose a Food to Review")
            }else{
                Text(text = selectedFood!!.foodName.toString())
            }
            DropdownMenu(expanded = shouldDropDown, onDismissRequest = { shouldDropDown = false },
                modifier = Modifier
                    .background(Color.Cyan)) {

                //Iterate through the retrieved Drop Down Values to display
                listOfFoodOptions.forEachIndexed{ index, foodItem ->

                    //https://foso.github.io/Jetpack-Compose-Playground/material/dropdownmenu/
                    //For each of the indexed food item, place a Drop down item here
                    DropdownMenuItem(
                        text = {
                            Text(text = foodItem.foodName.toString(),color = Color.Black,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.SemiBold,
                                ) },
                        onClick = {
                            selectedFood = foodItem
                            shouldDropDown = false
                        })

                }

            }
        }




        //Once a Valid Food is selected, Make a Text Area Appear To prompt the user to Leave a Review
        if (selectedFood != null) {

            Text(text = "Leave a Review For ${selectedFood!!.foodName.toString()}?", textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 30.dp))



            //Text Area For Review
            TextField(
                value = reviewText,
                onValueChange = { reviewText = it  },
                label = {
                    Text(text = "Thoughts?")
                },
                modifier = Modifier
                    .padding(bottom = 30.dp)
            )


            //Button To Leave Review
            Button(onClick = {
                Log.d("OUT", "OuterRide")

                val addedReview = checkIfCanAddReviewAndAdd(reviewText,
                    selectedFood!!.docID.toString()
                )
                if (!addedReview){
                    Toast.makeText(context, "Error: Enter some text to add a review", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Review Added!", Toast.LENGTH_SHORT).show()
                }


            }) {
                Text("Leave The Review")
                //Bottom Of Button
            }

        }





    }


}


@Preview(showBackground = true)
@Composable
fun PreviewAddReviewAFood(){
    AddAReviewToAFoodScreen()
}



//A dedicated function to handle retrieving a list of all the foods from the collections

suspend fun retrieveAllFoodsFromFireStore(): ArrayList<Food>{

    //Initialize Firebase Firestore
    val fs = FirebaseFirestore.getInstance()

    //Create a variable reference to the Foods Collections
    val foodsCollection = fs.collection("Foods")

    //PlaceHolder to store the iterating through foods
    val foodIteratorPlaceHolder = ArrayList<Food>()

    //Get ALL the documents from the Collection
    //Create a foods Storage to hold the Docs
    val retrievedFoodSnapShot = foodsCollection.get().await()
    try{
        if (!retrievedFoodSnapShot.isEmpty){
            //Return is NOT NULL

            //convert snapshot to docs
            val foodDocs = retrievedFoodSnapShot.documents

            //Iterate Through ALL FOODS
            for (foodDoc in foodDocs) {
                val newFood = foodDoc.toObject<Food>()

                //Append to the collection
                foodIteratorPlaceHolder.add(newFood!!)

            }


        }
    }catch (e:Exception){
        Log.e("Error While Getting All Food Docs For review options", e.message.toString())

    }

    //Return the food Array once all is complete
    return foodIteratorPlaceHolder


}



//Function to check if the Review can be added, or to start calling the
//Add Review Function
fun checkIfCanAddReviewAndAdd(reviewText:String, selectedFoodID:String): Boolean {
    //Get Context

    //Check if the review text is NOT EMPTY,
    //if the review text is NOT EMPTY, then call and and add the text to firebase
    //if it IS nothing, present a toast popup
    if (reviewText.isBlank()){
        //checks if the entry is blank
        //and presents the Toast error message

        return false
    }else{
        //else subtmit and add to firebase and return true

        addReviewToMeal(reviewText, selectedFoodID)



        return true
    }
}

//https://firebase.google.com/docs/firestore/manage-data/add-data

fun addReviewToMeal(reviewText:String, foodToReviewID:String){

    //Initialize Firebase Firestore
    val fs = FirebaseFirestore.getInstance()

    //Create a variable reference to the Foods Collections
    val foodsCollection = fs.collection("Foods")


    //Create a doc ref to the given ID
    val toReviewFoodRef = foodsCollection.document(foodToReviewID)

    //with the food ref, create a reference to the collections on the food `Reviews`
    val thisFoodsReviewsCollection = toReviewFoodRef.collection("Reviews")

    //Add the Review to the collection

    val reviewData = hashMapOf(
        "reviewText" to reviewText
    )

    //Add the Food Review(for this demonstration, no listener is needed for success
    thisFoodsReviewsCollection.add(reviewData)


}