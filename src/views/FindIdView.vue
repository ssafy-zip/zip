<template>
  <div class="form-container">
    <!-- 헤더 -->
    <div class="form-header">
      <router-link to="/">
        <img src="/logo/logo-light-128.png" class="form-logo" />
      </router-link>
    </div>

    <form class="form-box" @submit.prevent="handleFindId">
      <h2 class="form-title">아이디 찾기</h2>
      <!-- 이메일 -->
      <BaseInput
        id="email"
        v-model="email"
        icon="fa-solid fa-envelope fa-2x"
        placeholder="가입 시 등록한 이메일을 입력하세요"
        autofocus
        :clearable="true"
        :errorMessage="email && !validEmail ? ERROR_MESSAGES.INVALID_EMAIL : ''"
      />

      <button type="submit" class="form-submit-button" :disabled="isLoading || !canSubmit">
        {{ isLoading ? '전송 중…' : '아이디 찾기' }}
      </button>
    </form>

    <!-- 결과 메시지 -->
    <div v-if="message" class="form-message" :class="{ error: isError, success: !isError }">
      {{ message }}
    </div>

    <div class="link-group">
      <router-link :to="{ name: 'FindPassword' }">비밀번호 재설정</router-link>
      <router-link :to="{ name: 'Login' }">로그인</router-link>
      <router-link :to="{ name: 'Join' }">회원가입</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import baseURL from '@/baseURL'
import { useValidation } from '@/utils/useValidation'
import { ERROR_MESSAGES } from '@/constants/error'
import BaseInput from '@/components/BaseInput.vue'

const { isValidEmail } = useValidation()

const email = ref('')

const isLoading = ref(false)
const message = ref('') // 화면에 띄울 메시지
const isError = ref(false) // 에러 여부

const validEmail = computed(() => isValidEmail(email.value))

const canSubmit = computed(() => {
  return email.value !== '' && validEmail.value
})

const handleFindId = async () => {
  if (!email.value) {
    message.value = ERROR_MESSAGES.REQUIRED_FIELDS
    isError.value = true
    return
  }

  // 이전 메시지 초기화
  message.value = ''
  isError.value = false
  isLoading.value = true

  try {
    const { data } = await baseURL.post('/api/users/find-id', {
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

<style scoped src="@/assets/css/authForm.css"></style>
