<template>
  <div class="bbs-write-wrapper">
    <h2 class="write-title">게시글 작성</h2>
    <form @submit.prevent="submitPost" class="write-form">
      <div class="form-group">
        <label for="category">카테고리</label>
        <select id="category" v-model="form.category" required>
          <option v-for="tab in filteredTabs" :key="tab" :value="tab">
            {{ tab }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label for="title">제목</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
          placeholder="제목을 입력하세요"
          required
        />
      </div>

      <div class="form-group">
        <label for="content">내용</label>
        <textarea
          id="content"
          v-model="form.content"
          rows="10"
          placeholder="내용을 입력하세요"
          required
        ></textarea>
      </div>

      <div class="form-actions">
        <button type="button" class="cancel-button" @click="cancel">취소</button>
        <button type="submit" class="submit-button">등록</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const { proxy } = getCurrentInstance()
const router = useRouter()

const tabs = ['전체', '공지', '정보', '질문', '홍보', '잡담']

// form state
const form = ref({ category: tabs[1], title: '', content: '' })

// Determine admin status
const userRole = computed(() => localStorage.getItem('userRole') || '')
const isAdmin = computed(() => userRole.value === 'ADMIN')

// Filtered tabs: hide '공지' for non-admin
const filteredTabs = computed(() => {
  return tabs.slice(1).filter((tab) => tab !== '공지' || isAdmin.value)
})

// Submission handler
const categoryMap = {
  공지: 'NOTICE',
  정보: 'INFO',
  질문: 'QUESTION',
  홍보: 'PROMOTION',
  잡담: 'CHAT',
}

const submitPost = async () => {
  if (form.value.category === '공지' && !isAdmin.value) {
    alert('공지 카테고리는 관리자만 작성할 수 있습니다.')
    return
  }

  try {
    await await axios.post(
      '/api/boards',
      {
        category: categoryMap[form.value.category], // DB 'type' 컬럼 매핑
        title: form.value.title,
        content: form.value.content,
      },
      { headers: { Authorization: `Bearer ${proxy.$auth.token}` } },
    )
    alert('게시글이 등록되었습니다.')
    router.push('/bbs')
  } catch (err) {
    console.error(err)
    alert('게시글 등록 중 오류가 발생했습니다.')
  }
}

const cancel = () => {
  router.push('/bbs')
}
</script>

<style scoped>
.bbs-write-wrapper {
  max-width: 800px;
  margin: 60px auto;
  padding: 0 20px;
}
.write-title {
  font-size: 1.5rem;
  margin-bottom: 24px;
  color: #111827;
}
.write-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.form-group label {
  font-weight: bold;
  color: #374151;
}
.form-group input,
.form-group select,
.form-group textarea {
  padding: 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 14px;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
.cancel-button,
.submit-button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}
.cancel-button {
  background-color: #e5e7eb;
  color: #374151;
}
.cancel-button:hover {
  background-color: #d1d5db;
}
.submit-button {
  background-color: #2563eb;
  color: white;
}
.submit-button:hover {
  background-color: #1d4ed8;
}
</style>
