<template>
  <section class="house-map">
    <nav class="house-map__sidebar-nav">
      <ul class="house-map__sidebar-nav-list">
        <li class="house-map__sidebar-nav-item" @click="toggleSidebar">
          <i class="fa-solid fa-bars fa-2x"></i>
          <span>{{ openSidebar ? '닫기' : '열기' }}</span>
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
          <i class="fa-solid fa-ellipsis-h fa-2x"></i>
          <span>더보기</span>
        </li>
      </ul>
    </nav>

    <!-- 사이드바 -->
    <section :class="['house-map__sidebar', { closed: !openSidebar }]">
      <!--헤더: 검색-->
      <section class="house_map__search">
        <!--검색 상자-->
        <article class="house-map__sidebar-search-box">
          <div class="house-map__search-input-wrapper">
            <button class="house-map__search-button">
              <i class="fas fa-search"></i>
            </button>
            <input type="text" placeholder="검색" class="house-map__search-keyword" />
          </div>
        </article>
        <!--검색 필터-->
        <article class="house-map__search-filters">
          <div class="house-map__search-filter-header">
            <div class="house-map__search-filter-wrapper">
              <select class="house-map__search-selectBox">
                <option value="">시도</option>
                <option value="">서울특별시</option>
                <option value="">부산광역시</option>
              </select>
              <span>
                <i class="fas fa-chevron-right"></i>
              </span>
              <select class="house-map__search-selectBox">
                <option value="">시군구</option>
              </select>
              <i class="fas fa-chevron-right"></i>
              <select class="house-map__search-selectBox">
                <option value="">읍면동</option>
              </select>
            </div>
          </div>
        </article>
      </section>
      <!-- 콘텐츠: 검색된 아파트 목록 -->
      <section class="house-map__sidebar-thread">
        <div class="house-map__sidebar-thread-container">
          <div
            class="house-map__apt-info"
            v-for="item in [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"
            :key="item"
          >
            <div class="house-map__apt-info-title">아파트 이름</div>
            <ul class="house-map__apt-info-detail">
              <li><span>(최소 거래가)</span> - <span>(최대 거래가)</span> 만원</li>
              <li><span>(최소 면적)</span> - <span>(최대 면적)</span> ㎡</li>
              <li>(주소)</li>
              <li>매물 수: <span>3</span></li>
            </ul>
            <button class="house-map__apt-info-favorite" :class="{ favorited }">
              <i v-if="favorited" class="fas fa-star"></i>
              <i v-else class="far fa-star"></i>
            </button>
          </div>
        </div>
      </section>
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

const favorited = ref(true)

const openSidebar = ref(false)

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
const toggleSidebar = () => {
  openSidebar.value = !openSidebar.value
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

  border-right: 1px solid #d9d9d9;
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

  font-size: 14px;
  color: #3c3c3c;

  box-sizing: border-box;
  padding: 4px;
  cursor: pointer;
}

.house-map__sidebar-nav-item:hover {
  color: #0475f4;
  border: 1px solid #0475f4;
}

/* 사이드바 */
.house-map__sidebar {
  display: flex;
  flex-direction: column;

  position: absolute;
  z-index: 999;
  overflow: hidden;
  left: var(--sidebar-nav-width);
  transition: transform 0.3s ease;

  height: 100%;
  width: 350px;

  background-color: #ffffff;
  box-shadow: 4px 0 4px rgba(30, 41, 59, 0.1);
  border-right: 1px solid #e5e7eb;

  padding: 10px 0;
  box-sizing: border-box;
}
.house-map__sidebar.closed {
  transform: translateX(-100%);
}

.house_map__search {
  padding: 0 10px;
  border-bottom: 1px solid #3c3c3c;
}

.house-map__search-input-wrapper {
  display: flex;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 12px;
}

.house-map__search-button {
  background: none;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.house-map__search-keyword {
  flex: 1;
  border: none;
  font-size: 16px;
  outline: none;
}

.house-map__search-button:hover {
  color: #2563eb;
}

.house-map__search-button::after {
  content: '';
  border: 1px solid #ddd;
  margin-left: 10px;
}

.house-map__search-filter-header {
  display: flex;
  justify-content: space-between;

  padding: 8px;
  margin: 8px 0;
  gap: 12px;
}

.house-map__search-filter-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
}

.house-map__search-selectBox {
  flex: 1;
  background: none;
  border: none;
  appearance: none;
  outline: none;
  cursor: pointer;
  text-align: center;
  font-size: 14px;
}

.house-map__sidebar-thread {
  flex: 1;
  overflow-y: scroll;
  min-width: 0;
  padding: 0 10px;
}

.house-map__apt-info {
  display: flex;
  flex-direction: column;
  justify-content: center;

  border-bottom: 1px solid #3c3c3c;
  background-color: white;
  padding: 10px;
}
.house-map__apt-info:hover {
  background-color: #f3f4f6;
}

.house-map__apt-info-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0;
  color: #111827;
}
.house-map__apt-info-detail {
  list-style: none;
  margin: 0;
  padding: 0;

  display: flex;
  flex-direction: column;

  padding-left: 10px;
  font-size: 14px;
  color: #374151;
}

.house-map__apt-info-favorite {
  align-self: flex-end;
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
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
