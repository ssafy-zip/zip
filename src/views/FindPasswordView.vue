<template>
  <div class="form-container">
    <div class="form-header">
      <router-link to="/">
        <img src="/logo/logo-light-128.png" class="form-logo" />
      </router-link>
    </div>
    <form class="form-box" @submit.prevent="handleFindPw">
      <h2 class="form-title">비밀번호 재설정</h2>
      <!-- 아이디 -->
      <BaseInput
        id="username"
        v-model="username"
        icon="fa-solid fa-user fa-2x"
        placeholder="아이디"
        :clearable="true"
        autofocus
      />

      <!-- 이메일 -->
      <BaseInput
        id="email"
        v-model="email"
        icon="fa-solid fa-envelope fa-2x"
        placeholder="이메일"
        :clearable="true"
        :errorMessage="email && !validEmail ? ERROR_MESSAGES.INVALID_EMAIL : ''"
      />

      <button type="submit" class="form-submit-button" :disabled="isLoading || !canSubmit">
        {{ isLoading ? '확인 중…' : '비밀번호 재설정' }}
      </button>
    </form>

    <!-- 결과 메시지 -->
    <div v-if="message" class="form-message" :class="{ error: isError, success: !isError }">
      {{ message }}
    </div>

    <div class="link-group">
      <router-link :to="{ name: 'FindId' }">아이디 찾기</router-link>
      <router-link :to="{ name: 'Login' }">로그인</router-link>
      <router-link :to="{ name: 'Join' }">회원가입</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useValidation } from '@/utils/useValidation'
import { ERROR_MESSAGES } from '@/constants/error'
import BaseInput from '@/components/BaseInput.vue'
const { isValidEmail } = useValidation()

const username = ref('')
const email = ref('')

const isLoading = ref(false)
const message = ref('')
const isError = ref(false)

const validEmail = computed(() => isValidEmail(email.value))

const canSubmit = computed(() => {
  return username.value !== '' && email.value !== '' && validEmail.value
})

const router = useRouter()

const handleFindPw = async () => {
  // 1) 입력 검증
  if (!username.value || !email.value) {
    message.value = ERROR_MESSAGES.REQUIRED_FIELDS
    isError.value = true
    return
  }

  // 2) 상태 초기화
  message.value = ''
  isError.value = false
  isLoading.value = true

  try {
    // 3) API 호출
    const { data } = await axios.post('/api/users/canExchangePassword', {
      userId: username.value,
      email: email.value,
    })

    // 4) 결과 처리
    const canExchange = data === true || data === 'true'
    if (canExchange) {
      router.push({
        name: 'ResetPasswordForm',
        query: { userId: username.value },
      })
    } else {
      message.value = '등록된 사용자 정보가 없습니다.'
      isError.value = true
    }
  } catch (err) {
    console.error(err)
    message.value = err.response?.data?.message || '서버 오류가 발생했습니다.'
    isError.value = true
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped src="@/assets/css/authForm.css"></style>
