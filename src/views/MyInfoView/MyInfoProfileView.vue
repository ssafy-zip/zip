<template>
  <section class="myInfo__content-container">
    <!-- 1. 개인 정보 카드 -->
    <article class="info-tile">
      <div class="info-tile__title-container">
        <div class="info-tile__title">개인 정보</div>
        <button class="tile-button" @click="showProfileModal = true">정보 수정</button>
      </div>
      <div class="info-tile__seperator"></div>
      <div class="info-tile__content">
        <!-- 아이디 -->
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">아이디</div>
            <div class="tile-value">{{ user.id || '-' }}</div>
          </div>
        </div>
        <!-- 이름 -->
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">이름</div>
            <div class="tile-value">{{ user.name || '-' }}</div>
          </div>
        </div>
        <!-- 이메일 -->
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">이메일 주소</div>
            <div class="tile-value">{{ user.email || '-' }}</div>
          </div>
        </div>
        <!-- 휴대폰 -->
        <div class="tile-item-container">
          <div class="tile-inner-wrapper">
            <div class="tile-label">휴대폰 번호</div>
            <div class="tile-value">{{ user.phone || '-' }}</div>
          </div>
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
            <div class="tile-value">마지막 업데이트 {{ lastPasswordUpdate }}</div>
          </div>
          <button class="tile-button" @click="showPasswordModal = true">업데이트</button>
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

    <!-- 모달 컴포넌트 -->
    <InputProfileModal
      :visible="showProfileModal"
      :initial="user"
      @close="showProfileModal = false"
      @saved="onProfileSaved"
    />
    <InputPasswordModal :visible="showPasswordModal" @close="showPasswordModal = false" />
    <InputWithdrawModal :visible="showWithdrawModal" @close="showWithdrawModal = false" />
  </section>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

import InputProfileModal from '@/components/InputModal/InputProfileModal.vue'
import InputPasswordModal from '@/components/InputModal/InputPasswordModal.vue'
import InputWithdrawModal from '@/components/InputModal/InputWithdrawModal.vue'

const showProfileModal = ref(false)
const showPasswordModal = ref(false)
const showWithdrawModal = ref(false)

// 사용자 정보
const user = reactive({
  id: '',
  name: '',
  email: '',
  phone: '',
})
// 비밀번호 마지막 업데이트 날짜
const lastPasswordUpdate = ref('2022. 9. 28')

// 컴포넌트가 마운트되면 API 호출
onMounted(async () => {
  try {
    // 로컬스토리지에서 JWT 꺼내기
    const token = localStorage.getItem('authToken')
    // getUserInfo 호출 (헤더에 Bearer 토큰 포함)
    const { data } = await axios.get('/api/users/getUserInfo', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    // 응답값 화면에 반영
    user.id = data.id
    user.name = data.name
    user.email = data.email
    user.phone = data.phone
  } catch (err) {
    console.error('유저 정보 로딩 실패', err)
  }
})

// 프로필 수정 후 실행
function onProfileSaved(updated) {
  user.name = updated.name
  user.email = updated.email
  user.phone = updated.phone
  showProfileModal.value = false
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css');

.myInfo__content-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
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
.danger-tile {
  border-color: #fecaca;
  background-color: #fef2f2;
  color: #991b1b;
}
</style>
