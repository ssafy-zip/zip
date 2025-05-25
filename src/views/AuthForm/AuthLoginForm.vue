<template>
  <AuthFormLayout>
    <!-- 폼 -->
    <form class="form-box" @submit.prevent="handleSubmit">
      <!-- 아이디 -->
      <BaseInput
        id="username"
        v-model="username"
        icon="fa-solid fa-user fa-2x"
        placeholder="아이디"
        :clearable="true"
        autofocus
      />

      <!-- 비밀번호 -->
      <BaseInput
        id="password"
        v-model="password"
        icon="fa-solid fa-lock fa-2x"
        placeholder="비밀번호"
        :type="showPassword ? 'text' : 'password'"
      />

      <!-- 오류 메시지 -->
      <p v-if="errorMessage" class="form-error">
        {{ errorMessage }}
      </p>

      <!-- 아이디 저장 -->
      <div class="form-options">
        <label class="form-option">
          <input type="checkbox" v-model="rememberId" />
          아이디 저장
        </label>
      </div>

      <!-- 로그인 버튼 -->
      <button type="submit" class="form-submit-button" :disabled="!canSubmit">로그인</button>

      <div class="form-separator"></div>

      <!-- 소셜 로그인 -->
      <div class="social-buttons">
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
      <div class="link-group">
        <router-link :to="{ name: 'FindId' }">아이디 찾기</router-link>
        <router-link :to="{ name: 'FindPassword' }">비밀번호 재설정</router-link>
        <router-link :to="{ name: 'Join' }">회원가입</router-link>
      </div>
    </form>
  </AuthFormLayout>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import AuthFormLayout from './AuthFormLayout.vue'
import { ERROR_MESSAGES } from '@/constants/error'
import BaseInput from '@/components/BaseInput.vue'

const router = useRouter()
const { proxy } = getCurrentInstance()

const username = ref('')
const password = ref('')
const rememberId = ref(false)
const showPassword = ref(false)
const errorMessage = ref('')

/* 제출 버튼 상태 추적 */
const canSubmit = computed(() => {
  return username.value.trim() !== '' && password.value.trim() !== ''
})

/* 입력 폼 액션 */
async function handleSubmit() {
  errorMessage.value = ''
  if (!username.value || !password.value) {
    errorMessage.value = ERROR_MESSAGES.LOGIN_FAILED
    return
  }

  try {
    // 1) 로그인 API 호출
    const { data } = await axios.post('/api/users/login', {
      userId: username.value,
      password: password.value,
    })
    const { token, role, userId: serverUserId } = data

    // 2) 전역 상태에 토큰 저장
    proxy.$setAuthToken(token)

    // 3) 로컬스토리지에 토큰·롤·서버 userId 저장
    localStorage.setItem('authToken', token)
    localStorage.setItem('userRole', role)
    localStorage.setItem('savedUserId', serverUserId)

    // 4) “아이디 저장” 체크박스는 별도의 키로
    if (rememberId.value) {
      localStorage.setItem('rememberedUsername', username.value)
    } else {
      localStorage.removeItem('rememberedUsername')
    }

    // 5) 홈으로 이동
    router.push({ name: 'Home' })
  } catch (err) {
    console.error(err)
    if ([401, 403].includes(err.response?.status)) {
      errorMessage.value = ERROR_MESSAGES.LOGIN_FAILED
    } else {
      errorMessage.value = ERROR_MESSAGES.SERVER_ERROR
    }
  }
}

onMounted(() => {
  const saved = localStorage.getItem('savedUserId')
  if (saved) {
    username.value = saved
    rememberId.value = true
  }
})
</script>

<style scoped src="@/assets/css/authForm.css"></style>
<style scoped>
/* 소셜 로그인 버튼 */
.social-buttons {
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
