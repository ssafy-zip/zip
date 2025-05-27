<template>
  <AuthFormLayout>
    <form class="form-box" @submit.prevent="handleJoin">
      <h2 class="form-title">회원가입</h2>

      <!-- 아이디 -->
      <BaseInput
        id="userId"
        v-model="form.userId"
        icon="fas fa-user fa-2x"
        placeholder="아이디"
        :clearable="true"
        autofocus
      />

      <!-- 비밀번호 -->
      <BaseInput
        id="password"
        v-model="form.password"
        icon="fas fa-lock fa-2x"
        placeholder="비밀번호"
        type="password"
        :errorMessage="form.password && !validPassword ? ERROR_MESSAGES.INVALID_PASSWORD : ''"
        :valid="form.password.length > 0 && validPassword"
      />

      <!-- 비밀번호 확인 -->
      <BaseInput
        id="confirmPassword"
        v-model="form.confirmPassword"
        icon="fas fa-key fa-2x"
        placeholder="비밀번호 재입력"
        type="password"
        :errorMessage="
          form.confirmPassword && !passwordsMatch ? ERROR_MESSAGES.PASSWORD_MISMATCH : ''
        "
        :valid="form.confirmPassword.length > 0 && passwordsMatch"
      />

      <!-- 이름 -->
      <BaseInput
        id="name"
        v-model="form.name"
        icon="fas fa-id-card fa-2x"
        placeholder="이름"
        :clearable="true"
      />

      <!-- 이메일 -->
      <BaseInput
        id="email"
        v-model="form.email"
        icon="fas fa-envelope fa-2x"
        placeholder="이메일"
        :clearable="true"
        :errorMessage="form.email && !validEmail ? ERROR_MESSAGES.INVALID_EMAIL : ''"
        :valid="form.email.length > 0 && validEmail"
      />

      <!-- 전화번호 -->
      <BaseInput
        id="phone"
        v-model="form.phone"
        icon="fas fa-phone fa-2x"
        placeholder="전화번호"
        inputmode="numeric"
        :clearable="true"
        :errorMessage="form.phone && !validPhone ? ERROR_MESSAGES.INVALID_PHONE : ''"
        :valid="form.phone.length > 0 && validPhone"
      />

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
          <button type="button" class="terms-button" @click="open('service')">약관 보기</button>
        </div>
        <div class="agree-item">
          <label class="agree-label">
            <input type="checkbox" v-model="form.agreePrivacy" />
            (필수) 개인정보 수집 및 이용 동의
          </label>
          <button type="button" class="terms-button" @click="open('privacy')">약관 보기</button>
        </div>
      </div>

      <!-- 가입하기 버튼: 비밀번호 매칭 안 되면 비활성화 -->
      <button type="submit" class="form-submit-button" :disabled="!canSubmit">가입하기</button>

      <div class="link-group">
        <router-link :to="{ name: 'Login' }"> 이미 계정이 있으신가요? 로그인 </router-link>
      </div>
    </form>
    <TermsModal :visible="show" :title="title" :content="content" @close="close" />
  </AuthFormLayout>
  <!-- 약관 모달 -->
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import baseURL from '@/baseURL'
import { ERROR_MESSAGES } from '@/constants/error'
import AuthFormLayout from './AuthFormLayout.vue'
import BaseInput from '@/components/BaseInput.vue'

import TermsModal from '@/components/TermsModal.vue'
import { useTermsModal } from '@/utils/useTermModal'
const { show, title, content, open, close } = useTermsModal()

import { useValidation } from '@/utils/useValidation'
const { isValidPassword, isValidEmail, isValidPhone } = useValidation()

const router = useRouter()

const form = reactive({
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

const agreeAll = computed(() => form.agreePrivacy && form.agreeService)

// 비밀번호 일치 여부 계산
const passwordsMatch = computed(
  () => form.confirmPassword.length > 0 && form.password === form.confirmPassword,
)
const validPassword = computed(() => isValidPassword(form.password))
const validEmail = computed(() => isValidEmail(form.email))
const validPhone = computed(() => isValidPhone(form.phone))

const isFormValid = computed(() => {
  // eslint-disable-next-line no-unused-vars
  const { role, ...rest } = form
  return Object.values(rest).every((value) => !!value)
})

const canSubmit = computed(
  () => isFormValid.value && validEmail.value && validPhone.value && validPassword.value,
)

// 전체 동의 체크
function checkAll() {
  if (agreeAll.value) {
    form.agreeService = false
    form.agreePrivacy = false
  } else {
    form.agreeService = true
    form.agreePrivacy = true
  }
}

// 회원가입 처리
async function handleJoin() {
  if (!passwordsMatch.value) {
    alert(ERROR_MESSAGES.PASSWORD_MISMATCH)
    return
  }
  if (!agreeAll.value) {
    alert(ERROR_MESSAGES.AGREEMENT_REQUIRED)
    return
  }

  try {
    await baseURL.post('/api/users', {
      userId: form.userId,
      password: form.password,
      name: form.name,
      email: form.email,
      phone: form.phone,
      role: form.role,
    })
    alert('회원가입이 완료되었습니다!')
    router.push({ name: 'Login' })
  } catch (err) {
    console.error(err)
    alert('회원가입 중 오류가 발생했습니다.')
  }
}
</script>

<style scoped src="@/assets/css/authForm.css"></style>
<style scoped>
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
  cursor: pointer;
  font-size: 14px;
  padding: 0;
}

.terms-button:hover {
  text-decoration: underline;
  color: #1d4ed8;
}
</style>
