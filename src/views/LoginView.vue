<template>
  <div class="login-wrapper">
    <!-- 로고 -->
    <div class="form-header">
      <a href="#"><img src="/image/logo/logo-black-large.png" class="form-logo" /></a>
    </div>

    <!-- 로그인 폼 -->
    <form class="login-form" @submit.prevent="handleSubmit">
      <!-- 아이디 -->
      <div class="form-group">
        <label for="username" class="form-label">
          <i class="fa-solid fa-envelope fa-2x"></i>
        </label>
        <div class="input-wrapper">
          <input type="text" id="username" v-model="username" placeholder="아이디" />
          <button
            type="button"
            class="clear-button"
            @click="username = ''"
            aria-label="입력 지우기"
          >
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>
      </div>

      <!-- 비밀번호 -->
      <div class="form-group horizontal">
        <label for="password" class="form-label">
          <i class="fa-solid fa-lock fa-2x"></i>
        </label>
        <div class="input-wrapper">
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            v-model="password"
            placeholder="비밀번호"
          />
          <button
            type="button"
            class="toggle-password"
            @click="togglePassword"
            aria-label="비밀번호 보기/숨기기"
          >
            <i v-if="!showPassword" class="fa-solid fa-eye"></i>
            <i v-else class="fa-solid fa-eye-slash"></i>
          </button>
        </div>
      </div>

      <!-- 아이디 저장 -->
      <div class="form-remember">
        <label class="remember-label">
          <input type="checkbox" v-model="rememberId" />
          아이디 저장
        </label>
      </div>

      <!-- 로그인 버튼 -->
      <button type="submit" class="login-button">로그인</button>

      <hr />

      <!-- 소셜 로그인 -->
      <div class="social-login">
        <button type="button" class="social-button google">
          <i class="fa-brands fa-google"></i> Google
        </button>
        <button type="button" class="social-button kakao">
          <i class="fa-solid fa-comment"></i> Kakao
        </button>
        <button type="button" class="social-button naver">
          <i class="fa-brands fa-neos"></i> Naver
        </button>
      </div>

      <!-- 링크 -->
      <div class="login-links">
        <a href="/findId">아이디 찾기</a>
        <span>|</span>
        <a href="/findPassword">비밀번호 찾기</a>
        <span>|</span>
        <a href="/join">회원가입</a>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const username = ref('')
const password = ref('')
const rememberId = ref(false)
const showPassword = ref(false)

const togglePassword = () => {
  showPassword.value = !showPassword.value
}

const handleSubmit = () => {
  // TODO: 로그인 로직 처리
  alert(`아이디: ${username.value}, 비밀번호: ${password.value}`)
}
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css');

body {
  background-color: #f9fafb;
  margin: 0;
  font-family: Arial, sans-serif;
}

.login-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
}

.form-logo {
  width: 128px;
}

.login-form {
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 40px;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-group {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 8px;
}

.form-label {
  width: 40px;
  font-weight: bold;
  color: #374151;
  text-align: center;
}

.input-wrapper {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
}

.input-wrapper input {
  width: 100%;
  padding: 10px;
  padding-right: 40px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}

.clear-button,
.toggle-password {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #6b7280;
  padding: 4px;
}

.clear-button:hover,
.toggle-password:hover {
  color: #111827;
}

.form-remember {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #374151;
}

.remember-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.remember-label input[type='checkbox'] {
  accent-color: #2563eb;
  cursor: pointer;
}

.login-button {
  width: 100%;
  padding: 12px;
  background-color: #2563eb;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
  transition: background-color 0.2s ease;
}

.login-button:hover {
  background-color: #1d4ed8;
}

.login-links {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
  color: #6b7280;
}

.login-links a {
  color: #2563eb;
  text-decoration: none;
}

.login-links a:hover {
  text-decoration: underline;
}

.login-form hr {
  border: none;
  height: 1px;
  background-color: #e5e7eb;
  margin: 24px 0;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 20px 0;
  flex-wrap: wrap;
}

.social-button {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: bold;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  color: white;
  white-space: nowrap;
  transition: background-color 0.2s ease;
  min-width: 100px;
}

.social-button.google {
  background-color: #ea4335;
}

.social-button.kakao {
  background-color: #fee500;
  color: #3c1e1e;
}

.social-button.naver {
  background-color: #03c75a;
}
</style>
