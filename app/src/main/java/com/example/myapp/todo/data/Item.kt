package com.example.myapp.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "items")
data class Item(@PrimaryKey val _id: String? = null,
                val managerName: String = "",
                val index: Int = 0,
                val dateCreated: String = "",
                val autumnTreatment: Boolean = false
    )
