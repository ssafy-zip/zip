<template>
  <div class="reset-wrapper">
    <h2 class="reset-title">비밀번호 재설정</h2>
    <form class="reset-form" @submit.prevent="handleSubmit">
      <FormInput
        icon="fa-lock"
        type="password"
        v-model="newPassword"
        placeholder="새 비밀번호"
        required
      />
      <FormInput
        icon="fa-lock"
        type="password"
        v-model="confirmPassword"
        placeholder="비밀번호 확인"
        required
      />
      <button type="submit" class="reset-button" :disabled="isLoading">
        {{ isLoading ? '처리 중…' : '비밀번호 변경' }}
      </button>
    </form>

    <div v-if="message" class="reset-message" :class="{ error: isError, success: !isError }">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import FormInput from '@/components/FormInput.vue'

const route = useRoute()
const router = useRouter()
const userId = ref('')

const newPassword = ref('')
const confirmPassword = ref('')
const isLoading = ref(false)
const message = ref('')
const isError = ref(false)

onMounted(() => {
  if (route.query.userId) {
    userId.value = route.query.userId
  } else {
    router.replace({ name: 'FindPassword' })
  }
})

const handleSubmit = async () => {
  message.value = ''
  isError.value = false

  // 검증
  if (!newPassword.value || !confirmPassword.value) {
    message.value = '모든 항목을 입력해주세요.'
    isError.value = true
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    message.value = '비밀번호가 일치하지 않습니다.'
    isError.value = true
    return
  }

  isLoading.value = true
  try {
    // userId는 쿼리로, password는 바디로 전송
    const { data } = await axios.post(
      '/api/users/reset-password',
      { password: newPassword.value },
      { params: { userId: userId.value } },
    )

    // 백엔드가 반환한 메시지를 잠시 보여주고 로그인으로 이동
    message.value = data
    isError.value = false

    setTimeout(() => {
      router.push({ name: 'Login' })
    }, 2000)
  } catch (err) {
    console.error(err)
    message.value = err.response?.data || '비밀번호 변경에 실패했습니다.'
    isError.value = true
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.reset-wrapper {
  max-width: 400px;
  margin: 100px auto;
  padding: 40px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.reset-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #111827;
  text-align: center;
}

.reset-button {
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

.reset-button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.reset-button:hover:enabled {
  background-color: #1d4ed8;
}

.reset-message {
  margin-top: 16px;
  padding: 12px;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
}

.reset-message.success {
  background-color: #ecfdf5;
  color: #065f46;
}

.reset-message.error {
  background-color: #fce7e7;
  color: #991b1b;
}
</style>
