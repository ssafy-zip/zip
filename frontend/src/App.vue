<template>
  <div class="app">
    <AppHeader />
    <button @click="goToChat" class="chat-btn">🤖 Chatbot</button>
    <main class="main-container">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
function goToChat() {
  router.push({ name: 'Chat' })
}
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: Arial, sans-serif;
  font-size: 100%;
  background-color: #f9fafb;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>
