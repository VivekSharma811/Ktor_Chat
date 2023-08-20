package com.hypheno.plugins

import com.hypheno.room.RoomController
import com.hypheno.routes.chatSocket
import com.hypheno.routes.getAllMessages
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    routing {
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
