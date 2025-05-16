<template>
  <div class="bbs-wrapper">
    <!-- 헤더 -->
    <div class="bbs-header">
      <div class="bbs-header-top">
        <h2 class="bbs-header-title">커뮤니티</h2>
      </div>

      <div class="bbs-header-center">
        <!-- 탭 -->
        <div class="bbs-tabs">
          <button
            v-for="tab in tabs"
            :key="tab"
            :class="['bbs-tab', { active: currentTab === tab }]"
            @click="selectTab(tab)"
          >
            {{ tab }}
          </button>
        </div>

        <!-- 정렬 + 글쓰기 -->
        <div class="bbs-misc">
          <select class="bbs-sort-select" v-model="sort" @change="fetchPosts">
            <option value="latest">최신순</option>
            <option value="oldest">오래된순</option>
            <option value="views">조회순</option>
            <option value="likes">추천순</option>
            <option value="comments">댓글순</option>
          </select>
          <button class="bbs-write-button" @click="handleWrite">글쓰기</button>
        </div>
      </div>
    </div>

    <!-- 게시글 테이블 -->
    <div class="bbs-table">
      <table>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
            <th>추천</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in posts" :key="post.id">
            <td>{{ post.id }}</td>
            <td class="bbs-record-title">
              <!-- 상세 페이지로 이동하도록 경로 수정 -->
              <router-link :to="`/bbs/${post.id}`">{{ post.title }}</router-link>
            </td>
            <td>{{ post.writer }}</td>
            <td>{{ post.date }}</td>
            <td>{{ post.views }}</td>
            <td>{{ post.likes }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 검색 -->
    <div class="bbs-search">
      <select class="bbs-search-option" v-model="searchOption">
        <option value="all">전체</option>
        <option value="title">제목</option>
        <option value="content">내용</option>
        <option value="writer">작성자</option>
      </select>
      <input type="text" class="bbs-search-input" v-model="searchQuery" placeholder="검색어 입력" />
      <button class="bbs-search-button" @click="searchPosts">
        <i class="fa fa-search"></i>
      </button>
    </div>

    <!-- 페이지네이션 -->
    <div class="bbs-pagination">
      <button class="page-button" @click="prevPage">«</button>
      <button
        v-for="page in totalPages"
        :key="page"
        :class="['page-button', { active: currentPage === page }]"
        @click="changePage(page)"
      >
        {{ page + 1 }}
      </button>
      <button class="page-button" @click="nextPage">»</button>
    </div>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

// 전역 auth 플러그인 사용
const { proxy } = getCurrentInstance()
const router = useRouter()

// 탭(한글)과 백엔드 PostType 매핑
const tabs = ['전체', '공지', '정보', '질문', '홍보', '잡담']
const typeMap = {
  공지: 'NOTICE',
  정보: 'INFO',
  질문: 'QUESTION',
  홍보: 'PROMOTION',
  잡담: 'CHAT',
}

const currentTab = ref('전체')
const sort = ref('latest')
const currentPage = ref(0) // Spring Page는 0 기반
const pageSize = ref(10)
const totalPages = ref(1)

const searchOption = ref('all')
const searchQuery = ref('')

const posts = ref([])

// API 호출: 카테고리·정렬·페이징 파라미터 전달
async function fetchPosts() {
  try {
    const params = {
      sort: sort.value,
      page: currentPage.value,
      size: pageSize.value,
    }
    if (currentTab.value !== '전체') {
      params.category = typeMap[currentTab.value]
    }
    if (searchOption.value !== 'all' && searchQuery.value.trim()) {
      params.searchBy = searchOption.value
      params.query = searchQuery.value.trim()
    }

    const resp = await axios.get('/api/boards', { params })
    posts.value = resp.data.content.map((b) => ({
      id: b.id,
      title: b.title,
      writer: b.writerName,
      date: new Date(b.createdAt).toLocaleString(),
      views: b.views,
      likes: b.likes,
    }))
    totalPages.value = resp.data.totalPages
  } catch (err) {
    console.error('게시판 목록 로드 오류', err)
  }
}

function selectTab(tab) {
  currentTab.value = tab
  currentPage.value = 0
  fetchPosts()
}

function changePage(page) {
  currentPage.value = page
  fetchPosts()
}

function prevPage() {
  if (currentPage.value > 0) changePage(currentPage.value - 1)
}

function nextPage() {
  if (currentPage.value < totalPages.value - 1) changePage(currentPage.value + 1)
}

function searchPosts() {
  currentPage.value = 0
  fetchPosts()
}

// 글쓰기 클릭 시 로그인 체크 및 라우팅
function handleWrite() {
  const token = proxy.$auth?.token || localStorage.getItem('authToken')
  if (!token) {
    alert('로그인 후 이용 가능합니다.')
    router.push({ name: 'Login' })
    return
  }
  router.push({ name: 'BbsWrite' })
}

onMounted(fetchPosts)
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css');

.bbs-wrapper {
  min-width: 600px;
  max-width: 1000px;
  margin: 60px auto;
  padding: 0 20px;
}
.bbs-header {
  display: flex;
  flex-direction: column;
  color: #111827;
}
.bbs-header-top {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.bbs-header-title {
  margin-bottom: 20px;
  color: #111827;
}
.bbs-header-center {
  display: flex;
  justify-content: space-between;
}
.bbs-tabs {
  display: flex;
}
.bbs-tab {
  padding: 6px 16px;
  border: 1px solid #cbd5e1;
  background: white;
  cursor: pointer;
  color: #374151;
  transition: all 0.2s;
}
.bbs-tab:first-child {
  border-top-left-radius: 8px;
}
.bbs-tab:last-child {
  border-top-right-radius: 8px;
}
.bbs-tab:hover {
  background-color: #3b82f6;
  color: white;
  border-color: #3b82f6;
}
.bbs-tab.active {
  background-color: #1d4ed8;
  color: white;
  border-color: #1d4ed8;
  font-weight: bold;
}
.bbs-misc {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.bbs-sort-select {
  padding: 8px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  background: white;
  color: #374151;
  cursor: pointer;
}
.bbs-write-button {
  padding: 8px 16px;
  background-color: #2563eb;
  color: white;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}
.bbs-write-button:hover {
  background-color: #1d4ed8;
}
.bbs-table table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}
.bbs-table th {
  padding: 10px;
  text-align: center;
  border-bottom: 2px solid #000;
  background: #e5e7eb;
  color: #1f2937;
  font-weight: bold;
}
.bbs-table td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #000;
}
.bbs-record-title {
  text-align: left;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  max-width: 400px;
}
.bbs-table a {
  color: inherit;
  text-decoration: none;
}
.bbs-search {
  display: flex;
  justify-content: flex-end;
  margin: 20px 0;
  gap: 8px;
}
.bbs-search-option,
.bbs-search-input {
  padding: 8px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
}
.bbs-search-input {
  width: 200px;
}
.bbs-search-button {
  background-color: #2563eb;
  border: none;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.bbs-search-button:hover {
  background-color: #1d4ed8;
}
.bbs-pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
.page-button {
  padding: 6px 10px;
  border: 1px solid #cbd5e1;
  background: white;
  cursor: pointer;
  color: #374151;
  transition: all 0.2s;
}
.page-button:hover {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}
.page-button.active {
  background: #1d4ed8;
  color: white;
  border-color: #1d4ed8;
  font-weight: bold;
}
</style>
