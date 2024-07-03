package com.example.im

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Message(val text: String, val isSentByMe: Boolean)

@Composable
fun MessageScreen() {
    ImUI()
}

@Composable
fun ImUI() {
    var messages by remember { mutableStateOf(listOf(
        Message("Hello!", true),
        Message("Hi, how are you?", false),
        Message("I'm good, thanks!", true)
    )) }
    var currentMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        MessagesList(messages, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(8.dp))
        MessageInput(
            currentMessage = currentMessage,
            onMessageChange = { currentMessage = it },
            onSendClick = {
                if (currentMessage.isNotEmpty()) {
                    messages = messages + Message(currentMessage, true)
                    currentMessage = ""
                }
            }
        )
    }
}

@Composable
fun MessagesList(
    messages: List<Message>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(messages) { message ->
            MessageItem(message)
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text = message.text,
            style = TextStyle(color = Color.White, fontSize = 16.sp),
            modifier = Modifier
                .background(if (message.isSentByMe) Color.Blue else Color.Gray)
                .padding(8.dp)
        )
    }
}

@Composable
fun MessageInput(
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(modifier = Modifier
        .padding(8.dp)
        .imePadding()) {
        BasicTextField(
            value = currentMessage,
            onValueChange = onMessageChange,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        )
        Button(onClick = onSendClick, modifier = Modifier.align(Alignment.CenterVertically)) {
            Text("Send")
        }
    }
}