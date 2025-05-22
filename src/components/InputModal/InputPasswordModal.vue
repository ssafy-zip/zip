<template>
  <InputModal
    :visible="visible"
    @close="close"
    title="비밀번호"
    :icon="['fa-solid', 'fa-lock', 'fa-3x']"
    description="비밀번호를 입력하세요."
  >
    <form @submit.prevent="submit" class="modal__form">
      <input
        type="password"
        v-model="password"
        placeholder="비밀번호 (6자 이상)"
        class="modal__input"
        required
      />
      <input
        type="password"
        v-model="passwordConfirm"
        placeholder="비밀번호 확인 (6자 이상)"
        class="modal__input"
        required
      />
      <button type="submit" class="modal__submit" :disabled="!isValid || isLoading">
        {{ isLoading ? '변경 중…' : '확인' }}
      </button>
      <p v-if="error" class="modal__error">{{ error }}</p>
    </form>
  </InputModal>
</template>

<script setup>
import InputModal from './InputModal.vue'
import { ref, computed } from 'vue'
import axios from 'axios'

defineProps({
  visible: Boolean,
})
const emit = defineEmits(['submit', 'close'])

const password = ref('')
const passwordConfirm = ref('')
const isLoading = ref(false)
const error = ref('')

const isValid = computed(() => {
  return password.value.length >= 6 && password.value === passwordConfirm.value
})

const close = () => emit('close')

const submit = async () => {
  error.value = ''
  if (!isValid.value) {
    error.value = '비밀번호가 일치하지 않거나 6자 미만입니다.'
    return
  }

  isLoading.value = true
  try {
    const token = localStorage.getItem('authToken')
    await axios.post(
      '/api/users/updatePassword',
      { newPassword: password.value }, // <-- DTO 형태로 보냅니다
      {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      },
    )
    emit('submit') // 부모에게 완료 알림
    close()
  } catch (err) {
    console.error('비밀번호 변경 실패', err)
    error.value = err.response?.data || '변경에 실패했습니다. 다시 시도해주세요.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
