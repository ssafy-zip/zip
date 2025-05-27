<template>
  <div class="chat-wrapper">
    <!-- header -->
    <div class="chat-header">
      <h2>달건이 봇</h2>
    </div>

    <!-- body -->
    <div class="chat-body">
      <div class="date-badge">{{ today }}</div>
      <div class="messages" ref="messagesContainer">
        <div
          v-for="(msg, idx) in messages"
          :key="idx"
          :class="['message', msg.role, { loading: msg.loading }]"
        >
          <div v-if="msg.role === 'assistant'" class="avatar">
            <img src="@/assets/images/dalgeon.jpg" alt="bot avatar" />
          </div>
          <div class="bubble" v-html="renderMarkdown(msg.content)"></div>
        </div>
      </div>
    </div>

    <!-- input & faq -->
    <div class="chat-bottom">
      <div class="chat-input">
        <input
          v-model="newMessage"
          @keyup.enter="sendMessage"
          placeholder="메시지 입력"
          class="message-input"
          :disabled="isLoading"
        />
        <button @click="sendMessage" class="send-btn" :disabled="isLoading">
          {{ isLoading ? '답변 중…' : '전송' }}
        </button>
      </div>
      <div class="chat-faq">
        <button
          class="faq-bubble"
          @click="sendFAQ('부동산 매매 계약 시 필요한 준비물은 무엇인가요?')"
        >
          매매 준비물
        </button>
        <button class="faq-bubble" @click="sendFAQ('매매 시 발생하는 세금은 어떤 게 있나요?')">
          매매 세금
        </button>
        <button
          class="faq-bubble"
          @click="
            sendFAQ('실거래 신고는 누가, 언제까지 해야 하나요? 실거래 신고 방법을 알려주세요.')
          "
        >
          실거래 신고 방법
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance, nextTick, watch, computed } from 'vue'
import baseURL from '@/baseURL'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()
const { proxy } = getCurrentInstance()

const messages = ref([])
const newMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)

const today = new Date().toLocaleDateString('ko-KR', {
  weekday: 'long',
  year: 'numeric',
  month: 'long',
  day: 'numeric',
})

const isLoggedIn = computed(() => !!proxy.$auth?.token)
function getUserId() {
  return localStorage.getItem('savedUserId') || ''
}

async function scrollToBottom() {
  await nextTick()
  const el = messagesContainer.value
  if (el) el.scrollTop = el.scrollHeight
}
watch(messages, scrollToBottom)

watch(isLoggedIn, () => {
  messages.value = []
  loadHistory()
})

function renderMarkdown(text) {
  return md.render(text || '')
}

async function loadHistory() {
  try {
    const token = proxy.$auth?.token
    const headers = token ? { Authorization: `Bearer ${token}` } : {}
    const userId = getUserId()

    const res = await baseURL.get('/api/chat/history', {
      headers,
      params: { userId },
    })

    messages.value = res.data.map((m) => ({
      role: m.role,
      content: m.content,
      timestamp: m.userTimestamp || Date.now(),
      loading: false,
    }))
  } catch (e) {
    console.error('History load error:', e)
  }
}

async function sendMessage() {
  const content = newMessage.value.trim()
  if (!content) return

  messages.value.push({ role: 'user', content, timestamp: Date.now(), loading: false })
  newMessage.value = ''

  messages.value.push({
    role: 'assistant',
    content: '답변 중…',
    timestamp: Date.now(),
    loading: true,
  })
  isLoading.value = true

  try {
    const token = proxy.$auth?.token
    const headers = token ? { Authorization: `Bearer ${token}` } : {}
    const userId = getUserId()

    const res = await baseURL.post(
      '/api/chat',
      { userInput: content },
      { headers, params: { userId } },
    )

    const idx = messages.value.findIndex((m) => m.loading)
    if (idx !== -1) {
      messages.value.splice(idx, 1, {
        role: 'assistant',
        content: res.data.content,
        timestamp: Date.now(),
        loading: false,
      })
    }
  } catch (e) {
    console.error('Send message error:', e)
    const idx = messages.value.findIndex((m) => m.loading)
    if (idx !== -1) {
      messages.value.splice(idx, 1, {
        role: 'assistant',
        content: '에러: 서버와 연결할 수 없습니다.',
        timestamp: Date.now(),
        loading: false,
      })
    }
  } finally {
    isLoading.value = false
  }
}
async function sendFAQ(message) {
  newMessage.value = message
  sendMessage()
}

onMounted(loadHistory)
</script>

<style scoped>
.chat-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
}

.chat-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}
.chat-header h2 {
  margin: 0;
  font-size: 1.25rem;
}

.chat-body {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
.date-badge {
  display: inline-block;
  background: #f0f0f0;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  margin-bottom: 1rem;
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  display: flex;
  align-items: flex-end;
}
.message.user {
  justify-content: flex-end;
}
.message.assistant {
  justify-content: flex-start;
}
.message.loading .bubble {
  opacity: 0.6;
  font-style: italic;
}

.avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 0.5rem;
}

.bubble {
  max-width: 70%;
  background: #f5f5f5;
  padding: 0.75rem;
  border-radius: 12px;
}
.message.user .bubble {
  background: #ffe066;
}

.bubble ul {
  padding-left: 1.2rem;
  margin: 0.5rem 0;
}
.bubble li {
  margin-bottom: 0.25rem;
  line-height: 1.4;
}
.bubble strong {
  font-weight: bold;
}

.timestamp {
  display: block;
  font-size: 0.625rem;
  color: #888;
  margin-top: 0.25rem;
  text-align: right;
}

.chat-bottom {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  border-top: 1px solid #eee;
}

.chat-input {
  display: flex;
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
}
.message-input {
  flex: 1;
  padding: 0.5rem 1rem;
  border: 1px solid #ccc;
  border-radius: 20px;
}
.send-btn {
  margin-left: 0.5rem;
  background: #1f8ef1;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 0 1rem;
  cursor: pointer;
}
.send-btn[disabled],
.message-input[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}

.chat-faq {
  display: flex;
  gap: 8px;
  padding: 0.5rem 0.75rem;
  flex-wrap: wrap;
  background-color: #f9fafb;
}
.faq-bubble {
  background-color: #e0f2fe;
  border: none;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: background-color 0.2s;
}
.faq-bubble:hover {
  background-color: #bae6fd;
}
</style>
