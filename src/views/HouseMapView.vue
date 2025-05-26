<template>
  <section class="house-map">
    <!-- 첫 번째 사이드바: 네비게이션 아이콘 -->
    <nav
      ref="sidebarNavRef"
      class="house-map__sidebar-nav"
      :class="{ dragging: isDragging }"
      :style="{
        left: sidebarPosition.x + 'px',
        top: sidebarPosition.y + 'px',
        cursor: isDragging ? 'grabbing' : 'grab',
      }"
      @mousedown="startDrag"
    >
      <ul class="house-map__sidebar-nav-list">
        <li class="house-map__sidebar-nav-item" @click="moveToCurrentLocation">
          <i class="fa-solid fa-crosshairs fa-lg"></i>
          <span>내 위치</span>
        </li>
        <li class="house-map__sidebar-nav-item" @click="toggleSearchSidebar">
          <i class="fa-solid fa-search fa-lg"></i>
          <span>검색</span>
        </li>
        <li class="house-map__sidebar-nav-item" @click="toggleInterestMode">
          <i class="fa-solid fa-heart fa-lg"></i>
          <span>관심</span>
        </li>
        <li class="house-map__sidebar-nav-item" @click="openAiSearchSidebar">
          <i class="fa-solid fa-robot fa-lg"></i>
          <span>AI 검색</span>
        </li>
        <li class="house-map__sidebar-nav-item" @click="openMoreSidebar">
          <i class="fa-solid fa-ellipsis-h fa-lg"></i>
          <span>더보기</span>
        </li>
      </ul>
    </nav>

    <!-- 두 번째 사이드바: 검색 및 아파트 목록 -->
    <aside :class="['sidebar', 'search-sidebar', { active: showSearchSidebar }]">
      <div class="sidebar-header">
        <h2>{{ isInterestMode ? '관심 아파트' : '매물 검색' }}</h2>
        <button class="close-btn" @click="closeSearchSidebar">
          <i class="fa-solid fa-times"></i>
        </button>
      </div>

      <!-- 검색 헤더 -->
      <section class="house-map__search">
        <article class="house-map__sidebar-search-box">
          <div class="house-map__search-input-wrapper">
            <button class="house-map__search-button" @click="searchByKeyword">
              <i class="fas fa-search"></i>
            </button>
            <input
              ref="searchInput"
              type="text"
              placeholder="아파트 이름 검색"
              v-model="aptNm"
              @keydown.enter="searchByKeyword"
              class="house-map__search-keyword"
            />
          </div>
        </article>
        <article class="house-map__search-filters">
          <div class="house-map__search-filter-header">
            <button
              class="house-map__select-current-position-button"
              @click="setBjdFilterToMapCenter"
            >
              <i class="fas fa-crosshairs"></i>
            </button>
            <div class="house-map__search-filter-wrapper">
              <select
                class="house-map__search-selectBox"
                v-model="selectedSido"
                @change="updateSggList()"
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
              <select
                class="house-map__search-selectBox"
                v-model="selectedUmd"
                @change="searchByRegion"
              >
                <option value="" disabled>읍/면/동</option>
                <option v-for="u in umdList" :key="u.code" :value="u.code">
                  {{ u.umdName }}
                </option>
              </select>
              <button class="breadcrumb-star-btn" @click="toggleStar">
                <i class="fa-solid fa-star" :class="{ starred: isStarred }"></i>
              </button>
            </div>
          </div>
          <!-- 검색 버튼 추가 -->
          <div class="house-map__search-actions">
            <button
              v-if="!isInterestMode"
              class="house-map__search-action-btn"
              @click="searchByKeyword"
            >
              <i class="fas fa-search"></i> 매물 검색
            </button>
            <button
              v-if="isInterestMode"
              class="house-map__search-action-btn interest-btn"
              @click="loadInterestApartments"
            >
              <i class="fas fa-heart"></i> 관심 아파트 검색
            </button>
          </div>
        </article>
      </section>

      <!-- 아파트 목록 -->
      <div class="apartment-list">
        <h3>
          아파트 목록 <span v-if="apartments.length">({{ apartments.length }})</span>
        </h3>
        <div v-if="isLoading" class="loading-indicator">
          <i class="fa-solid fa-spinner fa-spin"></i> 로딩 중...
        </div>
        <div v-else-if="apartments.length === 0" class="empty-state">
          {{
            isInterestMode
              ? '관심 등록된 아파트가 없습니다. 아파트 검색 후 관심 등록해보세요.'
              : '검색 결과가 없습니다. 다른 지역이나 키워드로 검색해보세요.'
          }}
        </div>
        <ul v-else class="summary-list">
          <li
            v-for="apt in apartments"
            :key="apt.aptSeq"
            :class="{ active: selectedApt && selectedApt.aptSeq === apt.aptSeq }"
          >
            <div class="apt-item-content" @click="selectApt(apt)">
              <strong>{{ apt.aptNm }}</strong
              ><br />
              <span v-if="apt.deals && apt.deals.length">
                {{ formatPrice(minPrice(apt)) }} - {{ formatPrice(maxPrice(apt)) }} 만원<br />
                {{ minSize(apt) }} - {{ maxSize(apt) }} ㎡<br />
                거래 이력: {{ apt.deals.length }}
              </span>
              <span v-else class="no-deals"> 거래 이력 없음 </span>
            </div>
            <button class="apt-favorite-btn" @click.stop="toggleAptFavorite(apt)">
              <i class="fa-solid fa-star" :class="{ starred: isAptFavorite(apt.aptSeq) }"></i>
            </button>
          </li>
        </ul>
      </div>
    </aside>

    <!-- 세 번째 사이드바: 아파트 상세 정보 -->
    <aside :class="['sidebar', 'detail-sidebar', { active: showDetailSidebar }]">
      <div class="sidebar-header">
        <h2>{{ selectedApt ? selectedApt.aptNm : '아파트 정보' }}</h2>
        <button class="close-btn" @click="closeDetailSidebar">
          <i class="fa-solid fa-times"></i>
        </button>
      </div>

      <div v-if="selectedApt" class="apartment-detail">
        <div class="apartment-info">
          <p><strong>주소:</strong> {{ selectedApt.jibun || '정보 없음' }}</p>
          <p><strong>건축년도:</strong> {{ selectedApt.buildYear || '정보 없음' }}년</p>
          <p v-if="selectedApt.deals && selectedApt.deals.length">
            <strong>가격대:</strong> {{ formatPrice(minPrice(selectedApt)) }} -
            {{ formatPrice(maxPrice(selectedApt)) }} 만원
          </p>
          <p v-if="selectedApt.deals && selectedApt.deals.length">
            <strong>면적대:</strong> {{ minSize(selectedApt) }} - {{ maxSize(selectedApt) }} ㎡
          </p>
        </div>

        <h3>거래 내역</h3>
        <div v-if="!selectedApt.deals || selectedApt.deals.length === 0" class="empty-state">
          거래 내역이 없습니다.
        </div>
        <ul v-else class="deal-list">
          <li v-for="deal in selectedApt.deals" :key="deal.id" class="deal-item">
            <div class="deal-date">{{ formatDealDate(deal) }}</div>
            <div class="deal-price">{{ formatPrice(deal.dealAmount) }} 만원</div>
            <div class="deal-info">
              <span>{{ deal.excluUseAr }}㎡</span>
              <span>{{ deal.floor }}층</span>
            </div>
          </li>
        </ul>
      </div>
    </aside>

    <!-- 맵 영역 -->
    <section class="house-map__map-container" ref="mapContainer">
      <div v-if="!isMapLoaded" class="house-map__map-placeholder">지도를 로드 중입니다...</div>
    </section>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import axios from 'axios'

// 관심 모드 상태 추가
const isInterestMode = ref(false)

// 로딩 상태
const isLoading = ref(false)

// 지도 로딩 상태
const isMapLoaded = ref(false)

// 검색어
const aptNm = ref('')

// 선택된 지역
const selectedSido = ref('')
const selectedSgg = ref('')
const selectedUmd = ref('')

// 지역 목록
const sidoList = ref([])
const sggList = ref([])
const umdList = ref([])

// 아파트 목록 및 선택된 아파트
const apartments = ref([])
const selectedApt = ref(null)

// 찜 상태
const isStarred = ref(false)

// 검색 input ref
const searchInput = ref(null)

// 맵 컨테이너 ref
const mapContainer = ref(null)
const map = ref(null)
const markers = ref([])

// 사이드바 상태 관리
const showSearchSidebar = ref(false)
const showDetailSidebar = ref(false)

// 드래그 관련 상태
const isDragging = ref(false)
const dragOffset = ref({ x: 0, y: 0 })
const sidebarPosition = ref({ x: 16, y: 16 })
const sidebarNavRef = ref(null)

// 관심 아파트 목록
const favoriteApts = ref(new Set())

// 사이드바 열기/닫기 메서드
function toggleSearchSidebar() {
  showSearchSidebar.value = !showSearchSidebar.value
  if (showSearchSidebar.value) {
    isInterestMode.value = false
  }
}

function closeSearchSidebar() {
  showSearchSidebar.value = false
}

function openDetailSidebar() {
  showDetailSidebar.value = true
}

function closeDetailSidebar() {
  showDetailSidebar.value = false
}

// 아파트 선택 시 상세 사이드바 열기
function selectApt(apt) {
  selectedApt.value = apt
  openDetailSidebar()

  // 지도에서 해당 아파트 위치로 이동
  if (map.value && apt.lat && apt.lng) {
    const position = new window.kakao.maps.LatLng(apt.lat, apt.lng)
    map.value.panTo(position)

    // 마커 강조 표시
    highlightMarker(apt.aptSeq)
  }
}

// 마커 강조 표시
function highlightMarker(aptSeq) {
  markers.value.forEach((marker) => {
    if (marker.aptSeq === aptSeq) {
      // 선택된 마커 스타일 변경
      marker.setZIndex(10)
    } else {
      marker.setZIndex(1)
    }
  })
}

// 추가 사이드바 기능
function toggleInterestMode() {
  isInterestMode.value = true
  showSearchSidebar.value = true

  // 주소 선택 초기화
  selectedSido.value = ''
  selectedSgg.value = ''
  selectedUmd.value = ''
  sggList.value = []
  umdList.value = []

  loadInterestApartments()
}

// 일반 검색 모드로 전환하는 함수 추가
// 사용되지 않는 toggleNormalMode 함수를 제거하고, 대신 searchByKeyword 함수에 모드 전환 로직 추가
async function searchByKeyword() {
  // 검색 시 일반 모드로 전환
  isInterestMode.value = false

  isLoading.value = true
  try {
    let data = []

    if (aptNm.value.trim()) {
      // 아파트 이름으로 검색
      const response = await axios.get('/api/apartments/apt', {
        params: { aptNm: aptNm.value },
      })
      data = response.data
    } else if (selectedUmd.value) {
      // 지역으로 검색
      const response = await axios.get('/api/apartments/region', {
        params: { lwdCd: selectedUmd.value },
      })
      data = response.data
    } else {
      // 검색 조건이 없으면 빈 배열
      data = []
    }

    apartments.value = data
    clearMarkers()
    addApartmentMarkers(data)

    // 검색 결과가 있으면 첫 번째 아파트로 지도 이동
    if (data.length > 0) {
      const firstApt = data[0]
      if (firstApt.lat && firstApt.lng) {
        map.value.panTo(new window.kakao.maps.LatLng(firstApt.lat, firstApt.lng))
      }
    }

    // 검색 결과 아파트들의 관심 등록 여부 확인
    await checkFavoriteStatusForApartments(data)
  } catch (error) {
    console.error('아파트 검색 실패:', error)
  } finally {
    isLoading.value = false
  }
}

function openAiSearchSidebar() {
  // AI 검색 사이드바 열기
  toggleSearchSidebar()
}

function openMoreSidebar() {
  // 더보기 사이드바 열기
  toggleSearchSidebar()
}

// 현재 위치로 이동
async function moveToCurrentLocation() {
  if (!map.value) return

  try {
    const position = await getCurrentDevicePosition()
    const latLng = new window.kakao.maps.LatLng(position.coords.latitude, position.coords.longitude)
    map.value.panTo(latLng)

    // 현재 위치 마커 표시
    new window.kakao.maps.Marker({
      position: latLng,
      map: map.value,
    })

    // 현재 위치 기반으로 법정동 코드 조회
    setBjdFilterToMapCenter()
  } catch (error) {
    console.error('현재 위치를 가져올 수 없습니다:', error)
  }
}

// 지역으로 검색
async function searchByRegion() {
  if (isInterestMode.value) {
    loadInterestApartments()
    return
  }

  if (!selectedUmd.value) return

  isLoading.value = true
  try {
    const { data } = await axios.get('/api/apartments/region', {
      params: { lwdCd: selectedUmd.value },
    })
    apartments.value = data
    clearMarkers()
    addApartmentMarkers(data)

    // 검색 결과가 있으면 첫 번째 아파트로 지도 이동
    if (data.length > 0) {
      const firstApt = data[0]
      if (firstApt.lat && firstApt.lng) {
        map.value.panTo(new window.kakao.maps.LatLng(firstApt.lat, firstApt.lng))
      }
    }

    // 검색 결과 아파트들의 관심 등록 여부 확인
    await checkFavoriteStatusForApartments(data)
  } catch (error) {
    console.error('지역 검색 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 읍면동 필터를 맵 중심으로 설정
function setBjdFilterToMapCenter() {
  if (!map.value) return

  const center = map.value.getCenter()
  const geocoder = new window.kakao.maps.services.Geocoder()

  geocoder.coord2RegionCode(center.getLng(), center.getLat(), async (result, status) => {
    if (status === window.kakao.maps.services.Status.OK) {
      const region = result.find((r) => r.region_type === 'B')
      if (region) {
        selectedSido.value = region.code.slice(0, 2)
        await updateSggList()
        selectedSgg.value = region.code.slice(0, 5)
        await updateUmdList()
        selectedUmd.value = region.code
        searchByRegion()
      }
    }
  })
}

// 시/도 목록 로드
async function updateSidoList() {
  try {
    const { data } = await axios.get('/api/lwdCd/sido')
    sidoList.value = data
  } catch (error) {
    console.error('시/도 목록 로드 실패:', error)
  }
}

// 시/군/구 목록 로드
async function updateSggList() {
  if (!selectedSido.value) {
    sggList.value = []
    return
  }

  try {
    const { data } = await axios.get(`/api/lwdCd/sgg/${selectedSido.value}`)
    sggList.value = data
    selectedSgg.value = ''
    selectedUmd.value = ''
    umdList.value = []
  } catch (error) {
    console.error('시/군/구 목록 로드 실패:', error)
  }
}

// 읍/면/동 목록 로드
async function updateUmdList() {
  if (!selectedSgg.value) {
    umdList.value = []
    return
  }

  try {
    const { data } = await axios.get(`/api/lwdCd/umd/${selectedSgg.value}`)
    umdList.value = data
    selectedUmd.value = ''
  } catch (error) {
    console.error('읍/면/동 목록 로드 실패:', error)
  }
}

// 관심 아파트 목록을 불러오는 함수 추가
async function loadInterestApartments() {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인 후 이용 가능합니다.')
    return
  }

  isLoading.value = true
  try {
    // 선택된 지역 코드가 있으면 사용, 없으면 null로 전달
    const params = {}
    if (aptNm.value.trim()) params.aptName = aptNm.value.trim()
    if (selectedSido.value) params.si = selectedSido.value
    if (selectedSgg.value) params.gun = selectedSgg.value.slice(2, 5) // 5자리 코드에서 뒤 3자리
    if (selectedUmd.value) params.gu = selectedUmd.value.slice(5) // 8자리 코드에서 뒤 3자리

    const { data } = await axios.get('/api/interestHouse/interestHouses', {
      params,
      headers: { Authorization: `Bearer ${token}` },
    })

    apartments.value = data
    clearMarkers()
    addApartmentMarkers(data)

    // 검색 결과가 있으면 첫 번째 아파트로 지도 이동
    if (data.length > 0) {
      const firstApt = data[0]
      if (firstApt.lat && firstApt.lng) {
        map.value.panTo(new window.kakao.maps.LatLng(firstApt.lat, firstApt.lng))
      }
    }

    // 검색 결과 아파트들의 관심 등록 여부 확인
    await checkFavoriteStatusForApartments(data)
  } catch (error) {
    console.error('관심 아파트 목록 로드 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 관심 지역 여부 확인
async function checkIsInterestRegion() {
  if (!selectedUmd.value) {
    isStarred.value = false
    return
  }

  const token = localStorage.getItem('authToken')
  if (!token) {
    isStarred.value = false
    return
  }

  try {
    const { data } = await axios.get('/api/interestRegion/isInterestRegion', {
      params: { lwdCd: selectedUmd.value },
      headers: { Authorization: `Bearer ${token}` },
    })
    isStarred.value = String(data).trim().toLowerCase() === 'true'
  } catch (error) {
    console.error('관심 지역 확인 실패:', error)
    isStarred.value = false
  }
}

// 관심 지역 토글
async function toggleStar() {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인 후 이용 가능합니다.')
    return
  }

  if (!selectedUmd.value) return

  try {
    if (isStarred.value) {
      await axios.delete('/api/interestRegion', {
        params: { lwdCd: selectedUmd.value },
        headers: { Authorization: `Bearer ${token}` },
      })
      isStarred.value = false
    } else {
      await axios.post(
        '/api/interestRegion',
        { lwdCd: selectedUmd.value },
        { headers: { Authorization: `Bearer ${token}` } },
      )
      isStarred.value = true
    }
  } catch (error) {
    console.error('관심 지역 업데이트 실패:', error)
  }
}

// 아파트가 관심 등록되어 있는지 확인
function isAptFavorite(aptSeq) {
  return favoriteApts.value.has(aptSeq)
}

// 아파트 관심 등록 여부 확인
async function checkIsAptFavorite(aptSeq) {
  const token = localStorage.getItem('authToken')
  if (!token) return false

  try {
    const { data } = await axios.get('/api/interestHouse/isInterestHouses', {
      params: { aptSeq },
      headers: { Authorization: `Bearer ${token}` },
    })
    return String(data).trim().toLowerCase() === 'true'
  } catch (error) {
    console.error('관심 아파트 확인 실패:', error)
    return false
  }
}

// 아파트 관심 등록/해제 토글
async function toggleAptFavorite(apt) {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인 후 이용 가능합니다.')
    return
  }

  const aptSeq = apt.aptSeq
  const isFavorite = favoriteApts.value.has(aptSeq)

  try {
    if (isFavorite) {
      // 관심 아파트 해제
      await axios.delete('/api/interestHouse', {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        data: { aptSeq },
      })
      favoriteApts.value.delete(aptSeq)
    } else {
      // 관심 아파트 등록
      await axios.post(
        '/api/interestHouse',
        { aptSeq },
        { headers: { Authorization: `Bearer ${token}` } },
      )
      favoriteApts.value.add(aptSeq)
    }
  } catch (error) {
    console.error('관심 아파트 업데이트 실패:', error)
  }
}

// 아파트 목록의 관심 등록 여부 확인
async function checkFavoriteStatusForApartments(apts) {
  const token = localStorage.getItem('authToken')
  if (!token) return

  // 관심 아파트 목록 초기화
  favoriteApts.value.clear()

  // 각 아파트에 대해 관심 등록 여부 확인
  for (const apt of apts) {
    const isFavorite = await checkIsAptFavorite(apt.aptSeq)
    if (isFavorite) {
      favoriteApts.value.add(apt.aptSeq)
    }
  }
}

// 카카오맵 스크립트 로드
async function loadKakaoMapScript() {
  if (window.kakao) return Promise.resolve()

  return new Promise((resolve) => {
    const script = document.createElement('script')
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_JS_KEY}&autoload=false&libraries=services`
    script.async = true
    script.onload = () => window.kakao.maps.load(resolve)
    document.head.appendChild(script)
  })
}

// 현재 위치 가져오기
function getCurrentDevicePosition() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocation is not supported by this browser.'))
      return
    }

    navigator.geolocation.getCurrentPosition(resolve, reject, {
      enableHighAccuracy: true,
      timeout: 5000,
      maximumAge: 0,
    })
  })
}

// 지도 초기화
async function initMap() {
  await loadKakaoMapScript()

  const options = { level: 5 }

  try {
    const position = await getCurrentDevicePosition()
    options.center = new window.kakao.maps.LatLng(
      position.coords.latitude,
      position.coords.longitude,
    )
  } catch (error) {
    console.error('현재 위치를 가져올 수 없습니다. 기본 위치를 사용합니다.', error)
    options.center = new window.kakao.maps.LatLng(37.5665, 126.978) // 서울시청
  }

  map.value = new window.kakao.maps.Map(mapContainer.value, options)

  // 줌 컨트롤 추가
  const zoomControl = new window.kakao.maps.ZoomControl()
  map.value.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT)

  // 지도 로드 완료
  isMapLoaded.value = true

  // 지도 이동 이벤트 리스너
  window.kakao.maps.event.addListener(map.value, 'dragend', () => {
    // 지도 이동 후 처리 로직
  })
}

// 드래그 시작
function startDrag(event) {
  isDragging.value = true
  const rect = sidebarNavRef.value.getBoundingClientRect()
  dragOffset.value = {
    x: event.clientX - rect.left,
    y: event.clientY - rect.top,
  }
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  event.preventDefault()
}

// 드래그 중
function onDrag(event) {
  if (!isDragging.value) return

  const newX = event.clientX - dragOffset.value.x
  const newY = event.clientY - dragOffset.value.y

  // 화면 경계 제한
  const maxX = window.innerWidth - 80 // 사이드바 너비 고려
  const maxY = window.innerHeight - 200 // 사이드바 높이 고려

  sidebarPosition.value = {
    x: Math.max(0, Math.min(newX, maxX)),
    y: Math.max(0, Math.min(newY, maxY)),
  }
}

// 드래그 종료
function stopDrag() {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// 마커 초기화
function clearMarkers() {
  markers.value.forEach((marker) => marker.setMap(null))
  markers.value = []
}

// 아파트 마커 추가
function addApartmentMarkers(apartments) {
  if (!map.value) return

  apartments.forEach((apt) => {
    if (!apt.lat || !apt.lng) return

    const position = new window.kakao.maps.LatLng(apt.lat, apt.lng)
    const marker = new window.kakao.maps.Marker({
      position,
      map: map.value,
      title: apt.aptNm,
    })

    // 마커에 아파트 정보 저장
    marker.aptSeq = apt.aptSeq

    // 마커 클릭 이벤트
    window.kakao.maps.event.addListener(marker, 'click', () => {
      selectApt(apt)
    })

    markers.value.push(marker)
  })
}

// 가격 포맷팅
function formatPrice(price) {
  if (!price) return '0'
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 거래일자 포맷팅
function formatDealDate(deal) {
  if (!deal.dealYmd) return '날짜 정보 없음'
  const year = deal.dealYmd.substring(0, 4)
  const month = deal.dealYmd.substring(4, 6)
  const day = deal.dealDay ? String(deal.dealDay).padStart(2, '0') : '??'
  return `${year}.${month}.${day}`
}

// 최소 가격
function minPrice(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.min(
    ...apt.deals.map((deal) => {
      const amount =
        typeof deal.dealAmount === 'string'
          ? parseInt(deal.dealAmount.replace(/,/g, ''))
          : deal.dealAmount
      return isNaN(amount) ? 0 : amount
    }),
  )
}

// 최대 가격
function maxPrice(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.max(
    ...apt.deals.map((deal) => {
      const amount =
        typeof deal.dealAmount === 'string'
          ? parseInt(deal.dealAmount.replace(/,/g, ''))
          : deal.dealAmount
      return isNaN(amount) ? 0 : amount
    }),
  )
}

// 최소 면적
function minSize(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.min(...apt.deals.map((deal) => deal.excluUseAr || 0))
}

// 최대 면적
function maxSize(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.max(...apt.deals.map((deal) => deal.excluUseAr || 0))
}

// 선택된 읍면동 변경 시 관심 지역 여부 확인
watch(selectedUmd, () => {
  checkIsInterestRegion()
})

// 컴포넌트 마운트 시 초기화
onMounted(async () => {
  // 법정동 목록 로드
  await updateSidoList()

  // 지도 초기화
  await initMap()

  // 검색 사이드바 열기
  toggleSearchSidebar()

  // 포커스 설정
  nextTick(() => {
    if (searchInput.value) {
      searchInput.value.focus()
    }
  })
})

// 컴포넌트 언마운트 시 이벤트 리스너 정리
onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
})
</script>

<style scoped>
.house-map {
  display: flex;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

/* 첫 번째 사이드바: 네비게이션 아이콘 */
.house-map__sidebar-nav {
  position: absolute;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  user-select: none;
  transition: box-shadow 0.2s ease;
}

.house-map__sidebar-nav:hover {
  cursor: grab;
}

.house-map__sidebar-nav.dragging {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  cursor: grabbing !important;
}

.house-map__sidebar-nav-list {
  list-style: none;
  margin: 0;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.house-map__sidebar-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.house-map__sidebar-nav-item:hover {
  background-color: #f0f9ff;
}

.house-map__sidebar-nav-item span {
  display: block;
  font-size: 12px;
  margin-top: 4px;
}

/* 사이드바 공통 스타일 */
.sidebar {
  position: absolute;
  top: 0;
  height: 100%;
  background: #fff;
  overflow-y: auto;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  z-index: 900;
  width: px;
  padding: 16px;
  transition: transform 0.3s ease;
}

.search-sidebar {
  left: 0;
  transform: translateX(-100%);
}

.search-sidebar.active {
  transform: translateX(0);
}

.detail-sidebar {
  right: 0;
  transform: translateX(100%);
}

.detail-sidebar.active {
  transform: translateX(0);
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #6b7280;
}

.close-btn:hover {
  color: #111827;
}

/* 검색 박스 */
.house-map__search {
  margin-bottom: 16px;
}

.house-map__sidebar-search-box {
  margin-bottom: 8px;
}

.house-map__search-input-wrapper {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
}

.house-map__search-button {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
}

.house-map__search-keyword {
  flex: 1;
  border: none;
  padding: 8px;
  outline: none;
}

.house-map__search-filters {
  font-size: 14px;
}

.house-map__search-filter-header {
  display: flex;
  align-items: center;
}

.house-map__select-current-position-button {
  background: none;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 4px;
  margin-right: 8px;
  cursor: pointer;
}

.house-map__search-filter-wrapper {
  display: flex;
  align-items: center;
  flex: 1;
  flex-wrap: wrap;
  gap: 4px;
}

.house-map__search-selectBox {
  padding: 4px;
  flex: 1;
  min-width: 80px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}

.breadcrumb-star-btn {
  background: none;
  border: none;
  cursor: pointer;
}

.breadcrumb-star-btn .fa-star {
  font-size: 18px;
  color: #bbb;
  transition: color 0.2s;
}

.breadcrumb-star-btn .fa-star.starred {
  color: #fbbf24;
}

/* 아파트 목록 */
.apartment-list {
  margin-top: 16px;
}

.apartment-list h3 {
  font-size: 16px;
  margin-bottom: 8px;
}

.summary-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* 아파트 목록 항목 스타일 수정 */
.summary-list li {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px;
  border-bottom: 1px solid #e5e5e5;
  line-height: 1.4;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.apt-item-content {
  flex: 1;
  cursor: pointer;
}

.apt-favorite-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  margin-left: 8px;
}

.apt-favorite-btn .fa-star {
  font-size: 18px;
  color: #bbb;
  transition: color 0.2s;
}

.apt-favorite-btn .fa-star.starred {
  color: #fbbf24;
}

.summary-list li:hover {
  background-color: #f3f4f6;
}

.summary-list li.active {
  background-color: #e0f7fa;
}

.no-deals {
  color: #6b7280;
  font-style: italic;
}

/* 아파트 상세 정보 */
.apartment-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.apartment-info {
  background-color: #f9fafb;
  padding: 12px;
  border-radius: 8px;
}

.apartment-info p {
  margin: 8px 0;
}

.deal-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.deal-item {
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.deal-date {
  font-weight: 500;
  color: #4b5563;
}

.deal-price {
  font-size: 16px;
  font-weight: bold;
  color: #2563eb;
  margin: 4px 0;
}

.deal-info {
  display: flex;
  gap: 12px;
  color: #6b7280;
  font-size: 14px;
}

/* 맵 영역 */
.house-map__map-container {
  flex: 1;
  background: #e5e7eb;
}

.house-map__map-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #666;
}

.house-map__search-actions {
  margin-top: 8px;
  display: flex;
  justify-content: center;
}

.house-map__search-action-btn {
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.house-map__search-action-btn:hover {
  background-color: #1d4ed8;
}

.house-map__search-action-btn.interest-btn {
  background-color: #dc2626;
}

.house-map__search-action-btn.interest-btn:hover {
  background-color: #b91c1c;
}
</style>
