<template>
  <AuthFormLayout>
    <div class="form-box">
      <h2 class="form-title">비밀번호 재설정</h2>
      <form class="reset-form" @submit.prevent="handleSubmit">
        <!-- 새 비밀번호 -->
        <BaseInput
          id="newPassword"
          v-model="newPassword"
          icon="fa-solid fa-lock fa-2x"
          placeholder="새 비밀번호"
          :type="showPassword ? 'text' : 'password'"
          :errorMessage="newPassword && !validPassword ? ERROR_MESSAGES.INVALID_PASSWORD : ''"
          :valid="newPassword.length > 0 && validPassword"
        />

        <!-- 비밀번호 확인 -->
        <BaseInput
          id="confirmPassword"
          v-model="confirmPassword"
          icon="fa-solid fa-lock fa-2x"
          placeholder="비밀번호 확인"
          type="password"
          :errorMessage="confirmPassword && !passwordsMatch ? ERROR_MESSAGES.PASSWORD_MISMATCH : ''"
          :valid="confirmPassword.length > 0 && passwordsMatch"
        />

        <button type="submit" class="form-submit-button" :disabled="isLoading || !canSubmit">
          {{ isLoading ? '처리 중…' : '비밀번호 변경' }}
        </button>
      </form>

      <div v-if="message" class="form-message" :class="{ error: isError, success: !isError }">
        {{ message }}
      </div>
    </div>
  </AuthFormLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import baseURL from '@/baseURL'
import { ERROR_MESSAGES } from '@/constants/error'
import AuthFormLayout from './AuthFormLayout.vue'
import BaseInput from '@/components/BaseInput.vue'
import { useValidation } from '@/utils/useValidation'
const { isValidPassword } = useValidation()

const route = useRoute()
const router = useRouter()
const userId = ref('')

const newPassword = ref('')
const confirmPassword = ref('')
const isLoading = ref(false)
const message = ref('')
const isError = ref(false)

const validPassword = computed(() => isValidPassword(newPassword.value))
const passwordsMatch = computed(
  () => confirmPassword.value.length > 0 && newPassword.value === confirmPassword.value,
)
const canSubmit = computed(
  () => newPassword.value !== '' && confirmPassword.value !== '' && validPassword && passwordsMatch,
)

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
    message.value = ERROR_MESSAGES.AGREEMENT_REQUIRED
    isError.value = true
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    message.value = ERROR_MESSAGES.PASSWORD_MISMATCH
    isError.value = true
    return
  }

  isLoading.value = true
  try {
    // userId는 쿼리로, password는 바디로 전송
    const { data } = await baseURL.post(
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

<style scoped src="@/assets/css/authForm.css"></style>
