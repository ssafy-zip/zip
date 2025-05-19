<template>
  <section class="myInfo__content-container">
    <div class="info-tile">
      <div class="info-tile__title-container">
        <div class="info-tile__title">관심 매물</div>
      </div>
      <div class="info-tile__seperator"></div>
      <div class="info-tile__content">
        <div class="tile-item-container" v-for="(apt, index) in interests" :key="index">
          <div class="tile-inner-wrapper">
            <div class="tile-label">매물 {{ index + 1 }}</div>
            <div class="tile-value">{{ apt }}</div>
          </div>
        </div>
        <div class="tile-action">
          <a href="#" class="tile-link">전체 보기</a>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
// 관심 매물
const interests = ref(['서초동 삼성아파트 84㎡', '신림동 한라아파트 59㎡'])
</script>

<style scoped>
/* 콘텐츠 영역 */
.myInfo__content-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 공통 카드 */
.info-tile {
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 0 24px 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.info-tile__title-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

.info-tile__title {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
}

.info-tile__seperator {
  height: 1px;
  background-color: #e5e7eb;
  margin-bottom: 16px;
}

.info-tile__content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 항목 한 줄 */
.tile-item-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tile-inner-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tile-label {
  font-size: 14px;
  color: #6b7280;
}

.tile-value {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
}

.tile-button {
  color: #2563eb;
  font-size: 14px;
  background: none;
  border: none;
  cursor: pointer;
}
.tile-button:hover {
  text-decoration: underline;
}

.tile-link {
  color: #2563eb;
  font-size: 14px;
  text-decoration: none;
}

.tile-link:hover {
  text-decoration: underline;
}

.tile-action {
  margin-top: 16px;
  text-align: right;
}

/* 계정 삭제 경고 */
.danger-tile {
  border-color: #fecaca;
  background-color: #fef2f2;
  color: #991b1b;
}
</style>
