<template>
  <InputModal
    :visible="visible"
    @close="close"
    title="이름"
    :icon="['fa-solid', 'fa-user', 'fa-3x']"
    description="이름을 입력하세요."
  >
    <form @submit.prevent="submit" class="modal__form">
      <input type="text" v-model="name" placeholder="이름" class="modal__input" required />
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

const name = ref('')
const isValid = computed(() => {
  return 0 <= name.value.length
})

const close = () => emit('close')

const submit = () => {
  emit('submit', name.value)
  close()
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
