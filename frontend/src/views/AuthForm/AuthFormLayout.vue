<template>
  <section class="form-container">
    <!-- 헤더 -->
    <div class="form-header">
      <router-link to="/">
        <img src="/logo/logo-light-128.png" class="form-logo" />
      </router-link>
    </div>
    <!--인증 폼-->
    <slot></slot>
  </section>
</template>

<script setup></script>

<style scoped src="@/assets/css/authForm.css"></style>
