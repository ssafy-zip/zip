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
      <button type="submit" class="modal__submit" :disabled="!isValid">확인</button>
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
const error = ref('')
const isValid = computed(() => {
  return 6 <= password.value.length && password.value === passwordConfirm.value
})

const close = () => emit('close')

// 제출 시 API 호출
const submit = async () => {
  if (!isValid.value) {
    error.value = '비밀번호가 일치하지 않거나 6자 미만입니다.'
    return
  }
  try {
    const token = localStorage.getItem('authToken')
    // updatePassword API 호출 (/api/users/updatePassword)
    await axios.post('/api/users/updatePassword', password.value, {
      headers: { Authorization: `Bearer ${token}` },
    })
    emit('updated')
    close()
  } catch (err) {
    console.error('비밀번호 변경 실패', err)
    error.value = '변경에 실패했습니다. 다시 시도해주세요.'
  }
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
