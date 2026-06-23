package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ui.MessageRole

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val role: MessageRole,
    val content: String,
    val isPetChat: Boolean,
    val timestamp: Long
)
