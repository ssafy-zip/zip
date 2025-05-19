<template>
  <section class="myInfo__content-container">
    <!-- 1. 개인 정보 카드 -->
    <article class="info-tile">
      <div class="info-tile__title-container">
        <div class="info-tile__title">개인 정보</div>
      </div>

      <div class="info-tile__seperator"></div>

      <div class="info-tile__content">
        <div class="tile-item-container" v-for="item in personalInfo" :key="item.label">
          <div class="tile-inner-wrapper">
            <div class="tile-label">{{ item.label }}</div>
            <div class="tile-value">{{ item.value || '-' }}</div>
          </div>
          <button v-if="item.action" class="tile-button">{{ item.action }}</button>
        </div>
      </div>
    </article>

    <!-- 2. 비밀번호와 보안 카드 -->
    <article class="info-tile">
      <div class="info-tile__title-container">
        <div class="info-tile__title">비밀번호와 보안</div>
      </div>

      <div class="info-tile__seperator"></div>

      <div class="info-tile__content">
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">비밀번호</div>
            <div class="tile-value">마지막 업데이트 2022. 9. 28.</div>
          </div>
          <button class="tile-button">업데이트</button>
        </div>
      </div>
    </article>

    <!-- 3. 계정 삭제 카드 -->
    <article class="info-tile danger-tile">
      <div class="info-tile__title-container">
        <div class="info-tile__title">계정 삭제</div>
        <button class="tile-button">계정 삭제 요청</button>
      </div>

      <div class="info-tile__seperator"></div>

      <div class="info-tile__content">
        <div class="tile-item-container">
          <div class="tile-subtitle">
            계정은 한 번 삭제하고 나면 다시 되돌릴 수 없습니다. 신중하게 진행해 주세요
          </div>
        </div>
      </div>
    </article>
  </section>
</template>

<script setup>
import { ref } from 'vue'

// 개인 정보
const user = ref({
  id: 'user123',
  name: '홍길동',
  email: 'gil@example.com',
  phone: '010-1234-5678',
})

const personalInfo = [
  { label: '아이디', value: user.value.id },
  { label: '이름', value: user.value.name, action: '이름 변경' },
  { label: '이메일 주소', value: user.value.email, action: '연동 메일 변경' },
  { label: '휴대폰 번호', value: user.value.phone, action: '연동 휴대폰 번호 변경' },
]
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
