<template>
  <div class="join-wrapper">
    <div class="form-header">
      <a href="#"><img src="/logo/logo-light-128.png" class="form-logo" /></a>
    </div>

    <form class="join-form" @submit.prevent="handleJoin">
      <h2 class="join-title">회원가입</h2>

      <!-- 아이디 (userId) -->
      <FormInput icon="fa-user" v-model="form.userId" placeholder="아이디" required />

      <!-- 비밀번호 -->
      <FormInput
        icon="fa-lock"
        type="password"
        v-model="form.password"
        placeholder="비밀번호"
        required
      />

      <!-- 비밀번호 확인 -->
      <FormInput
        icon="fa-lock"
        type="password"
        v-model="form.confirmPassword"
        placeholder="비밀번호 확인"
        required
      />
      <!-- 실시간 매칭 결과 -->
      <p v-if="form.confirmPassword && !passwordsMatch" class="error">
        비밀번호가 일치하지 않습니다.
      </p>

      <!-- 이름 -->
      <FormInput icon="fa-id-card" v-model="form.name" placeholder="이름" required />

      <!-- 이메일 -->
      <FormInput
        icon="fa-envelope"
        type="email"
        v-model="form.email"
        placeholder="이메일"
        required
      />

      <!-- 전화번호 -->
      <FormInput icon="fa-phone" type="tel" v-model="form.phone" placeholder="전화번호" required />

      <!-- 약관 동의 -->
      <div class="form-agree-multiple">
        <div class="agree-item">
          <label class="agree-label">
            <input type="checkbox" v-model="agreeAll" @change="checkAll" />
            전체 동의
          </label>
        </div>
        <div class="agree-item">
          <label class="agree-label">
            <input type="checkbox" v-model="form.agreeService" />
            (필수) 서비스 이용약관 동의
          </label>
          <button type="button" class="terms-button" @click="openTerms('service')">
            약관 보기
          </button>
        </div>
        <div class="agree-item">
          <label class="agree-label">
            <input type="checkbox" v-model="form.agreePrivacy" />
            (필수) 개인정보 수집 및 이용 동의
          </label>
          <button type="button" class="terms-button" @click="openTerms('privacy')">
            약관 보기
          </button>
        </div>
      </div>

      <!-- 가입하기 버튼: 비밀번호 매칭 안 되면 비활성화 -->
      <button type="submit" class="join-button" :disabled="!passwordsMatch">가입하기</button>

      <div class="join-links">
        <router-link to="/login"> 이미 계정이 있으신가요? 로그인 </router-link>
      </div>
    </form>

    <!-- 약관 모달 -->
    <div id="terms-modal" class="modal" :class="{ hidden: !showModal }">
      <div class="modal-content">
        <button class="close-button" @click="closeTerms">&times;</button>
        <h3 id="terms-title">{{ modalTitle }}</h3>
        <div id="terms-body" class="terms-text" v-html="modalContent"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import FormInput from '@/components/FormInput.vue'

defineOptions({ name: 'JoinView' })

const router = useRouter()

const form = ref({
  userId: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
  phone: '',
  role: '',
  agreeService: false,
  agreePrivacy: false,
})
const agreeAll = ref(false)
const showModal = ref(false)
const modalTitle = ref('')
const modalContent = ref('')

// 비밀번호 일치 여부 계산
const passwordsMatch = computed(
  () => form.value.confirmPassword.length > 0 && form.value.password === form.value.confirmPassword,
)

watch(
  () => form.value.confirmPassword,
  (newVal) => {
    if (newVal && !passwordsMatch.value) {
      console.log('비밀번호 불일치 감지')
    }
  },
)

// 전체 동의 체크
function checkAll() {
  form.value.agreeService = agreeAll.value
  form.value.agreePrivacy = agreeAll.value
}

// 약관 모달 열기
async function openTerms(type) {
  modalTitle.value = type === 'service' ? '서비스 이용약관' : '개인정보 수집 및 이용 동의'
  const res = await fetch(`/terms/${type}.html`)
  modalContent.value = await res.text()
  showModal.value = true
}

// 약관 모달 닫기
function closeTerms() {
  showModal.value = false
}

// 회원가입 처리
async function handleJoin() {
  if (!passwordsMatch.value) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }
  if (!form.value.agreeService || !form.value.agreePrivacy) {
    alert('모든 필수 약관에 동의해야 가입이 가능합니다.')
    return
  }

  try {
    await axios.post('/api/users', {
      userId: form.value.userId,
      password: form.value.password,
      name: form.value.name,
      email: form.value.email,
      phone: form.value.phone,
      role: form.value.role,
    })
    alert('회원가입이 완료되었습니다!')
    router.push('/login')
  } catch (err) {
    console.error(err)
    alert('회원가입 중 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css');

body {
  background-color: #f9fafb;
  margin: 0;
  font-family: Arial, sans-serif;
}

.join-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
}

.join-form {
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 40px;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.join-title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 24px;
  color: #111827;
}

.form-group {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 8px;
}

.form-label {
  width: 60px;
  color: #374151;
  text-align: right;
}

.input-wrapper {
  flex: 1;
}

.input-wrapper select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}

.join-button {
  width: 100%;
  padding: 12px;
  background-color: #2563eb;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 10px;
}

.join-button:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}

.join-button:hover:not(:disabled) {
  background-color: #1d4ed8;
}

.join-links {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
}

.join-links a {
  color: #2563eb;
  text-decoration: none;
}

.join-links a:hover {
  text-decoration: underline;
}

/* 약관 동의 */
.form-agree-multiple {
  margin: 12px 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
  color: #374151;
}

.agree-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.agree-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.terms-button {
  background: none;
  border: none;
  color: #2563eb;
  text-decoration: underline;
  cursor: pointer;
  font-size: 14px;
  padding: 0;
}

.terms-button:hover {
  color: #1d4ed8;
}

/* 에러 메시지 */
.error {
  margin: 4px 0 12px;
  color: #e53e3e;
  font-size: 0.875rem;
}

/* 약관 모달 */
.modal.hidden {
  display: none;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-content {
  background-color: white;
  padding: 24px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80%;
  overflow-y: auto;
  position: relative;
}

.close-button {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 20px;
  font-weight: bold;
  background: none;
  border: none;
  cursor: pointer;
}

.terms-text p {
  margin: 12px 0;
  font-size: 14px;
  color: #374151;
}
</style>
