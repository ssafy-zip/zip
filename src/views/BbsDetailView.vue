<template>
  <div class="bbs-detail">
    <!-- 뒤로가기: 이전 페이지 쿼리를 유지하며 목록으로 -->
    <button @click="goBack" class="back-btn">&larr; 목록으로</button>

    <!-- 게시글 헤더: 제목 & 수정/삭제 -->
    <div class="detail-header">
      <template v-if="!isEditingPost">
        <h2 class="detail-title">{{ post.title }}</h2>
      </template>
      <template v-else>
        <input v-model="editTitle" class="edit-title-input" />
      </template>
      <div v-if="isMyPost" class="post-actions">
        <template v-if="!isEditingPost">
          <button class="edit-btn" @click="startPostEdit">수정</button>
          <button class="delete-btn" @click="deletePost">삭제</button>
        </template>
        <template v-else>
          <button class="save-btn" @click="savePostEdit" :disabled="!editTitle.trim()">저장</button>
          <button class="cancel-btn" @click="cancelPostEdit">취소</button>
        </template>
      </div>
    </div>

    <!-- 메타 정보 -->
    <div class="detail-meta">
      <span>작성자: {{ post.writerName }}</span>
      <span>작성일: {{ formattedDate }}</span>
      <span>조회수: {{ post.views }}</span>
      <span>추천: {{ post.likes }}</span>
    </div>

    <!-- 게시글 본문 -->
    <div class="detail-content">
      <template v-if="!isEditingPost">
        <div v-html="post.content"></div>
      </template>
      <template v-else>
        <textarea v-model="editContent" rows="6" class="edit-content-textarea"></textarea>
      </template>
    </div>

    <!-- 댓글 섹션 (기존 로직 그대로) -->
    <section class="comments-section">
      <h3>댓글 ({{ comments.length }})</h3>
      <ul class="comment-list">
        <li v-for="c in comments" :key="c.id" class="comment-item">
          <div class="comment-meta">
            <strong>{{ c.writerName }}</strong>
            <span>{{ new Date(c.createdAt).toLocaleString() }}</span>
            <div v-if="isMyComment(c.writerId)" class="comment-actions">
              <template v-if="editingCommentId !== c.id">
                <button class="edit-btn" @click="startCommentEdit(c)">수정</button>
                <button class="delete-btn" @click="removeComment(c.id)">삭제</button>
              </template>
              <template v-else>
                <button
                  class="save-btn"
                  @click="saveCommentEdit(c.id)"
                  :disabled="!editingContent.trim()"
                >
                  저장
                </button>
                <button class="cancel-btn" @click="cancelCommentEdit">취소</button>
              </template>
            </div>
          </div>
          <div v-if="editingCommentId === c.id">
            <textarea v-model="editingContent" rows="3" class="edit-textarea"></textarea>
          </div>
          <p v-else class="comment-content">{{ c.content }}</p>
        </li>
      </ul>
      <div class="comment-form">
        <textarea v-model="newComment" placeholder="댓글을 입력하세요..." rows="3"></textarea>
        <button @click="addComment" :disabled="!newComment.trim()">등록</button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const boardId = route.params.id

// 이전 목록 페이지 (1-based)
const previousPage = Number(route.query.page || 1)
function goBack() {
  router.push({ name: 'Bbs', query: { page: previousPage } })
}

// 현재 사용자 ID 추출
function getCurrentUser() {
  const token = localStorage.getItem('authToken')
  if (!token) return ''
  try {
    return JSON.parse(atob(token.split('.')[1])).sub
  } catch {
    return ''
  }
}
const currentUser = ref(getCurrentUser())

// 게시글 상태 (writerId 포함)
const post = ref({
  id: null,
  writerId: '',
  writerName: '',
  title: '',
  content: '',
  createdAt: null,
  views: 0,
  likes: 0,
})
const formattedDate = computed(() =>
  post.value.createdAt ? new Date(post.value.createdAt).toLocaleString() : '',
)

// 게시글 수정 모드
const isEditingPost = ref(false)
const editTitle = ref('')
const editContent = ref('')

// 댓글 상태
const comments = ref([])
const newComment = ref('')
const editingCommentId = ref(null)
const editingContent = ref('')

// 권한 체크
const isMyPost = computed(() => post.value.writerId === currentUser.value)
const isMyComment = (writerId) => writerId === currentUser.value

// 데이터 로드
async function fetchDetail() {
  try {
    const { data } = await axios.get(`/api/boards/${boardId}`)
    // backend now returns writerId
    post.value = {
      ...data,
      writerId: data.writerId,
    }
  } catch {
    goBack()
  }
}
async function fetchComments() {
  try {
    const { data } = await axios.get(`/api/boards/${boardId}/comments`)
    comments.value = data
  } catch (e) {
    console.error(e)
  }
}

// 게시글 편집
function startPostEdit() {
  isEditingPost.value = true
  editTitle.value = post.value.title
  editContent.value = post.value.content
}
function cancelPostEdit() {
  isEditingPost.value = false
}
async function savePostEdit() {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  try {
    await axios.put(
      `/api/boards/${boardId}`,
      { title: editTitle.value, content: editContent.value },
      { headers: { Authorization: `Bearer ${token}` } },
    )
    isEditingPost.value = false
    await fetchDetail()
  } catch (e) {
    console.error(e)
    alert('게시글 수정 실패')
  }
}

// 게시글 삭제
async function deletePost() {
  if (!confirm('정말 삭제하시겠습니까?')) return
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  try {
    await axios.delete(`/api/boards/${boardId}`, { headers: { Authorization: `Bearer ${token}` } })
    goBack()
  } catch (e) {
    console.error(e)
    alert('게시글 삭제 실패')
  }
}

// 댓글 로직
async function addComment() {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  try {
    await axios.post(
      `/api/boards/${boardId}/comments`,
      { content: newComment.value },
      { headers: { Authorization: `Bearer ${token}` } },
    )
    newComment.value = ''
    await fetchComments()
  } catch (e) {
    console.error(e)
  }
}
async function removeComment(id) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  try {
    await axios.delete(`/api/boards/${boardId}/comments/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    await fetchComments()
  } catch (e) {
    console.error(e)
  }
}
function startCommentEdit(c) {
  editingCommentId.value = c.id
  editingContent.value = c.content
}
function cancelCommentEdit() {
  editingCommentId.value = null
  editingContent.value = ''
}
async function saveCommentEdit(id) {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  try {
    await axios.put(
      `/api/boards/${boardId}/comments/${id}`,
      { content: editingContent.value },
      { headers: { Authorization: `Bearer ${token}` } },
    )
    editingCommentId.value = null
    await fetchComments()
  } catch (e) {
    console.error(e)
  }
}

// 초기 로드
onMounted(async () => {
  await fetchDetail()
  await fetchComments()
})
</script>

<style scoped>
.bbs-detail {
  max-width: 800px;
  margin: 50px auto;
  padding: 0 20px;
}
.back-btn {
  margin-bottom: 20px;
  background: none;
  border: none;
  color: #3b82f6;
  cursor: pointer;
}
.detail-title {
  font-size: 2rem;
  margin-bottom: 10px;
}
.detail-meta {
  font-size: 0.9rem;
  color: #6b7280;
  margin-bottom: 20px;
  display: flex;
  gap: 1rem;
}
.detail-content {
  line-height: 1.6;
  margin-bottom: 40px;
}
.comments-section {
  border-top: 1px solid #e5e7eb;
  padding-top: 20px;
}
.comment-list {
  list-style: none;
  padding: 0;
  margin: 20px 0;
}
.comment-item {
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 6px;
  font-size: 0.85rem;
  color: #4b5563;
}
.comment-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}
.edit-btn,
.delete-btn,
.save-btn,
.cancel-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.85rem;
}
.edit-btn {
  color: #10b981;
}
.delete-btn {
  color: #ef4444;
}
.save-btn {
  color: #10b981;
}
.cancel-btn {
  color: #6b7280;
}
.edit-textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 8px;
}
.comment-content {
  margin: 0;
}
.comment-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.comment-form textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  resize: vertical;
}
.comment-form button {
  align-self: flex-end;
  padding: 6px 16px;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.comment-form button:disabled {
  background-color: #9ca3af;
  cursor: default;
}
</style>
