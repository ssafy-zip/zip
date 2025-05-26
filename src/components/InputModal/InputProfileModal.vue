<template>
  <div v-if="visible" class="modal-mask">
    <div class="modal-wrapper">
      <div class="modal-container">
        <h3 class="modal-title">개인 정보 수정</h3>
        <div class="modal-body">
          <!-- 이름 수정 -->
          <div class="form-group">
            <label for="name">이름</label>
            <input id="name" v-model="form.name" type="text" />
          </div>
          <!-- 이메일 수정 -->
          <div class="form-group">
            <label for="email">이메일 주소</label>
            <input id="email" v-model="form.email" type="email" />
          </div>
          <!-- 휴대폰 수정 -->
          <div class="form-group">
            <label for="phone">휴대폰 번호</label>
            <input id="phone" v-model="form.phone" type="tel" placeholder="010-1234-5678" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn" @click="save">저장</button>
          <button class="btn" @click="close">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import baseURL from '@/baseURL'

const props = defineProps({
  visible: Boolean,
  initial: {
    type: Object,
    default: () => ({ id: '', name: '', email: '', phone: '' }),
  },
})
const emit = defineEmits(['close', 'saved'])

const form = reactive({
  name: '',
  email: '',
  phone: '',
})

// 모달이 열릴 때 초기값 세팅
watch(
  () => props.visible,
  (open) => {
    if (open) {
      form.name = props.initial.name
      form.email = props.initial.email
      form.phone = props.initial.phone
    }
  },
)

async function save() {
  try {
    const payload = {
      userId: props.initial.id,
      name: form.name,
      email: form.email,
      phone: form.phone,
    }
    const token = localStorage.getItem('authToken')
    await baseURL.post('/api/users/updateUser', payload, {
      headers: { Authorization: `Bearer ${token}` },
    })
    emit('saved', { name: form.name, email: form.email, phone: form.phone })
  } catch (error) {
    console.error('업데이트 실패', error)
  }
}

function close() {
  emit('close')
}
</script>

<style scoped>
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-wrapper {
  width: 100%;
  max-width: 400px;
  margin: 0 16px;
}
.modal-container {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
.modal-title {
  font-size: 18px;
  font-weight: bold;
  margin: 16px;
}
.modal-body {
  padding: 0 16px 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.form-group label {
  font-size: 14px;
  margin-bottom: 4px;
}
.form-group input {
  padding: 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  background: #f9fafb;
}
.btn {
  padding: 6px 12px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.btn:hover {
  background-color: #e5e7eb;
}
</style>
