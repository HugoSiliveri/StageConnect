package com.project.stageconnect.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.stageconnect.model.Chat
import com.project.stageconnect.model.Message
import com.project.stageconnect.model.repository.ChatRepository

/**
 * ViewModel pour les conversations.
 *
 * @property chatRepository Le repository pour les conversations.
 */
class ChatViewModel : ViewModel() {

    private val chatRepository = ChatRepository()

    /**
     * Charge une conversation entre deux utilisateurs.
     *
     * @param onChatLoaded Une fonction qui est appelée lorsque la conversation est chargée.
     * @param userId1 L'identifiant de l'utilisateur 1.
     * @param userId2 L'identifiant de l'utilisateur 2.
     *
     * @return La conversation chargée.
     */
    fun loadChat(onChatLoaded: (Chat?, List<Message>) -> Unit, userId1: String, userId2: String) {
        chatRepository.getOrCreateChat(userId1, userId2) { chat ->
            if (chat != null) {
                chatRepository.listenForMessages(chat.id) { messages ->
                    onChatLoaded(chat, messages)
                }
            } else {
                onChatLoaded(null, emptyList())
            }
        }
    }

    /**
     * Charge toutes les conversations d'un utilisateur.
     *
     * @param onChatsLoaded Une fonction qui est appelée lorsque les conversations sont chargées.
     * @param userId L'identifiant de l'utilisateur.
     *
     * @return Les conversations chargées.
     */
    fun loadChats(onChatsLoaded: (List<Chat>) -> Unit, userId: String) {
        chatRepository.loadChats(userId) { chats ->
            onChatsLoaded(chats)
        }
    }

    /**
     * Envoie un message dans une conversation.
     *
     * @param chatId L'identifiant de la conversation.
     * @param senderId L'identifiant de l'utilisateur qui envoie le message.
     * @param content Le contenu du message.
     *
     * @return Le message envoyé.
     */
    fun sendMessage(chatId: String, senderId: String, content: String) {
        chatRepository.sendMessageWithChatId(chatId, senderId, content)
    }
}
