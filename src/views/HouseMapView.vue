<template>
  <section class="house-map">
    <!-- 사이드바 네비게이션 -->
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
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-search fa-2x"></i>
          <span>검색</span>
        </li>
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-heart fa-2x"></i>
          <span>관심</span>
        </li>
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-robot fa-2x"></i>
          <span>AI 검색</span>
        </li>
        <li class="house-map__sidebar-nav-item">
          <i class="fa-solid fa-ellipsis-h fa-2x"></i>
          <span>더보기</span>
        </li>
      </ul>
    </nav>

    <!-- 사이드바 -->
    <section :class="['house-map__sidebar', { closed: !openSidebar }]">
      <!-- 검색 헤더 -->
      <section class="house_map__search">
        <article class="house-map__sidebar-search-box">
          <div class="house-map__search-input-wrapper">
            <button class="house-map__search-button" @click="searchApt">
              <i class="fas fa-search"></i>
            </button>
            <input
              type="text"
              placeholder="검색"
              class="house-map__search-keyword"
              v-model="aptNm"
              @keydown.enter="searchApt"
            />
          </div>
        </article>

        <!-- 검색 필터 및 관심지역 ★ -->
        <article class="house-map__search-filters">
          <div class="house-map__search-filter-header">
            <button
              class="house-map__selete_current_position_button"
              @click="setBjdFilterToMapCenter"
            >
              <i class="fas fa-crosshairs"></i>
            </button>
            <div class="house-map__search-filter-wrapper">
              <select
                class="house-map__search-selectBox"
                v-model="selectedSido"
                @change="updateSggList"
              >
                <option value="" disabled>시/도</option>
                <option v-for="s in sidoList" :key="s.code" :value="s.code.slice(0, 2)">
                  {{ s.sidoName }}
                </option>
              </select>
              <i class="fas fa-chevron-right"></i>

              <select
                class="house-map__search-selectBox"
                v-model="selectedSgg"
                @change="updateUmdList"
              >
                <option value="" disabled>시/군/구</option>
                <option v-for="g in sggList" :key="g.code" :value="g.code.slice(0, 5)">
                  {{ g.sggName }}
                </option>
              </select>
              <i class="fas fa-chevron-right"></i>

              <select class="house-map__search-selectBox" v-model="selectedUmd">
                <option value="" disabled>읍/면/동</option>
                <option v-for="u in umdList" :key="u.code" :value="u.code">{{ u.umdName }}</option>
              </select>

              <!-- 관심지역 토글 버튼 -->
              <button class="breadcrumb-star-btn" @click="toggleStar">
                <i
                  class="fa-solid fa-star"
                  :style="{ color: isStarred ? '#fbbf24' : '#cbd5e1' }"
                ></i>
              </button>
            </div>
          </div>
        </article>
      </section>

      <!-- 아파트 검색 결과 -->
      <section class="house-map__sidebar-thread">
        <div class="house-map__sidebar-thread-container">
          <div class="house-map__apt-info" v-for="item in apartments" :key="item.aptSeq">
            <div>{{ item.aptSeq }}</div>
            <div class="house-map__apt-info-title">{{ item.aptNm }}</div>
            <ul class="house-map__apt-info-detail">
              <li>
                <span>{{
                  Math.min(...item.deals.map((d) => +d.dealAmount.replace(/,/g, '')))
                }}</span>
                -
                <span>{{
                  Math.max(...item.deals.map((d) => +d.dealAmount.replace(/,/g, '')))
                }}</span>
                만원
              </li>
              <li>
                <span>{{ Math.min(...item.deals.map((d) => +d.excluUseAr)) }}</span> -
                <span>{{ Math.max(...item.deals.map((d) => +d.excluUseAr)) }}</span> ㎡
              </li>
              <li>
                거래 이력 수: <span>{{ item.deals.length }}</span>
              </li>
            </ul>
            <button class="house-map__apt-info-favorite">
              <i class="fas fa-star"></i>
            </button>
          </div>
        </div>
      </section>
    </section>

    <!-- 카카오 맵 -->
    <section class="house-map__map-container" ref="mapContainer">
      <div v-if="!isMapLoaded" class="house-map__map-placeholder">
        지도를 로드할 수 없습니다.<br />나중에 다시 시도해 주세요.
      </div>
    </section>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'

// JWT 토큰 설정
const token = localStorage.getItem('authToken')
if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

// 법정동 목록 상태
const sidoList = ref([])
const sggList = ref([])
const umdList = ref([])
const selectedSido = ref('')
const selectedSgg = ref('')
const selectedUmd = ref('')

// 관심지역 상태
const isStarred = ref(false)

// 법정동 목록 조회
async function updateSidoList() {
  const { data } = await axios.get('/api/lwdCd/sido')
  sidoList.value = data
  selectedSido.value = ''
  selectedSgg.value = ''
  selectedUmd.value = ''
}
async function updateSggList() {
  if (!selectedSido.value) sggList.value = []
  else {
    const { data } = await axios.get(`/api/lwdCd/sgg/${selectedSido.value}`)
    sggList.value = data
  }
  selectedSgg.value = ''
  selectedUmd.value = ''
}
async function updateUmdList() {
  if (!selectedSgg.value) umdList.value = []
  else {
    const { data } = await axios.get(`/api/lwdCd/umd/${selectedSgg.value}`)
    umdList.value = data
  }
  selectedUmd.value = ''
}

// 선택된 읍/면/동 변경 시 등록 여부 조회
watch(
  selectedUmd,
  async (newCode) => {
    if (!newCode) {
      isStarred.value = false
      return
    }
    try {
      const { data } = await axios.get('/api/interestRegion/isInterestRegion', {
        params: { lwdCd: newCode },
      })
      const clean = String(data).trim().replace(/^"|"$/g, '')
      isStarred.value = clean.toLowerCase() === 'true'
    } catch {
      isStarred.value = false
    }
  },
  { immediate: true },
)

// 관심지역 토글
async function toggleStar() {
  if (!token) return alert('로그인 해주세요.')
  if (!selectedUmd.value) return
  try {
    if (isStarred.value) {
      await axios.delete('/api/interestRegion', { params: { lwdCd: selectedUmd.value } })
      isStarred.value = false
    } else {
      await axios.post('/api/interestRegion', { lwdCd: selectedUmd.value })
      isStarred.value = true
    }
  } catch {
    console.error('관심지역 업데이트 실패')
  }
}

// 카카오맵 초기화
const mapContainer = ref(null)
const map = ref(null)
const isMapLoaded = computed(() => !!map.value)

async function loadKakaoMapScript() {
  if (window.kakao) return
  return new Promise((resolve) => {
    const script = document.createElement('script')
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_JS_KEY}&autoload=false&libraries=services`
    script.async = true
    script.onload = () => window.kakao.maps.load(resolve)
    document.head.appendChild(script)
  })
}

function getCurrentDevicePosition() {
  return new Promise((res, rej) =>
    navigator.geolocation ? navigator.geolocation.getCurrentPosition(res, rej) : rej(),
  )
}

function coord2RegionCode(lat, lng) {
  return new Promise((res, rej) => {
    const geocoder = new window.kakao.maps.services.Geocoder()
    geocoder.coord2RegionCode(lng, lat, (result, status) => {
      if (status === window.kakao.maps.services.Status.OK) {
        const region = result.find((r) => r.region_type === 'B')
        return region ? res(region) : rej()
      }
      rej()
    })
  })
}

async function setBjdFilterToMapCenter() {
  if (!map.value) return
  const center = map.value.getCenter()
  await setBjdFilter(center.getLat(), center.getLng())
}

async function setBjdFilter(lat, lng) {
  const region = await coord2RegionCode(lat, lng)
  selectedSido.value = region.code.slice(0, 2)
  await updateSggList()
  selectedSgg.value = region.code.slice(0, 5)
  await updateUmdList()
  selectedUmd.value = region.code
}

async function moveToCurrentLocation() {
  try {
    const pos = await getCurrentDevicePosition()
    const lat = pos.coords.latitude
    const lng = pos.coords.longitude
    await setBjdFilter(lat, lng)
    if (map.value) {
      const loc = new window.kakao.maps.LatLng(lat, lng)
      if (map.value.getLevel() > 5) map.value.setLevel(5)
      map.value.panTo(loc)
      new window.kakao.maps.Marker({ position: loc, map: map.value })
    }
  } catch {
    console.error('위치 이동 실패')
  }
}

// 아파트 검색
const aptNm = ref('')
const apartments = ref([])
async function searchApt() {
  try {
    const { data } = await axios.get('/api/apartments/apt', {
      params: {
        aptNm: aptNm.value,
        code: selectedUmd.value,
      },
    })
    apartments.value = data
  } catch {
    console.error('아파트 검색 실패')
  }
}

// 사이드바 토글
const openSidebar = ref(false)
function toggleSidebar() {
  openSidebar.value = !openSidebar.value
}

onMounted(async () => {
  await updateSidoList()
  await loadKakaoMapScript()
  const options = { level: 5 }
  try {
    const pos = await getCurrentDevicePosition()
    options.center = new window.kakao.maps.LatLng(pos.coords.latitude, pos.coords.longitude)
  } catch {
    options.center = new window.kakao.maps.LatLng(37.5665, 126.978)
  }
  map.value = new window.kakao.maps.Map(mapContainer.value, options)
  new window.kakao.maps.Marker({ position: options.center, map: map.value })
  map.value.addControl(new window.kakao.maps.ZoomControl(), window.kakao.maps.ControlPosition.RIGHT)
})
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
}
.house-map__selete_current_position_button {
  margin: 0;
  padding: 0;
  background: none;
  border: 2px solid #cccccc;
  border-radius: 8px;
  padding: 2px;
  cursor: pointer;
  font-size: 20px;
}
.house-map__selete_current_position_button:hover {
  background-color: #e5e7eb;
}

.house-map__search-filter-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
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

/* 관심지역 ★ 스타일 */
.breadcrumb-star-btn {
  background: none;
  border: none;
  cursor: pointer;
  margin-left: 8px;
}
.breadcrumb-star-btn .fa-star {
  font-size: 18px;
  color: #cbd5e1;
  transition: color 0.2s;
}
.breadcrumb-star-btn .fa-star.starred {
  color: #fbbf24;
}
</style>
