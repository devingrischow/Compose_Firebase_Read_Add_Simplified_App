package com.example.compose_firebase_app.data.models

import com.google.firebase.firestore.DocumentId


//This is the shape and structure of the food object inside of FIREBASE
//firebase returns data as a dictionary of data, and will be instantly converted to this.
//This specificaly is a FOOD OBJECT
data class Food(
    @DocumentId val docID:String? = null,
    val foodName:String? = "Loading...",
    val image:String? = "",
    val calories:Double? = 0.0
)
