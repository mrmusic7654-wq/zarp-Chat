package com.example.data

import kotlinx.coroutines.flow.Flow

class MessageRepository(private val messageDao: MessageDao) {
    
    fun getMessages(isPetChat: Boolean): Flow<List<MessageEntity>> {
        return messageDao.getMessages(isPetChat)
    }

    suspend fun insertMessage(message: MessageEntity) {
        messageDao.insertMessage(message)
    }

    suspend fun clearHistory() {
        messageDao.clearAll()
    }
}
