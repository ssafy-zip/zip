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

defineProps({
  visible: Boolean,
})
const emit = defineEmits(['submit', 'close'])

const password = ref('')
const passwordConfirm = ref('')
const isValid = computed(() => {
  return 6 <= password.value.length && password.value === passwordConfirm.value
})

const close = () => emit('close')

const submit = () => {
  emit('submit', password.value)
  close()
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
