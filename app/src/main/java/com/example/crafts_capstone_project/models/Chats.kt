package com.example.crafts_capstone_project.models

class Chats(
    private var sender: String = "",
    private var message: String = "",
    private var receiver: String = "",
    private var isseen: Boolean = false,
    private var url: String = "",
    private var messageId: String = ""
) {

    fun getSender(): String {
        return sender
    }

    fun getMessage(): String {
        return message
    }

    fun getReceiver(): String {
        return receiver
    }

    fun isIsSeen(): Boolean {
        return isseen
    }

    fun getUrl(): String {
        return url
    }

    fun getMessageId(): String {
        return messageId
    }

    fun setSender(sender: String) {
        this.sender = sender
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setReceiver(receiver: String) {
        this.receiver = receiver
    }

    fun isSeen(isseen: Boolean) {
        this.isseen = isseen
    }

    fun setUrl(url: String) {
        this.url = url
    }

    fun setMessageId(messageId: String) {
        this.messageId = messageId
    }
}
