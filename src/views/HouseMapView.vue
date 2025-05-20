<template>
  <div class="house-map-container">
    <!-- 사이드바 -->
    <!-- 사이드바 토글 버튼(임시)-->
    <div class="house-sidebar-toggle" @click="toggleSidebar">
      {{ sidebarOpen ? '닫기' : '열기' }}
    </div>

    <div :class="['house-sidebar', { open: sidebarOpen }]">
      <!-- <div v-if="sidebarOpen" class="house-sidebar-content"> -->
      <div class="house-sidebar-content">
        <h2>사이드바 영역</h2>
      </div>
    </div>

    <!-- 지도 영역 -->
    <div class="house-map" ref="mapContainer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const sidebarOpen = ref(true)
const mapContainer = ref(null)

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

onMounted(() => {
  // Kakao 지도 스크립트 로드
  const script = document.createElement('script')
  script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_JS_KEY}&autoload=false`
  script.async = true
  document.head.appendChild(script)

  script.onload = () => {
    window.kakao.maps.load(() => {
      const options = {
        center: new window.kakao.maps.LatLng(37.5665, 126.978), // 서울 시청
        level: 5,
      }
      new window.kakao.maps.Map(mapContainer.value, options)
    })
  }
})
</script>

<style scoped>
.house-map-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

/* 사이드바 */
.house-sidebar {
  width: 0;
  background-color: #f3f4f6;
  transition: width 0.3s ease;
  overflow: hidden;
  position: relative;
  border-right: 1px solid #e5e7eb;
}
.house-sidebar.open {
  width: 250px;
}

/* 사이드바 열고 닫기 버튼 */
.house-sidebar-toggle {
  position: absolute;
  top: 0px;
  left: 0px;
  width: 50px;
  background-color: #2563eb;
  color: white;
  padding: 6px;
  cursor: pointer;
  text-align: center;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
  font-size: 14px;
}

/* 사이드바 내부 내용 */
.house-sidebar-content {
  min-width: 210px;
  padding: 20px;
}
.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}
.sidebar-menu li {
  padding: 8px 0;
  border-bottom: 1px solid #e5e7eb;
  cursor: pointer;
  color: #111827;
}

/* 지도 영역 */
.house-map {
  flex: 1;
  background-color: #e5e7eb;
  position: relative;
}
.map-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #374151;
}
</style>
