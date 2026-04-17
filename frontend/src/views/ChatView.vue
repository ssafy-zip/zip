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

    <!-- input -->
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

// 로그인 상태 확인
const isLoggedIn = computed(() => !!proxy.$auth?.token)

// savedUserId helper — 없으면 빈 문자열 반환
function getUserId() {
  return localStorage.getItem('savedUserId') || ''
}

// 스크롤 위치 조정
async function scrollToBottom() {
  await nextTick()
  const el = messagesContainer.value
  if (el) el.scrollTop = el.scrollHeight
}
watch(messages, scrollToBottom)

// 로그인 상태 바뀔 때마다 세션 초기화
watch(isLoggedIn, () => {
  messages.value = []
  loadHistory()
})

// 마크다운→HTML
function renderMarkdown(text) {
  return md.render(text || '')
}

// 대화 기록 로드
async function loadHistory() {
  try {
    const token = proxy.$auth?.token
    const headers = token ? { Authorization: `Bearer ${token}` } : {}
    const userId = getUserId()

    console.log(token)
    console.log(userId)
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

// 메시지 전송
async function sendMessage() {
  const content = newMessage.value.trim()
  if (!content) return

  // 사용자 메시지 표시
  messages.value.push({ role: 'user', content, timestamp: Date.now(), loading: false })
  newMessage.value = ''

  // 로딩 표시
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

    console.log(userId)

    // userId는 쿼리 파라미터로 전달
    const res = await baseURL.post(
      '/api/chat',
      { userInput: content },
      { headers, params: { userId } },
    )

    // 로딩 메시지 교체
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

// 컴포넌트 마운트 시 기록 불러오기
onMounted(loadHistory)
</script>

<style scoped>
.chat-wrapper {
  max-width: 600px;
  margin: 2rem auto;
  display: flex;
  flex-direction: column;
  height: 80vh;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chat-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}
.chat-header h2 {
  margin: 0;
  font-size: 1.25rem;
}

.chat-body {
  flex: 1;
  padding: 1rem;
  overflow: hidden;
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
  height: calc(100% - 2rem);
  overflow-y: auto;
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

.chat-input {
  display: flex;
  padding: 0.75rem;
  border-top: 1px solid #eee;
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
</style>
