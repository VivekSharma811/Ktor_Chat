package com.hypheno.room

import com.hypheno.data.MessageDataSource
import com.hypheno.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if (members.containsKey(username)) {
            throw MemberAlreadyExistException()
        }

        members[username] = Member(username, sessionId, socket)
    }

    suspend fun sendMessage(sender: String, message: String) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                userName = sender,
                timestamp = System.currentTimeMillis()
            )
            messageDataSource.insertMessage(messageEntity)

            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(userName: String) {
        members[userName]?.socket?.close()

        if (members.containsKey(userName)) {
            members.remove(userName)
        }
    }
}
