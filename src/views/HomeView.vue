<template>
  <AppHeader />

  <main class="home-content">
    <h2 class="home-title">🏠 서비스에 오신 것을 환영합니다!</h2>
    <p class="home-text">
      이곳은 부동산 지도와 커뮤니티 게시판 기능을 제공합니다. 상단의 네비게이션 메뉴를 이용해 다양한
      서비스를 이용해 보세요.
    </p>
  </main>

  <AppFooter />
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

defineOptions({ name: 'HomeView' })
</script>

<style scoped>
.home-content {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
  text-align: center;
}
.home-title {
  font-size: 2rem;
  margin-bottom: 16px;
}
.home-text {
  font-size: 1.125rem;
  line-height: 1.6;
  color: #555;
}
</style>
