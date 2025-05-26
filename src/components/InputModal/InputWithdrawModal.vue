<template>
  <InputModal
    :visible="visible"
    @close="close"
    title="계정 삭제"
    :icon="['fa-solid', 'fa-user-slash', 'fa-3x']"
    description="삭제한 계정은 복구할 수 없습니다.<br/>비밀번호를 입력해주세요."
  >
    <form @submit.prevent="submit" class="modal__form">
      <input
        type="password"
        v-model="password"
        placeholder="비밀번호"
        class="modal__input"
        required
      />
      <div v-if="error" class="error-msg">{{ error }}</div>
      <button type="submit" class="modal__submit" :disabled="!isValid">확인</button>
    </form>
  </InputModal>
</template>

<script setup>
import InputModal from './InputModal.vue'
import { ref, computed, watch, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import baseURL from '@/baseURL'
// Props
const props = defineProps({ visible: Boolean })
const emit = defineEmits(['close', 'deleted'])

// Form state
const password = ref('')
const error = ref('')
const router = useRouter()

// Valid when password entered
const isValid = computed(() => password.value.length > 0)

// Reset on open
watch(
  () => props.visible,
  (open) => {
    if (open) {
      password.value = ''
      error.value = ''
    }
  },
)

// Close modal
const close = () => emit('close')
const { proxy } = getCurrentInstance()
// Submit deletion
const submit = async () => {
  if (!isValid.value) return
  try {
    const token = localStorage.getItem('authToken')
    await baseURL.delete('/api/users/deleteUserId', {
      headers: { Authorization: `Bearer ${token}` },
      data: password.value,
    })
    emit('deleted')
    close()

    localStorage.removeItem('authToken')
    localStorage.removeItem('savedUserId')
    localStorage.removeItem('userRole')

    // 2) 인증 상태 초기화 (플러그인 사용 시)
    proxy.$logout()
    router.push({ name: 'Home' })
  } catch (e) {
    console.error('계정 삭제 실패', e)
    error.value = '비밀번호가 올바르지 않거나 삭제에 실패했습니다.'
  }
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
<style scoped>
.error-msg {
  color: #dc2626;
  font-size: 13px;
  margin: 8px 0;
}
</style>
