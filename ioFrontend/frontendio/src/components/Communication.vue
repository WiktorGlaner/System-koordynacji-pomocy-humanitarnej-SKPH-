<template>
  <div class="container">
    <div class="chat-container" id="chat-page">
      <div class="users-list">
        <div class="users-list-container">
          <h2>{{ $t('users') }}</h2>
          <ul id="connectedUsers">
            <li
                v-for="user in connectedUsers"
                :key="user.username"
                :class="['user-item', { active: user.username === selectedUserId }]"
                @click="selectUser(user.username)"
            >
              <img src="../assets/user_icon.png" :alt="user.username" />
              <span>{{ user.username }}</span>
              <span
                  v-if="user.unreadMessages > 0"
                  class="nbr-msg"
              >{{ user.unreadMessages }}</span>
            </li>
          </ul>
        </div>
      </div>

      <div class="chat-area">
        <div id="chat-messages">
          <div
              v-for="message in messages"
              :key="message.timestamp"
              :class="['message', message.senderId === username ? 'sender' : 'receiver']"
          >
            <p>{{ message.content }}</p>
          </div>
        </div>

        <form
            v-show="selectedUserId"
            @submit.prevent="sendMessage"
            class="message-input"
        >
          <input
              autocomplete="off"
              type="text"
              v-model="messageInput"
              :placeholder="$t('placeholder')"
          />
          <button type="submit">{{ $t('send') }}</button>
        </form>
      </div>
    </div>
  </div>
</template>


<style scoped>
.container {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  flex-direction: column;
}

.chat-container {
  display: flex;
  max-width: 800px;
  min-width: 800px;
  min-height: 600px;
  max-height: 600px;
  margin: 20px;
  border: 1px solid #ccc;
  background-color: #fff;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.users-list {
  flex: 1;
  border-right: 1px solid #ccc;
  padding: 20px;
  box-sizing: border-box;
  background-color: #3498db;
  color: #fff;
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.users-list-container {
  height: 100%;
  overflow-y: auto;
}

.users-list h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.users-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.user-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  cursor: pointer;
}

.user-item.active {
  background-color: #cdebff;
  color: #4f4f4f;
  padding: 10px;
  border-radius: 5px;
}

.user-item img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-item span {
  font-weight: bold;
}

.separator {
  height: 1px;
  background-color: #ccc;
  margin: 10px 0;
}

.chat-area {
  flex: 3;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}

.message {
  margin-bottom: 5px;
  border-radius: 5px;
}

#chat-messages {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  flex-grow: 1;
}

.message p {
  padding: 12px;
  border-radius: 15px;
  word-wrap: break-word;
}

.user-item span.nbr-msg {
  margin-left: 10px;
  background-color: #f8fa6f;
  color: white;
  padding: 5px;
  width: 20px;
  text-align: center;
  border-radius: 50%;
  height: 20px;
  line-height: 20px;
  display: inline-block;
}

.sender {
  background-color: #3498db;
  color: #fff;
  align-self: flex-end;
}

.receiver {
  background-color: #ecf0f1;
  color: #333;
  align-self: flex-start;
}

.message-input {
  margin-top: auto;
  display: flex;
}

.message-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-right: 10px;
}

.message-input button {
  padding: 10px;
  border: none;
  background-color: #3498db;
  color: #fff;
  border-radius: 5px;
  cursor: pointer;
}

.hidden {
  display: none;
}
</style>

<script>
export default {
  name: "Communication",
  data() {
    return {
      stompClient: null,
      messages: [],
      connectedUsers: [],
      messageInput: "",
      username: "",
      selectedUserId: "",
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push("/login");
      return;
    }
    this.username = this.currentUser.username;
    this.connect();
  },
  beforeRouteLeave(to, from, next) {
    if (this.stompClient) {
      this.stompClient.disconnect(() => console.log("WebSocket disconnected"));
    }
    next();
  },
  methods: {
    connect() {
      const socket = new SockJS("/ws");
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, this.onConnected, this.onError);
    },
    onConnected() {
      this.stompClient.subscribe(
          `/user/${this.username}/queue/messages`,
          this.onMessageReceived
      );
      this.stompClient.subscribe(`/user/public`, this.onMessageReceived);
      this.fetchConnectedUsers();
    },
    async fetchConnectedUsers() {
      try {
        const response = await fetch(
            "http://localhost:8080/api/test/allUsers"
        );
        const users = await response.json();
        this.connectedUsers = users
            .filter((user) => user.username !== this.username)
            .map((user) => ({ ...user, unreadMessages: 0 }));
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    },
    selectUser(username) {
      this.selectedUserId = username;
      this.fetchUserChat();
      const user = this.connectedUsers.find((u) => u.username === username);
      if (user) user.unreadMessages = 0;
    },
    async fetchUserChat() {
      try {
        const response = await fetch(
            `http://localhost:8080/messages/${this.username}/${this.selectedUserId}`
        );
        this.messages = await response.json();
        this.scrollToBottom(); // Przewiń na dół po załadowaniu wiadomości
      } catch (error) {
        console.error("Error fetching chat:", error);
      }
    },
    sendMessage() {
      if (this.messageInput.trim() && this.stompClient) {
        const chatMessage = {
          senderId: this.username,
          recipientId: this.selectedUserId,
          content: this.messageInput.trim(),
          timestamp: new Date().toISOString(),
        };
        this.stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        this.messages.push(chatMessage);
        this.messageInput = "";
        this.scrollToBottom(); // Przewiń na dół po wysłaniu wiadomości
      }
    },
    onMessageReceived(payload) {
      const message = JSON.parse(payload.body);
      if (message.senderId === this.selectedUserId) {
        this.messages.push(message);
        this.scrollToBottom(); // Przewiń na dół po otrzymaniu wiadomości
      } else {
        const user = this.connectedUsers.find((u) => u.username === message.senderId);
        if (user) user.unreadMessages += 1;
      }
    },
    onError(error) {
      console.error("WebSocket error:", error);
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatMessages = document.getElementById('chat-messages');
        if (chatMessages) {
          chatMessages.scrollTop = chatMessages.scrollHeight;
        }
      });
    }
  },
};
</script>
