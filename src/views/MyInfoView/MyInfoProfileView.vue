<template>
  <section class="myInfo__content-container">
    <!-- 1. 개인 정보 카드 -->
    <article class="info-tile">
      <div class="info-tile__title-container">
        <div class="info-tile__title">개인 정보</div>
      </div>

      <div class="info-tile__seperator"></div>

      <!--아이디-->
      <div class="info-tile__content">
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">아이디</div>
            <div class="tile-value">{{ user.id || '-' }}</div>
          </div>
        </div>
        <!--이름-->
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">이름</div>
            <div class="tile-value">{{ user.name || '-' }}</div>
          </div>
          <button class="tile-button" @click="showNameModal = true">이름 변경</button>
        </div>
        <!--이메일 주소-->
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">이메일 주소</div>
            <div class="tile-value">{{ user.email || '-' }}</div>
          </div>
          <button class="tile-button" @click="showEmailModal = true">연동 메일 변경</button>
        </div>
        <!--휴대폰 번호-->
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">휴대폰 번호</div>
            <div class="tile-value">{{ user.phone || '-' }}</div>
          </div>
          <button class="tile-button" @click="showPhoneModal = true">연동 휴대폰 변경</button>
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
          <button class="tile-button" @click="() => (showPasswordModal = true)">업데이트</button>
        </div>
      </div>
    </article>

    <!-- 3. 계정 삭제 카드 -->
    <article class="info-tile danger-tile">
      <div class="info-tile__title-container">
        <div class="info-tile__title">계정 삭제</div>
        <button class="tile-button" @click="showWithdrawModal = true">계정 삭제 요청</button>
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

    <InputNameModal :visible="showNameModal" @close="showNameModal = false" />
    <InputEmailModal :visible="showEmailModal" @close="showEmailModal = false" />
    <InputPhoneModal :visible="showPhoneModal" @close="showPhoneModal = false" />
    <InputPasswordModal :visible="showPasswordModal" @close="showPasswordModal = false" />
    <InputWithdrawModal :visible="showWithdrawModal" @close="showWithdrawModal = false" />
  </section>
</template>

<script setup>
import { ref, reactive } from 'vue'
import InputNameModal from '@/components/InputModal/InputNameModal.vue'
import InputEmailModal from '@/components/InputModal/InputEmailModal.vue'
import InputPhoneModal from '@/components/InputModal/InputPhoneModal.vue'
import InputPasswordModal from '@/components/InputModal/InputPasswordModal.vue'
import InputWithdrawModal from '@/components/InputModal/InputWithdrawModal.vue'

const showNameModal = ref(false)
const showEmailModal = ref(false)
const showPhoneModal = ref(false)
const showPasswordModal = ref(false)
const showWithdrawModal = ref(false)

// 개인 정보
const user = reactive({
  id: 'user123',
  name: '홍길동',
  email: 'gil@example.com',
  phone: '010-1234-5678',
})
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css');

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
