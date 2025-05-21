<template>
  <main class="home-content">
    <h2 class="home-title">🏠 서비스에 오신 것을 환영합니다!</h2>
    <p class="home-text">
      이곳은 부동산 지도와 커뮤니티 게시판 기능을 제공합니다. 상단의 네비게이션 메뉴를 이용해 다양한
      서비스를 이용해 보세요.
    </p>

    <!-- 오늘의 뉴스 요약 섹션 -->
    <section class="news-summary">
      <h3 class="summary-title">📰 오늘의 뉴스 요약</h3>

      <!-- 리스트 형태로 깔끔하게 렌더링 -->
      <ol v-if="!loading && !error" class="summary-list">
        <li v-for="(item, idx) in summaryItems" :key="idx" v-html="item"></li>
      </ol>

      <!-- 로딩 중 및 오류 메시지 -->
      <p v-if="loading" class="summary-text">뉴스 요약을 불러오는 중입니다...</p>
      <p v-if="error" class="summary-text error">뉴스 요약 로드 실패: {{ error }}</p>
    </section>
  </main>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

defineOptions({ name: 'HomeView' })

const summary = ref('')
const loading = ref(true)
const error = ref('')

// AI가 반환한 HTML 요약을 파싱하여 목록으로 변환
const summaryItems = computed(() => {
  if (!summary.value) return []
  try {
    const parser = new DOMParser()
    const doc = parser.parseFromString(summary.value, 'text/html')
    const titles = Array.from(doc.querySelectorAll('h3'))
    return titles.map((h3) => {
      const link = h3.querySelector('a')
      const titleHtml = link
        ? `<a href="${link.href}" target="_blank">${link.textContent}</a>`
        : h3.textContent
      const p = h3.nextElementSibling
      const desc = p ? p.textContent : ''
      return `${titleHtml}<p>${desc}</p>`
    })
  } catch (e) {
    console.error('summaryItems 파싱 오류', e)
    return []
  }
})

onMounted(async () => {
  try {
    const response = await axios.get('/api/chat/newsSummation')
    summary.value = response.data.summary
  } catch (e) {
    console.error('뉴스 요약 로드 실패', e)
    error.value = '데이터를 가져오는 중 문제가 발생했습니다.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home-content {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
  text-align: center;
}
.home-title {
  font-size: 2rem;
  margin-bottom: 16px;
}
.home-text {
  font-size: 1.125rem;
  line-height: 1.6;
  color: #555;
  margin-bottom: 32px;
}
.news-summary {
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 24px;
  text-align: left;
}
.summary-title {
  font-size: 1.5rem;
  margin-bottom: 16px;
}
.summary-list {
  list-style: decimal inside;
  padding-left: 0;
  margin: 0 0 16px 0;
}
.summary-list li {
  margin-bottom: 12px;
  font-size: 1rem;
  line-height: 1.6;
  color: #333;
}
.summary-text {
  font-size: 1rem;
  line-height: 1.6;
  color: #333;
}
.error {
  color: #dc2626;
}
</style>
