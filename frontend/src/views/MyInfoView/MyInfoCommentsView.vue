<template>
  <section class="myInfo__content-container">
    <!-- 카테고리별 댓글 카드 -->
    <div class="info-tile" v-for="(catBlock, idx) in filteredCategories" :key="idx">
      <!-- 카드 헤더: 카테고리 이름 -->
      <div class="info-tile__title-container">
        <div class="info-tile__title">
          {{ categoryLabels[catBlock.category] || catBlock.category }}
        </div>
      </div>
      <div class="info-tile__seperator"></div>

      <!-- 카드 콘텐츠: 게시글별 그룹으로 묶인 댓글 리스트 -->
      <div class="info-tile__content">
        <div
          class="board-group"
          v-for="board in groupedCategories[idx].boards"
          :key="board.boardId"
        >
          <!-- 게시글 제목 (상세 페이지로 이동) -->
          <router-link
            :to="{ name: 'BbsDetail', params: { id: board.boardId } }"
            class="board-title tile-link"
          >
            {{ board.boardTitle }}
          </router-link>
          <!-- 해당 게시글에 단 내 댓글들 -->
          <div class="comment-item" v-for="comment in board.comments" :key="comment.commentId">
            <span class="tile-label">{{ formatDate(comment.createdAt) }}</span>
            <span class="tile-comment">{{ comment.content }}</span>
          </div>
        </div>

        <!-- 전체 보기 링크 -->
        <div v-if="groupedCategories[idx].boards.length" class="tile-action">
          <router-link
            :to="{ path: '/myPage/comments', query: { category: catBlock.category } }"
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

// 인증 토큰 획득
const { proxy } = getCurrentInstance()

// API 로부터 받아올 카테고리별 댓글 데이터
const categories = ref([])

// PostType → 한글 레이블
const categoryLabels = {
  NOTICE: '공지',
  INFO: '정보',
  QUESTION: '질문',
  PROMOTION: '홍보',
  CHAT: '잡담',
}

// 1) 댓글이 하나 이상인 카테고리만 필터
const filteredCategories = computed(() =>
  categories.value.filter((block) => Array.isArray(block.comments) && block.comments.length > 0),
)

// 2) 같은 게시글(boardId) 기준으로 댓글을 그룹핑
const groupedCategories = computed(() =>
  filteredCategories.value.map((block) => {
    const map = {}
    block.comments.forEach((c) => {
      if (!map[c.boardId]) {
        map[c.boardId] = {
          boardId: c.boardId,
          boardTitle: c.boardTitle,
          comments: [],
        }
      }
      map[c.boardId].comments.push(c)
    })
    return {
      category: block.category,
      boards: Object.values(map),
    }
  }),
)

// ISO 문자열을 YYYY.MM.DD로
function formatDate(iso) {
  const d = new Date(iso)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

// 컴포넌트 마운트 시 API 호출
onMounted(async () => {
  try {
    const { data } = await baseURL.get('/api/users/myPage/myCommentsListsByCategory', {
      headers: {
        Authorization: `Bearer ${proxy.$auth.token}`,
      },
    })
    categories.value = data
  } catch (err) {
    console.error('내 댓글 불러오기 실패:', err)
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

/* 카드 */
.info-tile {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 0 24px 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
/* 헤더 */
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

/* 게시글 그룹 */
.board-group {
  margin-bottom: 16px;
}
.board-title {
  font-size: 16px;
  font-weight: 500;
  color: #2563eb;
  text-decoration: none;
}
.board-title:hover {
  text-decoration: underline;
}

/* 댓글 아이템 */
.comment-item {
  display: flex;
  gap: 8px;
  margin-left: 12px;
  margin-top: 4px;
}
.tile-label {
  font-size: 14px;
  color: #6b7280;
}
.tile-comment {
  font-size: 14px;
  color: #374151;
}

/* 전체 보기 링크 */
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
