<template>
  <div class="container">
    <header class="jumbotron">
      <h3>
       Chat Room
      </h3>
    </header>
    <div id="chat-page">
      <div class="chat-container">
        <ul id="messageArea" class="message-list">
          <li v-for="message in messages" :key="message.id" class="message-item">
            <i :style="{ 'background-color': getAvatarColor(message.sender) }">{{ message.sender[0] }}</i>
            <span>{{ message.sender }}</span>
            <p>{{ message.content }}</p>
          </li>
        </ul>
        <form id="messageForm" @submit.prevent="sendMessage" class="message-form">
          <div class="input-group">
            <input type="text" v-model="messageInput" placeholder="Type a message..." autocomplete="off" class="form-control"/>
            <button type="submit" class="send-btn">Send</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Communication",
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  data() {
    return {
      stompClient: null,
      messages: [],
      messageInput: "",
      username: "",
      colors: [
        "#2196F3", "#32c787", "#00BCD4", "#ff5652",
        "#ffc107", "#ff85af", "#FF9800", "#39bbb0"
      ],
    };
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
      return;
    }
    this.username = this.currentUser.username;
    this.connect();
  },
  beforeRouteLeave(to, from, next) {
      this.stompClient.disconnect(() => {
        console.log("WebSocket disconnected on leave");
      });
    next();
  },
  methods: {
    connect() {
      console.log("Attempting to connect to WebSocket...");
      var socket = new SockJS('/ws');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, this.onConnected, this.onError);
    },
    onConnected() {
      // Subscribe to the Public Topic
      this.stompClient.subscribe('/topic/public', this.onMessageReceived);

      // Tell your username to the server
      this.stompClient.send("/app/addUser",
          {},
          JSON.stringify({ sender: this.username, type: 'JOIN' })
      );

      document.getElementById('connectingElement').classList.add('hidden');
    },
    onError(error) {
      console.error("WebSocket connection error:", error);
      this.$router.push('/error'); // Przekierowanie na stronę błędu
    },
    sendMessage() {
      this.messageInput = this.messageInput.trim();
      if (this.messageInput && this.stompClient) {
        const chatMessage = {
          sender: this.username,
          content: this.messageInput,
          type: 'CHAT'
        };
        this.stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
        this.messageInput = '';
      }
    },
    onMessageReceived(payload) {
      var message = JSON.parse(payload.body);
      var messageElement = document.createElement('li');

      if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
      } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
      } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = this.getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
      }

      var textElement = document.createElement('p');
      var messageText = document.createTextNode(message.content);
      textElement.appendChild(messageText);

      messageElement.appendChild(textElement);

      document.getElementById('messageArea').appendChild(messageElement);
      document.getElementById('messageArea').scrollTop = document.getElementById('messageArea').scrollHeight;
    },
    getAvatarColor(messageSender) {
      var hash = 0;
      for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
      }
      var index = Math.abs(hash % this.colors.length);
      return this.colors[index];
    }
  }
}
</script>
