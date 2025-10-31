<template>
  <div class="app-layout">
    <div class="sidebar">
      <div class="logo-section">
        <img src="@/assets/logo.png" alt="客服助手" width="160" height="160" />
        <span class="logo-text">物流客服助手</span>
      </div>
      <el-button class="new-chat-button" @click="newChat">
        <i class="fa-solid fa-plus"></i>
        &nbsp;新会话
      </el-button>
    </div>
    <div class="main-content">
      <div class="chat-container">
        <div class="message-list" ref="messaggListRef">
          <div
              v-for="(message, index) in messages"
              :key="index"
              :class="
              message.isUser ? 'message user-message' : 'message bot-message'
            "
          >
            <!-- 会话图标 -->
            <i
                :class="
                message.isUser
                  ? 'fa-solid fa-user message-icon'
                  : 'fa-solid fa-robot message-icon'
              "
            ></i>
            <!-- 会话内容 -->
            <span>
              <span v-html="message.content"></span>
              <!-- loading -->
              <span
                  class="loading-dots"
                  v-if="message.isThinking || message.isTyping"
              >
                <span class="dot"></span>
                <span class="dot"></span>
              </span>
            </span>
          </div>
        </div>
        <div class="input-container">
          <el-input
              v-model="inputMessage"
              placeholder="请输入消息"
              @keyup.enter="sendMessage"
          ></el-input>
          <el-button @click="sendMessage" :disabled="isSending" type="primary"
          >发送</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import axios from 'axios'
import { v4 as uuidv4 } from 'uuid'
import { marked } from 'marked'

// 可选：代码高亮支持（如需，请取消注释以下三行 + 安装 highlight.js）
// import hljs from 'highlight.js'
// import 'highlight.js/styles/github.css'
// marked.setOptions({ highlight: (code, lang) => { const language = hljs.getLanguage(lang) ? lang : 'plaintext'; return hljs.highlight(code, { language }).value }, langPrefix: 'hljs language-' })

const messaggListRef = ref()
const isSending = ref(false)
const uuid = ref()
const inputMessage = ref('')
const messages = ref([])

onMounted(() => {
  initUUID()
  watch(messages, () => scrollToBottom(), { deep: true })
  hello()
})

const scrollToBottom = () => {
  if (messaggListRef.value) {
    messaggListRef.value.scrollTop = messaggListRef.value.scrollHeight
  }
}

const hello = () => {
  sendRequest('你好')
}

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    sendRequest(inputMessage.value.trim())
    inputMessage.value = ''
  }
}

const sendRequest = (message) => {
  isSending.value = true
  const userMsg = {
    isUser: true,
    content: message,
    isTyping: false,
    isThinking: false,
  }
  if (messages.value.length > 0) {
    messages.value.push(userMsg)
  }

  const botMsg = {
    isUser: false,
    content: '',
    isTyping: true,
    isThinking: false,
  }
  messages.value.push(botMsg)
  const lastMsg = messages.value[messages.value.length - 1]
  scrollToBottom()

  axios
      .post(
          '/api/ai/chat',
          { memoryId: uuid.value, message },
          {
            responseType: 'text', // 必须是 'text'，stream 模式不支持 'stream' 字符串
            onDownloadProgress: (e) => {
              const fullText = e.event.target.responseText

              // 使用 marked 将完整 Markdown 转为 HTML
              const htmlContent = marked.parse(fullText, {
                breaks: true, // \n 转 <br>
              })

              lastMsg.content = htmlContent // 整体替换，避免增量解析错误
              scrollToBottom()
            },
          }
      )
      .then(() => {
        lastMsg.isTyping = false
        isSending.value = false
      })
      .catch((error) => {
        console.error('流式错误:', error)
        lastMsg.content = '请求失败，请重试'
        lastMsg.isTyping = false
        isSending.value = false
      })
}

const initUUID = () => {
  let storedUUID = localStorage.getItem('user_uuid')
  if (!storedUUID) {
    storedUUID = uuidToNumber(uuidv4())
    localStorage.setItem('user_uuid', storedUUID)
  }
  uuid.value = storedUUID
}

const uuidToNumber = (uuid) => {
  let number = 0
  for (let i = 0; i < uuid.length && i < 6; i++) {
    const hexValue = uuid[i]
    number = number * 16 + (parseInt(hexValue, 16) || 0)
  }
  return number % 1000000
}

const newChat = () => {
  localStorage.removeItem('user_uuid')
  window.location.reload()
}
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 200px;
  background-color: #f4f4f9;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
}

.new-chat-button {
  width: 100%;
  margin-top: 20px;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #fff;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.message {
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 4px;
  display: flex;
}

.user-message {
  max-width: 70%;
  background-color: #e1f5fe;
  align-self: flex-end;
  flex-direction: row-reverse;
}

.bot-message {
  max-width: 100%;
  background-color: #f1f8e9;
  align-self: flex-start;
}

.message-icon {
  margin: 0 10px;
  font-size: 1.2em;
}

.loading-dots {
  padding-left: 5px;
}

.dot {
  display: inline-block;
  margin-left: 5px;
  width: 8px;
  height: 8px;
  background-color: #000000;
  border-radius: 50%;
  animation: pulse 1.2s infinite ease-in-out both;
}

.dot:nth-child(2) {
  animation-delay: -0.6s;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(0.6);
    opacity: 0.4;
  }

  50% {
    transform: scale(1);
    opacity: 1;
  }
}

.input-container {
  display: flex;
}

.input-container .el-input {
  flex: 1;
  margin-right: 10px;
}

/* Markdown 内容样式美化 */
:deep(.bot-message h1),
:deep(.bot-message h2),
:deep(.bot-message h3),
:deep(.bot-message h4),
:deep(.bot-message h5),
:deep(.bot-message h6) {
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: bold;
}

:deep(.bot-message ul),
:deep(.bot-message ol) {
  padding-left: 1.5em;
  margin: 0.5em 0;
}

:deep(.bot-message li) {
  margin: 0.2em 0;
}

:deep(.bot-message p) {
  margin: 0.5em 0;
}

:deep(.bot-message code) {
  background-color: #f0f0f0;
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9em;
}

:deep(.bot-message pre) {
  background-color: #2d2d2d;
  color: #f8f8f2;
  padding: 1em;
  border-radius: 6px;
  overflow-x: auto;
  margin: 0.5em 0;
}

:deep(.bot-message pre code) {
  background: none;
  padding: 0;
  font-size: 0.9em;
}

:deep(.bot-message a) {
  color: #007bff;
  text-decoration: underline;
}

:deep(.bot-message blockquote) {
  border-left: 4px solid #ccc;
  padding-left: 1em;
  margin: 0.5em 0;
  color: #555;
}

/* 响应式 */
@media (max-width: 768px) {
  .main-content {
    padding: 10px 0 10px 0;
  }
  .app-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
  }

  .logo-section {
    flex-direction: row;
    align-items: center;
  }

  .logo-text {
    font-size: 20px;
  }

  .logo-section img {
    width: 40px;
    height: 40px;
  }

  .new-chat-button {
    margin-right: 30px;
    width: auto;
    margin-top: 5px;
  }
}

@media (min-width: 769px) {
  .main-content {
    padding: 0 0 10px 10px;
  }
}
</style>