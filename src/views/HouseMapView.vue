<template>
  <section class="house-map">
    <nav class="house-map__sidebar-nav">
      <ul class="house-map__sidebar-nav-list">
        <li class="house-map__sidebar-nav-item" @click="toggleSidebarOpen">
          <i class="fa-solid fa-bars fa-2x"></i>
          <span>{{ sidebarOpen ? '닫기' : '열기' }}</span>
        </li>
        <li class="house-map__sidebar-nav-item" @click="moveToCurrentLocation">
          <i class="fa-solid fa-crosshairs fa-2x"></i>
          <span>내 위치</span>
        </li>
        <!--매물 검색 및 조회-->
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-search fa-2x"></i>
          <span>검색</span>
        </li>
        <!--관심 지역, 매물 목록-->
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-heart fa-2x"></i>
          <span>관심</span>
        </li>
        <!--챗봇-->
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-robot fa-2x"></i>
          <span>AI 검색</span>
        </li>
        <!--기타-->
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-ellipsis-h fa-2x"></i> <span>더보기</span>
        </li>
      </ul>
    </nav>

    <!-- 사이드바 -->
    <section :class="['house-map__sidebar', { closed: !sidebarOpen }]">
      <div class="house-map__sidebar-content">
        <h2>사이드바 영역</h2>
      </div>
    </section>

    <!-- 지도 영역 -->
    <section class="house-map__map-container" ref="mapContainer">
      <div v-if="!isMapLoaded" class="house-map__map-placeholder faild">
        지도를 로드할 수 없습니다.<br />
        나중에 다시 시도해 주세요.
      </div>
    </section>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const sidebarOpen = ref(false)

const mapContainer = ref(null)
const map = ref(null)
const isMapLoaded = computed(() => map.value)

// Kakao 지도 스크립트 로드
const loadKakaoMapScript = async () => {
  return new Promise((resolve) => {
    if (window.kakao && window.kakao.maps) {
      resolve() // 이미 로드됨
    } else {
      const script = document.createElement('script')
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_JS_KEY}&autoload=false`
      script.async = true
      script.onload = () => {
        window.kakao.maps.load(() => {
          resolve()
        })
      }
      document.head.appendChild(script)
    }
  })
}
/* 현재 위치 조회 */
const getCurrentPosition = async () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) reject()
    navigator.geolocation.getCurrentPosition(resolve, reject)
  })
}

const moveToCurrentLocation = async () => {
  const MAX_LEVEL = 5
  try {
    const position = await getCurrentPosition()
    const { latitude, longitude } = position.coords

    const targetLatLng = new window.kakao.maps.LatLng(latitude, longitude)
    if (map.value) {
      // 줌 아웃 상한
      const currentLevel = map.value.getLevel()
      if (currentLevel > MAX_LEVEL) {
        map.value.setLevel(MAX_LEVEL)
      }
      // 현재 위치로 이동
      map.value.panTo(targetLatLng)
      const marker = new window.kakao.maps.Marker({ position: targetLatLng })
      marker.setMap(map.value)
    }
  } catch (error) {
    alert('위치 정보를 가져오지 못했습니다.')
    console.log(error)
  }
}

onMounted(async () => {
  await loadKakaoMapScript()

  const options = { level: 5 }
  try {
    const position = await getCurrentPosition()
    const { latitude, longitude } = position.coords
    options.center = new window.kakao.maps.LatLng(latitude, longitude)
  } catch {
    options.center = new window.kakao.maps.LatLng(37.5665, 126.978) // 서울 시청
  }

  map.value = new window.kakao.maps.Map(mapContainer.value, options)

  const marker = new window.kakao.maps.Marker({ position: options.center })
  marker.setMap(map.value)

  const zoomControl = new window.kakao.maps.ZoomControl()
  map.value.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT)
})

/* 사이드바 제어 */
const toggleSidebarOpen = () => {
  sidebarOpen.value = !sidebarOpen.value
}
</script>

<style scoped>
.house-map {
  position: relative;
  display: flex;
  overflow: hidden;

  flex: 1;
  height: 100%;
  width: 100%;

  --sidebar-nav-width: 66px;
}

/* 사이드바 네비게이션 전체 */
.house-map__sidebar-nav {
  background-color: #ffffff;
  z-index: 1000;
  width: var(--sidebar-nav-width);
  box-sizing: border-box;
}

/* 네비게이션 리스트 */
.house-map__sidebar-nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
}

/* 네비게이션 항목 */
.house-map__sidebar-nav-item {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  width: 100%;
  aspect-ratio: 1;

  margin: 0 auto;
  gap: 2px;
  cursor: pointer;

  font-size: 14px;
  color: #3c3c3c;

  box-sizing: border-box;
  padding: 4px;
}

/* 호버 효과 */
.house-map__sidebar-nav-item:hover {
  color: #0475f4;
  border: 1px solid #0475f4;
}

/* 사이드바 */
.house-map__sidebar {
  position: absolute;
  z-index: 999;
  overflow: hidden;
  left: var(--sidebar-nav-width);
  transition: transform 0.3s ease;

  width: 250px;
  height: 100%;

  background-color: #f3f4f6;
  border-right: 1px solid #e5e7eb;
}
.house-map__sidebar.closed {
  transform: translateX(-100%);
}

.house-map__sidebar-content {
  min-width: 210px;
  padding: 20px;
}

/* 지도 영역 */
.house-map__map-container {
  flex: 1;
  background-color: #e5e7eb;
  position: relative;
}

.house-map__map-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #374151;
}
</style>
