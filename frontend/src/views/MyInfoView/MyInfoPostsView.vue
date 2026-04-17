<template>
  <section class="myInfo__content-container">
    <!-- 카테고리별 카드 -->
    <div class="info-tile" v-for="(catBlock, idx) in filteredCategories" :key="idx">
      <!-- 카드 헤더: 카테고리 이름 -->
      <div class="info-tile__title-container">
        <div class="info-tile__title">
          {{ categoryLabels[catBlock.category] || catBlock.category }}
        </div>
      </div>
      <div class="info-tile__seperator"></div>

      <!-- 카드 콘텐츠: 게시글 리스트 -->
      <div class="info-tile__content">
        <div class="tile-item-container" v-for="article in catBlock.articles" :key="article.id">
          <div class="tile-inner-wrapper">
            <div class="tile-label">
              {{ formatDate(article.createdAt) }}
            </div>
            <!-- BbsDetailView로 연결: 라우터 이름 'BbsDetail', props:id -->
            <router-link
              :to="{ name: 'BbsDetail', params: { id: article.id } }"
              class="tile-value tile-link"
            >
              {{ article.title }}
            </router-link>
          </div>
        </div>

        <!-- 전체 보기 링크 -->
        <div class="tile-action">
          <router-link
            :to="{ path: '/myPage/posts', query: { category: catBlock.category } }"
            class="tile-link"
          >
            전체 보기
          </router-link>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import baseURL from '@/baseURL'

// 인증 토큰 가져오기
const { proxy } = getCurrentInstance()

// 전체 카테고리별 게시글 블록
const categories = ref([])

// PostType 코드 → 한글 레이블
const categoryLabels = {
  NOTICE: '공지',
  INFO: '정보',
  QUESTION: '질문',
  PROMOTION: '홍보',
  CHAT: '잡담',
}

// 글이 하나 이상인 카테고리만 보여주는 계산 속성
const filteredCategories = computed(() =>
  categories.value.filter((block) => Array.isArray(block.articles) && block.articles.length > 0),
)

// ISO 날짜 문자열 → "YYYY.MM.DD" 포맷
function formatDate(iso) {
  const d = new Date(iso)
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}.${mm}.${dd}`
}

// 마운트 시 서버에서 데이터 로드
onMounted(async () => {
  try {
    const { data } = await baseURL.get('/api/users/myPage/myArticleListsByCategory', {
      headers: {
        Authorization: `Bearer ${proxy.$auth.token}`,
      },
    })
    categories.value = data
  } catch (err) {
    console.error('내 게시글 불러오기 실패:', err)
  }
})
</script>

<style scoped>
.myInfo__content-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  width: 100%;
}

.info-tile {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 0 24px 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.info-tile__title-container {
  display: flex;
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
  background: #e5e7eb;
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

.tile-action {
  margin-top: 16px;
  text-align: right;
}
.tile-link {
  color: #2563eb;
  font-size: 14px;
  text-decoration: none;
}
.tile-link:hover {
  text-decoration: underline;
}
</style>
