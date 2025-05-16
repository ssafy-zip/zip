<template>
  <header class="header">
    <!-- 상단: 로그인/회원가입 or 내 정보/로그아웃 -->
    <div class="header-top">
      <div class="header-top-content">
        <ul class="auth-list">
          <template v-if="!isLoggedIn">
            <li class="auth-item">
              <router-link to="/login">로그인</router-link>
            </li>
            <li class="auth-item">
              <router-link to="/join">회원가입</router-link>
            </li>
          </template>
          <template v-else>
            <li class="auth-item">
              <router-link to="/profile" class="my-info">내 정보</router-link>
            </li>
            <li class="auth-item">
              <button class="logout-button" @click="handleLogout">로그아웃</button>
            </li>
          </template>
        </ul>
      </div>
    </div>

    <!-- 중앙: 로고 및 네비게이션 -->
    <div class="header-center">
      <div class="header-left">
        <router-link to="/">
          <img src="/image/logo/logo-white-small.png" alt="로고" class="header-logo" />
        </router-link>
      </div>
      <nav class="header-nav">
        <ul class="nav-list">
          <li class="nav-item"><router-link to="/houseMap">매물</router-link></li>
          <li class="nav-item"><router-link to="/news">뉴스</router-link></li>
          <li class="nav-item"><router-link to="/bbs">커뮤니티</router-link></li>
        </ul>
      </nav>
      <div class="header-right"></div>
    </div>
  </header>
</template>

<script setup>
import { computed, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'

defineOptions({ name: 'AppHeader' })

const router = useRouter()
const { proxy } = getCurrentInstance()

// 전역 프로퍼티 $auth 에 저장된 token 값을 즉시 읽어옵니다.
const isLoggedIn = computed(() => !!proxy.$auth.token)

function handleLogout() {
  proxy.$logout()
  router.push('/login')
}
</script>

<style scoped>
.header {
  display: flex;
  flex-direction: column;
  justify-content: center;
  background-color: #111827;
  color: white;
}

.header-top-content,
.header-center {
  min-width: 400px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
}

.header-top {
  background-color: #1f2937;
  padding-right: 50px;
}

.header-top-content {
  display: flex;
  flex-direction: row-reverse;
}

.auth-list {
  list-style: none;
  display: flex;
  gap: 14px;
  margin: 0;
  padding: 0;
  font-size: 14px;
  align-items: center;
  height: 40px;
}

.auth-item a,
.my-info,
.logout-button {
  color: white;
  background: none;
  border: none;
  text-decoration: none;
  font-size: 14px;
  padding: 4px 6px;
  border-radius: 4px;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    color 0.2s ease;
}

.auth-item a:hover,
.logout-button:hover {
  background-color: #374151;
  color: #60a5fa;
}

.auth-item a:active,
.logout-button:active {
  background-color: #2563eb;
  color: white;
}

.header-center {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.header-nav {
  display: flex;
  justify-content: center;
}

.nav-list {
  list-style: none;
  display: flex;
  gap: 24px;
  margin: 0;
  padding: 0;
}

.nav-item a {
  color: white;
  text-decoration: none;
  font-size: 24px;
  font-weight: bold;
  padding: 6px 8px;
  border-radius: 4px;
  transition:
    background-color 0.2s ease,
    color 0.2s ease;
}

.nav-item a:hover {
  background-color: #374151;
  color: #60a5fa;
}

.nav-item a:active {
  background-color: #2563eb;
  color: white;
}
</style>
