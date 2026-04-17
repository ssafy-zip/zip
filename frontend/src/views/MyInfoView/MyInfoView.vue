<template>
  <div class="myInfo">
    <div class="myInfo__container">
      <!-- 사이드바 -->
      <aside class="myInfo__sidebar">
        <ul>
          <template v-for="tab in tabList" :key="tab.name">
            <li :class="{ active: selectedTab === tab.tab }" @click="moveTab(tab.name)">
              <i :class="tab.icon"></i> {{ tab.label }}
            </li>
          </template>
        </ul>
      </aside>

      <!-- 콘텐츠 -->
      <section class="myInfo__content">
        <RouterView></RouterView>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const tabList = [
  { name: 'MyInfoProfileView', tab: 'profile', icon: ['fas', 'fa-user'], label: '내 정보' },
  { name: 'MyInfoFavoritesView', tab: 'favorites', icon: ['fas', 'fa-heart'], label: '관심 매물' },
  { name: 'MyInfoPostsView', tab: 'posts', icon: ['fas', 'fa-pen'], label: '작성 글' },
  {
    name: 'MyInfoCommentsView',
    tab: 'comments',
    icon: ['fas', 'fa-comment-dots'],
    label: '작성 댓글',
  },
]

const routeToTab = tabList.reduce((acc, item) => {
  acc[item.name] = item.tab
  return acc
}, {})

const selectedTab = ref('')

onMounted(() => {
  selectedTab.value = routeToTab[route.name] || 'profile'
})

watch(
  () => route.name,
  (newName) => {
    selectedTab.value = routeToTab[newName] || 'profile'
  },
)

const moveTab = (name) => {
  router.push({ name })
}
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
</style>
