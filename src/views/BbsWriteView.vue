<template>
  <div class="bbs-write">
    <form @submit.prevent="submitPost" class="write-form">
      <!-- 게시글 작성 상단 -->
      <div class="bbs-write__header">
        <h2>글쓰기</h2>
        <select v-model="form.category" class="bbs-write__select" required>
          <option disabled value="">카테고리 선택</option>
          <option v-for="tab in filteredTabs" :key="tab" :value="tab">
            {{ tab }}
          </option>
        </select>
      </div>

      <!-- 제목 입력 -->
      <input
        v-model="form.title"
        class="bbs-write__title-input"
        type="text"
        placeholder="제목을 입력하세요(필수)"
        required
      />

      <!-- 본문 에디터 -->
      <quill-editor
        v-model:content="form.content"
        contentType="html"
        theme="snow"
        class="bbs-write__editor"
      />

      <!-- 버튼 영역 -->
      <div class="bbs-write__buttons">
        <button class="bbs-write__button bbs-write__button--secondary" @click="cancel">
          취소하기
        </button>
        <button class="submit bbs-write__button bbs-write__button--primary">등록하기</button>
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
const form = ref({ category: '', title: '', content: '' })

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
.bbs-write {
  max-width: 800px;
  margin: 40px auto;
  padding: 24px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 12px;
}

.bbs-write__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.bbs-write__select {
  padding: 8px 12px;
  font-size: 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.bbs-write__title-input {
  width: 100%;
  padding: 12px;
  font-size: 18px;
  border: 1px solid #ccc;
  border-radius: 6px;
  margin-bottom: 20px;
}

.bbs-write__editor {
  min-height: 300px;
  margin-bottom: 24px;
}

.bbs-write__buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.bbs-write__button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
}

.bbs-write__button--primary {
  background-color: #2563eb;
  color: #fff;
}

.bbs-write__button--secondary {
  background-color: #e5e7eb;
  color: #111827;
}
</style>
