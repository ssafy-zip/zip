<template>
  <div class="join-wrapper">
    <div class="form-header">
      <a href="#"><img src="/image/logo/logo-black-large.png" class="form-logo" /></a>
    </div>

    <form class="join-form" @submit.prevent="handleJoin">
      <h2 class="join-title">회원가입</h2>

      <!-- 아이디 -->
      <FormInput icon="fa-user" v-model="form.username" placeholder="아이디" required />

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

      <button type="submit" class="join-button">가입하기</button>

      <div class="join-links">
        <router-link to="/login">이미 계정이 있으신가요? 로그인</router-link>
      </div>
    </form>

    <!-- 약관 모달 -->
    <div id="terms-modal" class="modal" :class="{ hidden: !showModal }">
      <div class="modal-content">
        <span class="close-button" @click="closeTerms">&times;</span>
        <h3 id="terms-title">{{ modalTitle }}</h3>
        <div id="terms-body" class="terms-text" v-html="modalContent"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

import FormInput from '@/components/FormInput.vue'

// 폼 데이터
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
  phone: '',
  agreeService: false,
  agreePrivacy: false,
})

const agreeAll = ref(false)
const showModal = ref(false)
const modalTitle = ref('')
const modalContent = ref('')

// 전체 동의 체크
const checkAll = () => {
  form.value.agreeService = agreeAll.value
  form.value.agreePrivacy = agreeAll.value
}

// 모달 열기
const openTerms = async (type) => {
  modalTitle.value = type === 'service' ? '서비스 이용약관' : '개인정보 수집 및 이용 동의'
  const res = await fetch(`/terms/${type}.html`)
  modalContent.value = await res.text()
  showModal.value = true
}

// 모달 닫기
const closeTerms = () => {
  showModal.value = false
}

// 제출 처리
const handleJoin = () => {
  if (form.value.password !== form.value.confirmPassword) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  if (!form.value.agreeService || !form.value.agreePrivacy) {
    alert('모든 필수 약관에 동의해야 가입이 가능합니다.')
    return
  }

  // TODO: 서버 전송
  alert('회원가입 성공!')
}
</script>

<script>
export default {
  name: 'JoinView',
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

.form-logo {
  width: 128px;
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
  width: 40px;
  color: #374151;
  text-align: center;
}

.input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
}

.input-wrapper input {
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

.join-button:hover {
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

#agree-all {
  accent-color: #111827;
  transform: scale(1.2);
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

.modal-content h3 {
  margin-top: 0;
}

.terms-text p {
  margin: 12px 0;
  font-size: 14px;
  color: #374151;
}

.close-button {
  position: absolute;
  right: 16px;
  top: 12px;
  font-size: 20px;
  font-weight: bold;
  color: #6b7280;
  background: none;
  border: none;
  cursor: pointer;
}
</style>
