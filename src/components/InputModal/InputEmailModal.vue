<template>
  <InputModal
    :visible="visible"
    @close="close"
    title="이메일"
    :icon="['fas', 'fa-envelope', 'fa-3x']"
    description="이메일를 입력하세요."
  >
    <form @submit.prevent="submit" class="modal__form">
      <input type="text" v-model="email" placeholder="이메일" class="modal__input" required />
      <div class="modal__input-wrapper">
        <input type="text" v-model="code" placeholder="인증코드" class="modal__input" required />
        <button
          type="button"
          class="modal__input-option-button"
          @click="() => console.log('인증코드 발송')"
        >
          발송하기
        </button>
      </div>
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

const email = ref('')
const code = ref('')
const isValid = computed(() => {
  return 0 < email.value.length
})

const close = () => emit('close')

const submit = () => {
  emit('submit', email.value)
  close()
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
