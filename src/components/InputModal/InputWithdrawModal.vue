<template>
  <InputModal
    :visible="visible"
    @close="close"
    title="계정 삭제"
    :icon="['fa-solid', 'fa-user-slash', 'fa-3x']"
    description="삭제한 계정은 복구할 수 없습니다.<br>삭제하려면 비밀번호를 입력해주세요."
  >
    <form @submit.prevent="submit" class="modal__form">
      <input
        type="password"
        v-model="password"
        placeholder="비밀번호"
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
const isValid = computed(() => {
  return 0 < password.value.length
})

const close = () => emit('close')

const submit = () => {
  emit('submit', password.value)
  close()
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
