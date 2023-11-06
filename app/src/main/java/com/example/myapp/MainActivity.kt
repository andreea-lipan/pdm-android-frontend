package com.example.myapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapp.todo.data.Item
import com.example.myapp.todo.ui.ItemDetail

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d("MainActivity", "onCreate")
            ItemDetail(Item(text = "Learn compose", done = false))
        }
    }
}

@Preview
@Composable
fun PreviewMyApp() {
    ItemDetail(Item(text = "Learn compose", done = false))
}
