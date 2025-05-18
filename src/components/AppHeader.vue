<template>
  <header class="app-header">
    <!-- 상단: 로그인/회원가입 or 내 정보/로그아웃 -->
    <section class="app-header__top">
      <div class="app-header__top-container">
        <ul class="app-header__auth-menu">
          <template v-if="!isLoggedIn">
            <li class="app-header__auth-item">
              <router-link to="/login">로그인</router-link>
            </li>
            <li class="app-header__auth-item">
              <router-link to="/join">회원가입</router-link>
            </li>
          </template>
          <template v-else>
            <li class="app-header__auth-item">
              <router-link to="/myInfo">내 정보</router-link>
            </li>
            <li class="app-header__auth-item">
              <a @click.prevent="handleLogout">로그아웃</a>
            </li>
          </template>
        </ul>
      </div>
    </section>

    <!-- 중앙: 로고 및 네비게이션 -->
    <section class="app-header__center">
      <article class="app-header__left">
        <router-link to="/">
          <img src="/logo/logo-dark-64.png" alt="로고" class="app-header__logo" />
        </router-link>
      </article>
      <nav class="app-header__nav">
        <ul class="app-header__nav-menu">
          <li class="app-header__nav-item"><router-link to="/houseMap">매물</router-link></li>
          <li class="app-header__nav-item"><router-link to="/news">뉴스</router-link></li>
          <li class="app-header__nav-item"><router-link to="/bbs">커뮤니티</router-link></li>
        </ul>
      </nav>
      <div class="app-header__right"></div>
    </section>
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
.app-header {
  display: flex;
  flex-direction: column;
  justify-content: center;
  background-color: #111827;
  color: white;
}

.app-header__top {
  background-color: #1f2937;
}

.app-header__top-container,
.app-header__center {
  min-width: 400px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
}

.app-header__top-container {
  display: flex;
  flex-direction: row-reverse;
  box-sizing: border-box;
  padding-right: 50px;
}

.app-header__auth-menu {
  list-style: none;
  display: flex;
  gap: 14px;
  margin: 0;
  padding: 0;
  font-size: 14px;
  align-items: center;
  height: 40px;
}

.app-header__auth-item a {
  color: white;
  background: none;
  border: none;
  text-decoration: none;
  font-size: 14px;
  padding: 4px 6px;
  cursor: pointer;
  transition: color 0.2s ease;
}

.app-header__auth-item a:hover {
  text-decoration: underline;
  color: #60a5fa;
}

.app-header__center {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-header__nav {
  display: flex;
  justify-content: center;
}

.app-header__nav-menu {
  list-style: none;
  display: flex;
  gap: 24px;
  margin: 0;
  padding: 0;
}

.app-header__nav-item a {
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

.app-header__nav-item a:hover {
  background-color: #374151;
  color: #60a5fa;
}

.app-header__nav-item a:active {
  background-color: #1f2937;
  color: #3b82f6;
}
</style>
