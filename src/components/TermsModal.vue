<template>
  <div class="g-modal" :class="{ hidden: !visible }">
    <div class="g-modal__content" role="dialog" aria-modal="true" tabindex="-1">
      <!-- 헤더 -->
      <div class="g-modal__header">
        <div class="g-modal__title">{{ title }}</div>
        <button class="g-modal__close" @click="$emit('close')" aria-label="닫기">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <!-- 본문 -->
      <div class="g-modal__body" v-html="content"></div>

      <!-- 푸터 -->
      <div class="g-modal__footer"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: Boolean,
  title: {
    type: String,
    default: '약관',
  },
  content: {
    type: String,
    default: '',
  },
})
</script>

<style scoped>
.g-modal.hidden {
  display: none;
}

.g-modal {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.g-modal__content {
  background-color: white;
  padding: 0;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.g-modal__header {
  flex-shrink: 0;
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 1;
  padding: 16px 24px 12px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.g-modal__title {
  font-size: 16px;
  font-weight: bold;
}

.g-modal__close {
  font-size: 20px;
  background: none;
  border: none;
  cursor: pointer;
  color: #374151;
}

.g-modal__body {
  padding: 16px 24px;
  overflow-y: auto;
  flex: 1;
  font-size: 14px;
  color: #374151;
}

.g-modal__footer {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  text-align: center;
  flex-shrink: 0;
}
</style>
