<template>
  <div class="news-list">
    <h1 class="news-title">최신 부동산 뉴스</h1>
    <ul class="news-items">
      <li v-for="(item, idx) in news" :key="item.id" class="news-item">
        <div class="news-rank">{{ idx + 1 }}</div>
        <div class="news-content">
          <a :href="item.link" target="_blank" class="news-headline">
            {{ item.title }}
          </a>
          <div class="news-source">{{ item.source }}</div>
        </div>
        <div class="news-thumb">
          <a v-if="item.thumbnail" :href="item.link" target="_blank" rel="noopener">
            <img :src="item.thumbnail" alt="thumb" />
          </a>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import baseURL from '@/baseURL'

const news = ref([])

async function fetchNews() {
  try {
    const res = await baseURL.get('/api/news')
    news.value = res.data
  } catch (err) {
    console.error(err)
  }
}

onMounted(fetchNews)
</script>

<style scoped>
.news-list {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 16px;
}
.news-title {
  font-size: 1.75rem;
  font-weight: bold;
  margin-bottom: 24px;
}
.news-items {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  row-gap: 16px;
}
.news-item {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #ececec;
  padding-bottom: 12px;
}
.news-rank {
  flex: 0 0 32px;
  font-size: 1.25rem;
  font-weight: bold;
  color: #666;
}
.news-content {
  flex: 1;
  padding: 0 12px;
}
.news-headline {
  display: block;
  font-size: 1rem;
  font-weight: 500;
  color: #111;
  text-decoration: none;
  margin-bottom: 4px;
}
.news-headline:hover {
  color: #0050ef;
  text-decoration: underline;
}
.news-source {
  font-size: 0.85rem;
  color: #888;
}
.news-thumb {
  flex: 0 0 100px;
  margin-left: 12px;
}
.news-thumb img {
  width: 100px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}
</style>
