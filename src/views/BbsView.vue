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
            @click="currentTab = tab"
          >
            {{ tab }}
          </button>
        </div>

        <!-- 정렬 + 글쓰기 -->
        <div class="bbs-misc">
          <select class="bbs-sort-select" v-model="sort">
            <option value="latest">최신순</option>
            <option value="oldest">오래된순</option>
            <option value="views">조회순</option>
            <option value="likes">추천순</option>
            <option value="comments">댓글순</option>
          </select>
          <router-link to="/bbs/write" class="bbs-write-button">글쓰기</router-link>
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
      <button class="bbs-search-button" @click="searchPosts"><i class="fa fa-search"></i></button>
    </div>

    <!-- 페이지네이션 -->
    <div class="bbs-pagination">
      <button class="page-button" @click="prevPage">&laquo;</button>
      <button
        v-for="page in totalPages"
        :key="page"
        :class="['page-button', { active: currentPage === page }]"
        @click="currentPage = page"
      >
        {{ page }}
      </button>
      <button class="page-button" @click="nextPage">&raquo;</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const tabs = ['전체', '공지', '정보', '질문', '홍보', '잡담']
const currentTab = ref('전체')
const sort = ref('latest')

const searchOption = ref('all')
const searchQuery = ref('')
const currentPage = ref(1)

const posts = ref([
  {
    id: 1,
    title: 'Zip 오픈을 축하합니다',
    writer: '관리자',
    date: '2024-05-10',
    views: 123,
    likes: 11,
  },
  // ... 더미 데이터 또는 API 연동
])

const totalPages = 5

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--
}
const nextPage = () => {
  if (currentPage.value < totalPages) currentPage.value++
}
const searchPosts = () => {
  alert(`"${searchOption.value}"에서 "${searchQuery.value}" 검색`)
}
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css');

.bbs-wrapper {
  min-width: 600px;
  max-width: 1000px;
  margin: 60px auto;
  padding: 0 20px;
}

/* 헤더 */
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

/* 탭 */
.bbs-tabs {
  display: flex;
}
.bbs-tab {
  padding: 6px 16px;
  border: 1px solid #cbd5e1;
  background: white;
  cursor: pointer;
  color: #374151;
  transition: all 0.2s ease;
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

/* 정렬 + 글쓰기 */
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
  background-color: white;
  color: #374151;
  cursor: pointer;
}
.bbs-write-button {
  padding: 8px 16px;
  background-color: #2563eb;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
  transition: background-color 0.2s ease;
}
.bbs-write-button:hover {
  background-color: #1d4ed8;
}

/* 게시판 테이블 */
.bbs-table table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}
.bbs-table th {
  padding: 10px;
  text-align: center;
  border-bottom: 2px solid #000000;
  background-color: #e5e7eb;
  color: #1f2937;
  font-weight: bold;
}
.bbs-table td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #000000;
}
.bbs-table .bbs-record-title {
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
.bbs-table a:hover {
  text-decoration: underline;
}

/* 검색 */
.bbs-search {
  display: flex;
  justify-content: flex-end;
  margin: 20px 0;
  gap: 8px;
}
.bbs-search-option {
  padding: 8px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  background-color: white;
  color: #374151;
}
.bbs-search-input {
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  width: 200px;
}
.bbs-search-button {
  background-color: #2563eb;
  border: none;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.bbs-search-button:hover {
  background-color: #1d4ed8;
}

/* 페이지네이션 */
.bbs-pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
.page-button {
  padding: 6px 10px;
  border: 1px solid #cbd5e1;
  background-color: white;
  cursor: pointer;
  color: #374151;
  transition: all 0.2s ease;
}
.page-button:hover {
  background-color: #3b82f6;
  color: white;
  border-color: #3b82f6;
}
.page-button.active {
  background-color: #1d4ed8;
  color: white;
  border-color: #1d4ed8;
  font-weight: bold;
}
</style>
