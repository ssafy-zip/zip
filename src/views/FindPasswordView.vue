<template>
  <div class="find-wrapper">
    <h2 class="find-title">비밀번호 찾기</h2>
    <form class="find-form" @submit.prevent="handleFindPw">
      <FormInput icon="fa-user" v-model="username" placeholder="아이디" required />
      <FormInput
        icon="fa-envelope"
        type="email"
        v-model="email"
        placeholder="가입한 이메일"
        required
      />
      <button type="submit" class="find-button">비밀번호 찾기</button>
    </form>
    <div class="find-links">
      <router-link to="/login">로그인</router-link> |
      <router-link to="/findId">아이디 찾기</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FormInput from '@/components/FormInput.vue'

const username = ref('')
const email = ref('')

const handleFindPw = () => {
  if (!username.value || !email.value) {
    alert('모든 항목을 입력해주세요.')
    return
  }
  // TODO: 서버 연동
  alert(`입력된 아이디: ${username.value}, 이메일: ${email.value}`)
}
</script>

<style scoped>
.find-wrapper {
  max-width: 400px;
  margin: 100px auto;
  padding: 40px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.find-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #111827;
  text-align: center;
}

.find-button {
  width: 100%;
  padding: 12px;
  background-color: #2563eb;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.find-button:hover {
  background-color: #1d4ed8;
}

.find-links {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.find-links a {
  color: #2563eb;
  text-decoration: none;
}

.find-links a:hover {
  text-decoration: underline;
}
</style>
