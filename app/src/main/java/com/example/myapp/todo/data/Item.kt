package com.example.myapp.todo.data

import java.time.LocalDate


data class Item(val _id: String? = null,
                val managerName: String = "",
                val index: Int = 0,
                val dateCreated: String = "",
                val autumnTreatment: Boolean = false
    )
