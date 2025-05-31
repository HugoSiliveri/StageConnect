package com.project.stageconnect.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.project.stageconnect.model.Chat
import com.project.stageconnect.model.Message

/**
 * Repository pour les conversations.
 *
 * @property db La base de données Firestore.
 *
 * @return Le repository pour les conversations.
 */
class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Charge toutes les conversations d'un utilisateur.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param onChatsLoaded Une fonction qui est appelée lorsque les conversations sont chargées.
     *
     * @return Les conversations chargées.
     */
    fun loadChats(userId: String, onChatsLoaded: (List<Chat>) -> Unit) {
        db.collection("conversations").whereArrayContains("userIds", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { it.toObject(Chat::class.java) }
                    onChatsLoaded(chats)
                }
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
    fun sendMessageWithChatId(chatId: String, senderId: String, content: String) {
        val message = Message(senderId = senderId, content = content)
        val conversationRef = db.collection("conversations").document(chatId)

        db.runBatch { batch ->
            val messageRef = conversationRef.collection("messages").document()
            batch.set(messageRef, message)
            batch.update(conversationRef, mapOf(
                "lastMessage" to content,
                "timestamp" to message.timestamp
            ))
        }
    }

    /**
     * Écoute les messages d'une conversation.
     *
     * @param conversationId L'identifiant de la conversation.
     * @param onMessagesReceived Une fonction qui est appelée lorsque les messages sont reçus.
     *
     * @return Une liste de messages.
     */
    fun listenForMessages(conversationId: String, onMessagesReceived: (List<Message>) -> Unit) {
        db.collection("conversations").document(conversationId).collection("messages").orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                    onMessagesReceived(messages)
                }
            }
    }

    /**
     * Crée ou récupère une conversation entre deux utilisateurs.
     *
     * @param user1Id L'identifiant de l'utilisateur 1.
     * @param user2Id L'identifiant de l'utilisateur 2.
     * @param onResult Une fonction qui est appelée lorsque la conversation est créée ou récupérée.
     *
     * @return La conversation créée ou récupérée.
     */
    fun getOrCreateChat(user1Id: String, user2Id: String, onResult: (Chat?) -> Unit) {
        db.collection("conversations").whereArrayContains("userIds", user1Id).get()
            .addOnSuccessListener { snapshot ->
                val existing = snapshot.documents.firstOrNull { doc ->
                    val userIds = doc.get("userIds") as? List<*> ?: emptyList<Any>()
                    userIds.contains(user2Id) && userIds.size == 2
                }

                if (existing != null) {
                    val chat = existing.toObject(Chat::class.java)
                    chat?.id = existing.id
                    onResult(chat)
                } else {
                    val newConversation = Chat(userIds = listOf(user1Id, user2Id))
                    db.collection("conversations").add(newConversation)
                        .addOnSuccessListener { docRef ->
                            val createdChat = newConversation.copy(id = docRef.id)
                            onResult(createdChat)
                        }
                        .addOnFailureListener {
                            onResult(null)
                        }
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}
