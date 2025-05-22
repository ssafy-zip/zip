<template>
  <div class="find-wrapper">
    <h2 class="find-title">아이디 찾기</h2>
    <form class="find-form" @submit.prevent="handleFindId">
      <FormInput
        icon="fa-envelope"
        type="email"
        v-model="email"
        placeholder="가입한 이메일"
        required
      />
      <button type="submit" class="find-button" :disabled="isLoading">
        {{ isLoading ? '전송 중…' : '아이디 찾기' }}
      </button>
    </form>

    <!-- 결과 메시지 -->
    <div v-if="message" class="find-message" :class="{ error: isError, success: !isError }">
      {{ message }}
    </div>

    <div class="find-links">
      <router-link to="/login">로그인</router-link> |
      <router-link to="/findPassword">비밀번호 찾기</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import FormInput from '@/components/FormInput.vue'

const email = ref('')
const isLoading = ref(false)
const message = ref('') // 화면에 띄울 메시지
const isError = ref(false) // 에러 여부

const handleFindId = async () => {
  if (!email.value) {
    message.value = '이메일을 입력하세요.'
    isError.value = true
    return
  }

  // 이전 메시지 초기화
  message.value = ''
  isError.value = false
  isLoading.value = true

  try {
    const { data } = await axios.post('/api/users/find-id', {
      email: email.value,
    })
    // 성공 메시지
    message.value = data.message
    isError.value = false
  } catch (err) {
    console.error(err)
    message.value = err.response?.data?.message || '아이디 찾기에 실패했습니다.'
    isError.value = true
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.find-wrapper {
  max-width: 400px;
  margin: 100px auto;
  padding: 40px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.find-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #111827;
  text-align: center;
}

.find-button {
  width: 100%;
  padding: 12px;
  background-color: #2563eb;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.find-button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.find-button:hover:enabled {
  background-color: #1d4ed8;
}

/* 결과 메시지 스타일 */
.find-message {
  margin-top: 16px;
  padding: 12px;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
}
.find-message.success {
  background-color: #ecfdf5;
  color: #065f46;
}
.find-message.error {
  background-color: #fce7e7;
  color: #991b1b;
}

.find-links {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.find-links a {
  color: #2563eb;
  text-decoration: none;
}

.find-links a:hover {
  text-decoration: underline;
}
</style>
