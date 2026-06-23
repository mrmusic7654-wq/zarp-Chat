package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.MessageEntity
import com.example.data.MessageRepository
import com.example.data.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PetState {
    Idle, Sleeping, Waking, Happy, Laughing, Dizzy, Curious, Worried, Celebrating, Eating, Bored, Thinking
}

enum class Accessory {
    NONE, GLASSES, SCARF, BOTH
}

enum class MessageRole {
    User, Assistant, System
}

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val isPetChat: Boolean = false
)

fun MessageEntity.toChatMessage() = ChatMessage(id, role, content, isPetChat)
fun ChatMessage.toEntity(timestamp: Long) = MessageEntity(id, role, content, isPetChat, timestamp)

class ChatViewModel(private val repository: MessageRepository, private val userPrefs: UserPreferences) : ViewModel() {
    
    val messages: StateFlow<List<ChatMessage>> = repository.getMessages(isPetChat = false)
        .map { list -> list.map { it.toChatMessage() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val petMessages: StateFlow<List<ChatMessage>> = repository.getMessages(isPetChat = true)
        .map { list -> list.map { it.toChatMessage() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    private val _petState = MutableStateFlow(PetState.Idle)
    val petState = _petState.asStateFlow()

    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _accessory = MutableStateFlow(Accessory.valueOf(userPrefs.currentAccessory))
    val accessory = _accessory.asStateFlow()

    private val _points = MutableStateFlow(userPrefs.points)
    val points = _points.asStateFlow()

    private val _unlockedGlasses = MutableStateFlow(userPrefs.unlockedGlasses)
    val unlockedGlasses = _unlockedGlasses.asStateFlow()

    private val _unlockedScarf = MutableStateFlow(userPrefs.unlockedScarf)
    val unlockedScarf = _unlockedScarf.asStateFlow()

    init {
        // Initial setup for empty DB could go here if we wanted to pre-populate
        // But for now we just rely on the UI or simple flows
        
        // Simulating battery drain
        viewModelScope.launch {
            while (true) {
                delay(10000)
                _batteryLevel.value = (_batteryLevel.value - 1).coerceAtLeast(0)
                if (_batteryLevel.value < 15 && _petState.value == PetState.Idle) {
                    _petState.value = PetState.Worried
                }
            }
        }
    }

    fun setAccessory(acc: Accessory) {
        _accessory.value = acc
        userPrefs.currentAccessory = acc.name
    }

    fun addPoints(amount: Int) {
        _points.value += amount
        userPrefs.points = _points.value
    }

    fun unlockGlasses(cost: Int): Boolean {
        if (_points.value >= cost && !_unlockedGlasses.value) {
            _points.value -= cost
            userPrefs.points = _points.value
            _unlockedGlasses.value = true
            userPrefs.unlockedGlasses = true
            return true
        }
        return false
    }

    fun unlockScarf(cost: Int): Boolean {
        if (_points.value >= cost && !_unlockedScarf.value) {
            _points.value -= cost
            userPrefs.points = _points.value
            _unlockedScarf.value = true
            userPrefs.unlockedScarf = true
            return true
        }
        return false
    }

    fun sendMessage(content: String, isPetChat: Boolean = false) {
        if (content.isBlank()) return
        
        val timestamp = System.currentTimeMillis()
        val userMsg = ChatMessage(timestamp.toString(), MessageRole.User, content, isPetChat)
        
        viewModelScope.launch {
            repository.insertMessage(userMsg.toEntity(timestamp))
            addPoints(10) // Give points per message sent
            _petState.value = PetState.Thinking
            _isTyping.value = true

            // TODO: Call Gemini API here
            delay(1500)
            
            val replyBase = if (isPetChat) "Bloop! You said: " else "This is a simulated response to: "
            val botTimestamp = System.currentTimeMillis()
            val botMsg = ChatMessage(botTimestamp.toString(), MessageRole.Assistant, replyBase + content, isPetChat)
            
            repository.insertMessage(botMsg.toEntity(botTimestamp))
            
            _isTyping.value = false
            setPetState(PetState.Celebrating)
            delay(2000)
            if (_petState.value == PetState.Celebrating) {
                setPetState(if (_batteryLevel.value < 15) PetState.Worried else PetState.Idle)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun setPetState(state: PetState) {
        _petState.value = state
    }
}

class ChatViewModelFactory(private val repository: MessageRepository, private val userPrefs: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository, userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

