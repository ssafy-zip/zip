<template>
  <div class="bbs-write">
    <form @submit.prevent="submitPost" class="write-form">
      <!-- 게시글 작성 상단 -->
      <div class="bbs-write__header">
        <h2 class="bbs-write__title">글쓰기</h2>
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

      <!-- 툴바: 볼드, 이탤릭, 밑줄, 이미지 업로드 -->
      <div class="toolbar">
        <button type="button" @click="format('bold')"><b>B</b></button>
        <button type="button" @click="format('italic')"><i>I</i></button>
        <button type="button" @click="format('underline')"><u>U</u></button>
        <button type="button" @click="triggerImageUpload">이미지 업로드</button>
        <input
          type="file"
          ref="imageInput"
          accept="image/*"
          @change="onImageSelected"
          style="display: none"
        />
      </div>

      <!-- 본문 에디터 (contenteditable) -->
      <div
        ref="editor"
        class="bbs-write__editor"
        contenteditable="true"
        @input="updateContent"
      ></div>

      <!-- 버튼 영역 -->
      <div class="bbs-write__buttons">
        <button
          type="button"
          class="bbs-write__button bbs-write__button--secondary"
          @click="cancel"
        >
          취소하기
        </button>
        <button type="submit" class="bbs-write__button bbs-write__button--primary">등록하기</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import baseURL from '@/baseURL'

// 인증토큰 · 라우터
const { proxy } = getCurrentInstance()
const router = useRouter()

// 폼 상태
const form = ref({ category: '', title: '', content: '' })

// 에디터 · 파일 input 참조
const editor = ref(null)
const imageInput = ref(null)

// 탭 · 권한 설정
const tabs = ['전체', '공지', '정보', '질문', '홍보', '잡담']
const userRole = computed(() => localStorage.getItem('userRole') || '')
const isAdmin = computed(() => userRole.value === 'ADMIN')
const filteredTabs = computed(() => tabs.slice(1).filter((tab) => tab !== '공지' || isAdmin.value))

// 카테고리 매핑
const categoryMap = {
  공지: 'NOTICE',
  정보: 'INFO',
  질문: 'QUESTION',
  홍보: 'PROMOTION',
  잡담: 'CHAT',
}

// 텍스트 포맷팅
function format(cmd) {
  document.execCommand(cmd, false, null)
  updateContent()
}

// 이미지 업로드 창 띄우기
function triggerImageUpload() {
  imageInput.value.click()
}

// 파일 선택 후 AWS 업로드 → <img> 태그 삽입
async function onImageSelected(e) {
  const file = e.target.files?.[0]
  if (!file) return

  // AWS 업로드
  const fd = new FormData()
  fd.append('file', file)
  const res = await baseURL.post('/api/aws', fd, {
    headers: {
      'Content-Type': 'multipart/form-data',
      Authorization: `Bearer ${proxy.$auth.token}`,
    },
  })
  const url = res.data

  // 커서 위치 Range 가져오기
  const sel = window.getSelection()
  if (!sel || !sel.rangeCount) {
    editor.value.focus()
  }
  const range = sel.getRangeAt(0)
  range.deleteContents()

  // <img> 노드 생성 & 삽입
  const img = document.createElement('img')
  img.src = url
  img.style.maxWidth = '100%'
  range.insertNode(img)

  // 커서를 이미지 뒤로 이동
  range.setStartAfter(img)
  sel.removeAllRanges()
  sel.addRange(range)

  // 내용 동기화
  updateContent()
}

// 에디터 내용 동기화
function updateContent() {
  form.value.content = editor.value.innerHTML
}

// 게시글 등록
async function submitPost() {
  if (form.value.category === '공지' && !isAdmin.value) {
    alert('공지 카테고리는 관리자만 작성할 수 있습니다.')
    return
  }

  const payload = {
    category: categoryMap[form.value.category],
    title: form.value.title,
    content: form.value.content,
  }

  try {
    await baseURL.post('/api/boards', payload, {
      headers: { Authorization: `Bearer ${proxy.$auth.token}` },
    })
    alert('게시글이 등록되었습니다.')
    router.push('/bbs')
  } catch (err) {
    console.error(err)
    alert('게시글 등록 중 오류가 발생했습니다.')
  }
}

// 취소
function cancel() {
  router.push('/bbs')
}

// 초기 포커스
onMounted(() => editor.value.focus())
</script>

<style scoped>
/* 컨테이너를 화면 가득 채우기 */
.bbs-write {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 32px;
  background: #ffffff;
  box-sizing: border-box;
}

/* 헤더 */
.bbs-write__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.bbs-write__title {
  margin: 0;
  font-size: 1.5rem;
  color: #111827;
}

/* 카테고리 셀렉트 */
.bbs-write__select {
  width: 200px;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background-color: #f9fafb;
  transition: all 0.2s;
}
.bbs-write__select:focus {
  outline: none;
  border-color: #2563eb;
  background-color: #ffffff;
}

/* 제목 입력 */
.bbs-write__title-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  margin-bottom: 24px;
  transition: border-color 0.2s;
}
.bbs-write__title-input:focus {
  outline: none;
  border-color: #2563eb;
}

/* 툴바 */
.toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.toolbar button {
  padding: 6px 12px;
  border: 1px solid #e5e7eb;
  background: #f3f4f6;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition:
    background 0.2s,
    border-color 0.2s;
}
.toolbar button:hover {
  background: #e5e7eb;
  border-color: #d1d5db;
}

/* 에디터 (contenteditable) */
.bbs-write__editor {
  width: 100%;
  min-height: 60vh;
  padding: 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #f9fafb;
  transition:
    border-color 0.2s,
    background 0.2s;
  overflow-y: auto;
  box-sizing: border-box;
}
.bbs-write__editor:focus {
  outline: none;
  border-color: #2563eb;
  background: #ffffff;
}

/* 버튼 영역 */
.bbs-write__buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
.bbs-write__button {
  padding: 10px 24px;
  font-weight: 500;
  border-radius: 6px;
  transition: background 0.2s;
}

/* 취소 버튼 */
.bbs-write__button--secondary {
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
}
.bbs-write__button--secondary:hover {
  background-color: #e5e7eb;
}

/* 등록 버튼 */
.bbs-write__button--primary {
  background-color: #2563eb;
  color: #ffffff;
  border: none;
}
.bbs-write__button--primary:hover {
  background-color: #1e4bb8;
}
</style>
