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
            <button class="house-map__search-button" @click="searchApt">
              <i class="fas fa-search"></i>
            </button>
            <input
              ref="searchInput"
              type="text"
              placeholder="검색"
              v-model="aptNm"
              @keydown.enter="searchApt"
              class="house-map__search-keyword"
            />
          </div>
        </article>

        <!-- 검색 필터 및 관심지역 ★ -->
        <article class="house-map__search-filters">
          <div class="house-map__search-filter-header">
            <button
              class="house-map__selete_current_position_button"
              @click="setLwdCdFilterToMapCenter"
              :disabled="isLocationLoading"
            >
              <i class="fas fa-crosshairs" :class="{ 'fa-spin': isLocationLoading }"></i>
            </button>
            <div class="house-map__search-filter-wrapper">
              <!-- 시/도 선택 -->
              <select
                class="house-map__search-selectBox"
                v-model="selectedSido"
                @change="onSidoChange"
              >
                <option value="" disabled>시/도</option>
                <option value="x">선택해제</option>
                <option v-for="s in sidoList" :key="s.code" :value="s.code">
                  {{ s.sidoName }}
                </option>
              </select>
              <i class="fas fa-chevron-right"></i>

              <!-- 시/군/구 선택 -->
              <select
                class="house-map__search-selectBox"
                v-model="selectedSgg"
                @change="onSggChange"
              >
                <option value="" disabled>시/군/구</option>
                <option value="x">선택해제</option>
                <option v-for="g in sggList" :key="g.code" :value="g.code">
                  {{ g.sggName }}
                </option>
              </select>
              <i class="fas fa-chevron-right"></i>

              <!-- 읍/면/동 선택 -->
              <select
                class="house-map__search-selectBox"
                v-model="selectedUmd"
                @change="onUmdChange"
              >
                <option value="" disabled>읍/면/동</option>
                <option value="x">선택해제</option>
                <option v-for="u in umdList" :key="u.code" :value="u.code">{{ u.umdName }}</option>
              </select>

              <!-- 관심지역 토글 버튼 -->
              <button class="house-map__favorite-button" @click="toggleFavoriteRegion">
                <i
                  class="fa-solid fa-star house-map__favorite-button-icon"
                  :class="{ starred: isStarred }"
                ></i>
              </button>
            </div>
          </div>
        </article>
      </section>

      <!-- 아파트 검색 결과 -->
      <section class="house-map__sidebar-thread">
        <div class="house-map__sidebar-thread-container">
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
          <div v-else class="house-map__apt-info" v-for="item in apartments" :key="item.aptSeq">
            <div class="house-map__apt-info-container" @click="selectApt(item)">
              <div class="house-map__apt-info-title">{{ item.aptNm }}</div>
              <ul class="house-map__apt-info-detail">
                <li v-if="item.deals && item.deals.length">
                  <span>{{
                    Math.min(...item.deals.map((d) => +d.dealAmount.replace(/,/g, '')))
                  }}</span>
                  -
                  <span>{{
                    Math.max(...item.deals.map((d) => +d.dealAmount.replace(/,/g, '')))
                  }}</span>
                  만원
                </li>
                <li v-if="item.deals && item.deals.length">
                  <span>{{ Math.min(...item.deals.map((d) => +d.excluUseAr)) }}</span> -
                  <span>{{ Math.max(...item.deals.map((d) => +d.excluUseAr)) }}</span> ㎡
                </li>
                <li>
                  <div style="display: flex; justify-content: space-between">
                    <span v-if="item.deals && item.deals.length">
                      거래 이력 수: {{ item.deals.length }}
                    </span>
                    <span v-else class="no-deals">거래 이력 없음</span>
                    <button
                      class="house-map__favorite-button"
                      @click.stop="toggleAptFavorite(item)"
                    >
                      <i
                        class="fa-solid fa-star house-map__favorite-button-icon"
                        :class="{ starred: isAptFavorite(item.aptSeq) }"
                      ></i>
                    </button>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </section>
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
      <div v-if="!isMapLoaded" class="house-map__map-placeholder">
        지도를 로드할 수 없습니다.<br />나중에 다시 시도해 주세요.
      </div>
    </section>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import axios from 'axios'

const useLwdCd = () => {
  const sidoList = ref([])
  const sggList = ref([])
  const umdList = ref([])
  const selectedSido = ref('')
  const selectedSgg = ref('')
  const selectedUmd = ref('')

  const updateSidoList = async () => {
    try {
      const { data } = await axios.get('/api/lwdCd/sido')
      console.log('updateSidoList 응답결과', data)
      sidoList.value = data
      console.log('시/도 목록 로드 완료:', data.length)
    } catch (error) {
      console.error('시/도 목록 로드 실패:', error)
    }
  }

  const selectLocation = async (regionCode) => {
    if (!regionCode) return

    try {
      console.log('selectLocation 호출:', regionCode)
      // 시/도 설정
      const sidoCode = regionCode.slice(0, 2)
      selectedSido.value = sidoCode + '00000000'
      console.log('시/도 설정:', sidoCode)

      // 시/군/구 목록 로드 및 설정
      const { data: sggData } = await axios.get(`/api/lwdCd/sgg/${sidoCode}`)
      sggList.value = sggData
      console.log('시/군/구 목록 로드:', sggData.length)

      const sggCode = regionCode.slice(0, 5)
      selectedSgg.value = sggCode + '00000'
      console.log('시/군/구 설정:', sggCode)

      // 읍/면/동 목록 로드 및 설정
      const { data: umdData } = await axios.get(`/api/lwdCd/umd/${sggCode}`)
      umdList.value = umdData
      console.log('읍/면/동 목록 로드:', umdData.length)

      selectedUmd.value = regionCode
      console.log('읍/면/동 설정:', regionCode)

      console.log('지역 선택 완료:', { sidoCode, sggCode, regionCode })
    } catch (error) {
      console.error('지역 선택 실패:', error)
      throw error
    }
  }

  const getLwdCdFullName = (lwdCd) => {
    if (!lwdCd) return ''
    return `${lwdCd.sidoName || ''} ${lwdCd.sggName || ''} ${lwdCd.umdName || ''}`.trim()
  }

  return {
    updateSidoList,
    sidoList,
    sggList,
    umdList,
    selectedSido,
    selectedSgg,
    selectedUmd,
    selectLocation,
    getLwdCdFullName,
  }
}

const useKakaoMap = () => {
  const DEFAULT_DISPLAY_LEVEL = 5

  const getCurrentDevicePosition = () => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('이 브라우저에서는 위치 서비스를 지원하지 않습니다.'))
        return
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          console.log('현재 위치 조회 성공:', position.coords)
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          })
        },
        (error) => {
          console.error('위치 조회 실패:', error)
          reject(error)
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 300000,
        },
      )
    })
  }

  const coordToRegionCode = (latitude, longitude) => {
    return new Promise((resolve, reject) => {
      if (!window.kakao || !window.kakao.maps) {
        reject(new Error('카카오맵이 로드되지 않았습니다'))
        return
      }

      const geocoder = new window.kakao.maps.services.Geocoder()
      geocoder.coord2RegionCode(longitude, latitude, (result, status) => {
        if (status === window.kakao.maps.services.Status.OK) {
          const region = result.find((r) => r.region_type === 'B')
          if (region) {
            resolve({ code: region.code })
          } else {
            reject(new Error('지역 정보를 찾을 수 없습니다'))
          }
        } else {
          reject(new Error('좌표를 주소로 변환할 수 없습니다'))
        }
      })
    })
  }

  const addressToPosition = (address) => {
    return new Promise((resolve, reject) => {
      const geocoder = new window.kakao.maps.services.Geocoder()
      geocoder.addressSearch(address, (result, status) => {
        if (status === window.kakao.maps.services.Status.OK) {
          resolve({
            latitude: parseFloat(result[0].y),
            longitude: parseFloat(result[0].x),
          })
        } else {
          reject(new Error('주소 검색 실패'))
        }
      })
    })
  }

  const loadKakaoMapScript = () => {
    if (window.kakao) return Promise.resolve()

    return new Promise((resolve) => {
      const script = document.createElement('script')
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_JS_KEY}&autoload=false&libraries=services`
      script.async = true
      script.onload = () => window.kakao.maps.load(resolve)
      document.head.appendChild(script)
    })
  }

  const initMap = async (container) => {
    await loadKakaoMapScript()

    const options = { level: DEFAULT_DISPLAY_LEVEL }

    try {
      const position = await getCurrentDevicePosition()
      options.center = new window.kakao.maps.LatLng(position.latitude, position.longitude)
    } catch (error) {
      console.error('현재 위치를 가져올 수 없습니다. 기본 위치를 사용합니다.', error)
      options.center = new window.kakao.maps.LatLng(37.5665, 126.978) // 서울시청
    }

    const map = new window.kakao.maps.Map(container, options)

    // 줌 컨트롤 추가
    const zoomControl = new window.kakao.maps.ZoomControl()
    map.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT)

    return map
  }

  return {
    DEFAULT_DISPLAY_LEVEL,
    getCurrentDevicePosition,
    coordToRegionCode,
    addressToPosition,
    initMap,
  }
}

// 유틸리티 함수들 사용 (HouseMapView2 방식)
const {
  updateSidoList,
  sidoList,
  sggList,
  umdList,
  selectedSido,
  selectedSgg,
  selectedUmd,
  selectLocation,
  getLwdCdFullName,
} = useLwdCd()

const {
  DEFAULT_DISPLAY_LEVEL,
  getCurrentDevicePosition,
  coordToRegionCode,
  addressToPosition,
  initMap,
} = useKakaoMap()

// 위치 로딩 상태
const isLocationLoading = ref(false)

// 관심 모드 상태
const isInterestMode = ref(false)

// 로딩 상태
const isLoading = ref(false)

// 지도 로딩 상태
const isMapLoaded = ref(false)

// 검색어
const aptNm = ref('')

// 아파트 목록 및 선택된 아파트
const apartments = ref([])
const selectedApt = ref(null)

// 찜 상태
const isStarred = ref(false)

// 검색 input ref
const searchInput = ref(null)

// 맵 컨테이너 ref
const mapContainer = ref(null)
let map = null
const markers = ref([])
const currentLocationMarker = ref(null)

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

// JWT 토큰 설정
const token = localStorage.getItem('authToken')
if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

// 선택된 읍/면/동 변경 시 관심 지역 여부 조회
watch(
  selectedUmd,
  async (newCode) => {
    if (!newCode) {
      isStarred.value = false
      return
    }
    try {
      const { data } = await axios.get('/api/interestRegion/isInterestRegion', {
        params: { lwdCd: newCode + '00' },
      })
      const clean = String(data).trim().replace(/^"|"$/g, '')
      isStarred.value = clean.toLowerCase() === 'true'
    } catch {
      isStarred.value = false
    }
  },
  { immediate: true },
)

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

function selectApt(apt) {
  selectedApt.value = apt
  openDetailSidebar()

  // 지도에서 해당 아파트 위치로 이동
  if (map && apt.lat && apt.lng) {
    const position = new window.kakao.maps.LatLng(apt.lat, apt.lng)
    map.panTo(position)
  }
}

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

function openAiSearchSidebar() {
  toggleSearchSidebar()
}

function openMoreSidebar() {
  toggleSearchSidebar()
}

// 마커 제어 (HouseMapView2 방식)
function clearMarkers() {
  markers.value.forEach((marker) => marker.setMap(null))
  markers.value.length = 0
}

// 아파트 검색
async function searchApt() {
  isInterestMode.value = false
  isLoading.value = true

  try {
    const { data } = await axios.get('/api/apartments/apt', {
      params: {
        aptNm: aptNm.value,
        code:
          selectedUmd.value.slice(5, 10) ||
          selectedSgg.value.slice(0, 5) ||
          selectedSido.value.slice(0, 2),
      },
    })
    apartments.value = data

    // 마커 표시
    clearMarkers()
    const markerPromises = apartments.value.map(async (apartment) => {
      try {
        let position = null

        // 이미 좌표가 있는 경우
        if (apartment.lat && apartment.lng) {
          position = new window.kakao.maps.LatLng(apartment.lat, apartment.lng)
        }
        // 좌표가 없는 경우 주소로 검색
        else if (apartment.sggCd && apartment.umdCd) {
          const { data: lwdCd } = await axios.get(`/api/lwdCd/${apartment.sggCd + apartment.umdCd}`)
          const address =
            getLwdCdFullName(lwdCd) +
            ' ' +
            Number(apartment.bonbun) +
            (Number(apartment.bubun) ? '-' + Number(apartment.bubun) : '')

          const pos = await addressToPosition(address)
          position = new window.kakao.maps.LatLng(pos.latitude, pos.longitude)
        }

        if (position) {
          const marker = new window.kakao.maps.Marker({
            position: position,
            map: map,
            title: apartment.aptNm,
          })

          // 마커 클릭 이벤트
          window.kakao.maps.event.addListener(marker, 'click', () => {
            selectApt(apartment)
          })

          return marker
        }
        return null
      } catch (error) {
        console.error('지역 코드 정보를 가져오는 중 오류 발생:', error)
        return null
      }
    })

    const results = await Promise.all(markerPromises)
    results.forEach((marker) => {
      if (marker) markers.value.push(marker)
    })

    await checkFavoriteStatusForApartments(data)
  } catch (error) {
    console.error('아파트 검색 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 지역으로 검색
async function searchByRegion() {
  if (selectedUmd.value === 'x') {
    selectedUmd.value = ''
    return
  }

  if (isInterestMode.value) {
    loadInterestApartments()
    return
  }

  if (!selectedUmd.value) return

  await searchApt()
}

// 관심 아파트 목록 로드
async function loadInterestApartments() {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인 후 이용 가능합니다.')
    return
  }

  isLoading.value = true
  try {
    const params = {}
    if (aptNm.value.trim()) params.aptName = aptNm.value.trim()
    if (selectedSido.value) params.si = selectedSido.value
    if (selectedSgg.value) params.gun = selectedSgg.value.slice(2, 5)
    if (selectedUmd.value) params.gu = selectedUmd.value.slice(5)

    const { data } = await axios.get('/api/interestHouse/interestHouses', {
      params,
      headers: { Authorization: `Bearer ${token}` },
    })

    apartments.value = data
    await checkFavoriteStatusForApartments(data)
  } catch (error) {
    console.error('관심 아파트 목록 로드 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 현재 위치로 이동 (HouseMapView2 방식)
async function moveToCurrentLocation() {
  if (!map) return

  try {
    const position = await getCurrentDevicePosition()
    const latLng = new window.kakao.maps.LatLng(position.latitude, position.longitude)

    map.setLevel(DEFAULT_DISPLAY_LEVEL)
    map.panTo(latLng)

    // 기존 현재 위치 마커 제거
    if (currentLocationMarker.value) {
      currentLocationMarker.value.setMap(null)
    }

    // 새로운 현재 위치 마커 생성
    currentLocationMarker.value = new window.kakao.maps.Marker({
      position: latLng,
      map: map,
      title: '현재 위치',
    })

    // 현재 위치 기반으로 법정동 코드 조회
    await setLwdCdFilter(position.latitude, position.longitude)
  } catch (error) {
    console.error('현재 위치를 가져올 수 없습니다:', error)
    alert('현재 위치를 가져올 수 없습니다. 위치 서비스를 확인해주세요.')
  }
}

// 읍면동 필터를 맵 중심으로 설정 (HouseMapView2 방식)
async function setLwdCdFilterToMapCenter() {
  if (!map) return

  isLocationLoading.value = true
  try {
    const center = map.getCenter()
    await setLwdCdFilter(center.getLat(), center.getLng())
  } catch (error) {
    console.error('지역 설정 실패:', error)
    alert('지역 정보를 가져올 수 없습니다.')
  } finally {
    isLocationLoading.value = false
  }
}

// HouseMapView2 방식의 setLwdCdFilter 함수
async function setLwdCdFilter(latitude, longitude) {
  try {
    console.log('setLwdCdFilter 호출:', { latitude, longitude })

    const region = await coordToRegionCode(latitude, longitude)
    console.log('좌표 변환 결과:', region)

    await selectLocation(region.code)
    console.log('지역 설정 완료:', region.code)
  } catch (error) {
    console.error('지역 설정 중 오류:', error)
    throw error
  }
}

// 관심지역 토글 (HouseMapView2 방식)
async function toggleFavoriteRegion() {
  if (!token) return alert('로그인 해주세요.')
  if (!selectedUmd.value) return

  try {
    if (isStarred.value) {
      await axios.delete('/api/interestRegion', {
        params: { lwdCd: selectedUmd.value + '00' },
      })
      isStarred.value = false
    } else {
      await axios.post('/api/interestRegion', { lwdCd: selectedUmd.value + '00' })
      isStarred.value = true
    }
  } catch (error) {
    console.error('관심지역 업데이트 실패:', error)
  }
}

// 아파트 관심 등록 여부 확인
function isAptFavorite(aptSeq) {
  return favoriteApts.value.has(aptSeq)
}

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
      await axios.delete('/api/interestHouse', {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        data: { aptSeq },
      })
      favoriteApts.value.delete(aptSeq)
    } else {
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

async function checkFavoriteStatusForApartments(apts) {
  const token = localStorage.getItem('authToken')
  if (!token) return

  favoriteApts.value.clear()

  for (const apt of apts) {
    const isFavorite = await checkIsAptFavorite(apt.aptSeq)
    if (isFavorite) {
      favoriteApts.value.add(apt.aptSeq)
    }
  }
}

// 드래그 관련 함수들
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

function onDrag(event) {
  if (!isDragging.value) return

  const newX = event.clientX - dragOffset.value.x
  const newY = event.clientY - dragOffset.value.y

  const maxX = window.innerWidth - 80
  const maxY = window.innerHeight - 200

  sidebarPosition.value = {
    x: Math.max(0, Math.min(newX, maxX)),
    y: Math.max(0, Math.min(newY, maxY)),
  }
}

function stopDrag() {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// 유틸리티 함수들
function formatPrice(price) {
  if (!price) return '0'
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function formatDealDate(deal) {
  if (!deal.dealYmd) return '날짜 정보 없음'
  const year = deal.dealYmd.substring(0, 4)
  const month = deal.dealYmd.substring(4, 6)
  const day = deal.dealDay ? String(deal.dealDay).padStart(2, '0') : '??'
  return `${year}.${month}.${day}`
}

function minPrice(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.min(...apt.deals.map((deal) => parseInt(deal.dealAmount.replace(/,/g, '')) || 0))
}

function maxPrice(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.max(...apt.deals.map((deal) => parseInt(deal.dealAmount.replace(/,/g, '')) || 0))
}

function minSize(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.min(...apt.deals.map((deal) => deal.excluUseAr || 0))
}

function maxSize(apt) {
  if (!apt.deals || apt.deals.length === 0) return 0
  return Math.max(...apt.deals.map((deal) => deal.excluUseAr || 0))
}

// 시/도 변경 처리
async function onSidoChange() {
  if (selectedSido.value === 'x') {
    selectedSido.value = ''
    selectedSgg.value = ''
    selectedUmd.value = ''
    sggList.value = []
    umdList.value = []
    return
  }

  if (selectedSido.value) {
    try {
      const { data: sggData } = await axios.get(`/api/lwdCd/sgg/${selectedSido.value.slice(0, 2)}`)
      sggList.value = sggData
      selectedSgg.value = ''
      selectedUmd.value = ''
      umdList.value = []
    } catch (error) {
      console.error('시/군/구 목록 로드 실패:', error)
    }
  }
}

// 시/군/구 변경 처리
async function onSggChange() {
  if (selectedSgg.value === 'x') {
    selectedSgg.value = ''
    selectedUmd.value = ''
    umdList.value = []
    return
  }

  if (selectedSgg.value) {
    try {
      const { data: umdData } = await axios.get(`/api/lwdCd/umd/${selectedSgg.value.slice(0, 5)}`)
      umdList.value = umdData
      selectedUmd.value = ''
    } catch (error) {
      console.error('읍/면/동 목록 로드 실패:', error)
    }
  }
}

// 읍/면/동 변경 처리
function onUmdChange() {
  if (selectedUmd.value === 'x') {
    selectedUmd.value = ''
    return
  }

  if (selectedUmd.value) {
    searchByRegion()
  }
}

// 컴포넌트 마운트 시 초기화 (HouseMapView2 방식)
onMounted(async () => {
  console.log('컴포넌트 마운트됨')

  // 법정동 목록 로드
  await updateSidoList()

  // 지도 초기화
  if ((map = await initMap(mapContainer.value))) {
    isMapLoaded.value = true
  }

  // 현재 위치 기반 지역 설정
  await setLwdCdFilterToMapCenter()

  // 검색 사이드바 열기
  toggleSearchSidebar()

  // 포커스 설정
  nextTick(() => {
    if (searchInput.value) {
      searchInput.value.focus()
    }
  })
})

// 컴포넌트 언마운트 시 정리
onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)

  if (currentLocationMarker.value) {
    currentLocationMarker.value.setMap(null)
  }
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
  width: 350px;
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
  padding: 0 10px;
  border-bottom: 1px solid #3c3c3c;
}

.house-map__sidebar-search-box {
  margin-bottom: 8px;
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

.house-map__search-filters {
  font-size: 14px;
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

.house-map__favorite-button {
  background: none;
  border: none;
  cursor: pointer;
  margin-left: 8px;
}

.house-map__favorite-button-icon {
  font-size: 18px;
  color: #cbd5e1;
  transition: color 0.2s;
}

.house-map__favorite-button-icon.starred {
  color: #fbbf24;
}

/* 아파트 검색 결과 (HouseMapView2 스타일) */
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
  background-color: white;
  padding: 5px;
}

.house-map__apt-info-container {
  border: 1px solid #d1d5db;
  border-radius: 4px;
  padding: 5px;
  cursor: pointer;
}

.house-map__apt-info-container:hover {
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
  color: #374151;
}

.loading-indicator {
  text-align: center;
  padding: 20px;
  color: #6b7280;
}

.empty-state {
  text-align: center;
  padding: 20px;
  color: #6b7280;
  font-style: italic;
}

.no-deals {
  color: #6b7280;
  font-style: italic;
}
</style>
