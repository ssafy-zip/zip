<template>
  <div class="myInfo">
    <div class="myInfo__container">
      <!-- 사이드바 -->
      <aside class="myInfo__sidebar">
        <ul>
          <li :class="{ active: selectedTab === 'profile' }" @click="selectedTab = 'profile'">
            <i class="fas fa-user"></i> 내 정보
          </li>
          <li :class="{ active: selectedTab === 'favorites' }" @click="selectedTab = 'favorites'">
            <i class="fas fa-heart"></i> 관심 매물
          </li>
          <li :class="{ active: selectedTab === 'posts' }" @click="selectedTab = 'posts'">
            <i class="fas fa-pen"></i> 작성 글
          </li>
          <li :class="{ active: selectedTab === 'comments' }" @click="selectedTab = 'comments'">
            <i class="fas fa-comment-dots"></i> 작성 댓글
          </li>
        </ul>
      </aside>

      <!-- 콘텐츠 -->
      <section class="myInfo__content">
        <!-- 개인 정보 섹션 -->
        <!-- 개인정보 + 보안 + 계정 삭제 섹션 -->
        <div v-if="selectedTab === 'profile'" class="myInfo-profile">
          <!-- 1. 개인 정보 카드 -->
          <div class="info-tile">
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
                <a href="#" class="tile-link">{{ item.action }}</a>
              </div>
            </div>
          </div>

          <!-- 2. 비밀번호와 보안 카드 -->
          <div class="info-tile">
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
                <a href="#" class="tile-link">업데이트</a>
              </div>
            </div>
          </div>

          <!-- 3. 계정 삭제 카드 -->
          <div class="info-tile danger-tile">
            <div class="info-tile__title-container">
              <div class="info-tile__title">계정 삭제</div>
              <a href="#" class="tile-link">계정 삭제 요청</a>
            </div>
            <div class="info-tile__seperator"></div>
            <div class="info-tile__content">
              <div class="tile-item-container">
                <div class="tile-subtitle">
                  계정은 한 번 삭제하고 나면 다시 되돌릴 수 없습니다. 신중하게 진행해 주세요
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 관심 매물 -->
        <div v-if="selectedTab === 'favorites'" class="info-tile">
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

        <!-- 내가 작성한 글 -->
        <div v-if="selectedTab === 'posts'" class="info-tile">
          <div class="info-tile__title-container">
            <div class="info-tile__title">내가 작성한 글</div>
          </div>
          <div class="info-tile__seperator"></div>
          <div class="info-tile__content">
            <div class="tile-item-container" v-for="(post, index) in posts" :key="index">
              <div class="tile-inner-wrapper">
                <div class="tile-label">게시글 {{ index + 1 }}</div>
                <div class="tile-action">
                  <a :href="post.link" class="tile-link">{{ post.title }}</a>
                </div>
              </div>
            </div>
            <div class="tile-action">
              <a href="#" class="tile-link">전체 보기</a>
            </div>
          </div>
        </div>

        <!-- 내가 작성한 댓글 -->
        <div v-if="selectedTab === 'comments'" class="info-tile">
          <div class="info-tile__title-container">
            <div class="info-tile__title">내가 작성한 댓글</div>
          </div>
          <div class="info-tile__seperator"></div>
          <div class="info-tile__content">
            <div class="tile-item-container" v-for="(comment, index) in comments" :key="index">
              <div class="tile-inner-wrapper">
                <div class="tile-label">댓글 {{ index + 1 }}</div>
                <div class="tile-action">
                  <a :href="comment.link" class="tile-link">{{ comment.content }}</a>
                </div>
              </div>
            </div>
            <div class="tile-action">
              <a href="#" class="tile-link">전체 보기</a>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const selectedTab = ref('profile')

const user = {
  id: 'user123',
  name: '홍길동',
  email: 'gil@example.com',
  phone: '010-1234-5678',
}
const personalInfo = [
  { label: '아이디', value: user.id, action: '아이디 설정' },
  { label: '이메일 주소', value: user.email, action: '연동 메일 변경' },
  { label: '휴대폰 번호', value: user.phone, action: '휴대폰 번호 연동' },
  { label: '국가/지역', value: 'KR', action: '국가/지역 변경' },
]

const interests = ['서초동 삼성아파트 84㎡', '신림동 한라아파트 59㎡']

const posts = [
  { title: '전세 사기 어떻게 피하나요?', link: '#' },
  { title: '서울 추천 지역 알려주세요!', link: '#' },
]

const comments = [
  { content: '좋은 정보 감사합니다!', link: '#' },
  { content: '이 지역은 정말 살기 좋아요.', link: '#' },
]
</script>

<style scoped>
.myInfo__container {
  display: flex;
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
  gap: 20px;
}

/* 사이드바 */
.myInfo__sidebar {
  width: 220px;
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  height: fit-content;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.myInfo__sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.myInfo__sidebar li {
  padding: 10px 12px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.myInfo__sidebar li:hover {
  background-color: #f3f4f6;
}

.myInfo__sidebar li.active {
  background-color: #2563eb;
  color: white;
}

/* 콘텐츠 영역 */
.myInfo__content {
  flex: 1;
}

.myInfo__card {
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.myInfo__card h3 {
  font-size: 20px;
  margin-bottom: 16px;
  color: #1f2937;
}

.myInfo-info,
.myInfo__list {
  list-style: none;
  padding: 0;
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #374151;
}

.myInfo-info li,
.myInfo__list li {
  margin-bottom: 6px;
}

.myInfo__list li a {
  color: #2563eb;
  text-decoration: none;
}

.myInfo__list li a:hover {
  text-decoration: underline;
}

.myInfo__button {
  background-color: #2563eb;
  color: white;
  padding: 10px 16px;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.myInfo__button:hover {
  background-color: #1d4ed8;
}

.myInfo-profile {
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

.tile-link {
  color: #2563eb;
  font-size: 14px;
  text-decoration: none;
}

.tile-link:hover {
  text-decoration: underline;
}

.tile-subtitle {
  color: #991b1b;
  font-size: 14px;
}

.tile-action {
  margin-top: 16px;
  text-align: right;
}

/* 계정 삭제 경고 */
.danger-tile {
  border-color: #fecaca;
  background-color: #fef2f2;
}
</style>
