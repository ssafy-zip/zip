<template>
  <div class="app">
    <AppHeader />
    <main class="main-container">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: Arial, sans-serif;
  font-size: 100%;
  background-color: f9fafb;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-container {
  flex-grow: 1;
}
</style>
