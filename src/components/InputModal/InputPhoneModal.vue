<template>
  <InputModal
    :visible="visible"
    @close="close"
    title="휴대폰 번호"
    :icon="['fa-solid', 'fa-phone', 'fa-3x']"
    description="휴대폰 번호를 입력하세요."
  >
    <form @submit.prevent="submit" class="modal__form">
      <input
        type="tel"
        inputmode="numeric"
        autocomplete="tel"
        v-model="phone"
        placeholder="휴대폰 번호"
        class="modal__input"
        required
      />
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

const phone = ref('')
const code = ref('')
const isValid = computed(() => {
  return 0 < phone.value.length
})

const close = () => emit('close')

const submit = () => {
  emit('submit', phone.value)
  close()
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
