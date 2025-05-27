<template>
  <section class="house-map">
    <!-- 사이드바 네비게이션 -->
    <nav class="house-map__sidebar-nav">
      <ul class="house-map__sidebar-nav-list">
        <li
          class="house-map__sidebar-nav-item"
          :class="{ active: isSelectedSearch }"
          @click="toggleSearchSidebar"
        >
          <i class="fas fa-search fa-2x"></i>
          <span class="house-map__sidebar-nav-item-label">검색</span>
        </li>
        <li
          class="house-map__sidebar-nav-item"
          :class="{ active: isSelectedFavorite }"
          @click="toggleFavoriteSidebar"
        >
          <i class="fas fa-star fa-2x"></i>
          <span class="house-map__sidebar-nav-item-label">관심</span>
        </li>
      </ul>
    </nav>
    <!-- 리모컨 -->
    <nav class="house-map__remote-control">
      <ul class="house-map__remote-control-list">
        <li class="house-map__remote-control-item" @click="moveToCurrentLocation">
          <i class="fas fa-location-crosshairs fa-2x"></i>
          <span class="house-map__remote-control-item-label">내 위치</span>
        </li>
        <li
          class="house-map__remote-control-item"
          :class="{ active: markersVisibleByType.search.value }"
          @click="toggleSearchMarkers"
        >
          <i
            class="fas fa-2x"
            :class="{
              'fa-location-dot': markersVisibleByType.search.value,
              'fa-location-pin': !markersVisibleByType.search.value,
            }"
          ></i>
          <span class="house-map__remote-control-item-label"> 마커 </span>
        </li>
        <li
          class="house-map__remote-control-item"
          :class="{ active: markersVisibleByType.favorite.value }"
          @click="toggleFavoriteMarkers"
        >
          <i
            class="fa-star fa-2x"
            :class="{
              fas: markersVisibleByType.favorite.value,
              far: !markersVisibleByType.favorite.value,
            }"
          ></i>
          <span class="house-map__remote-control-item-label">관심 </span>
        </li>
      </ul>
    </nav>

    <!--사이드바-->
    <aside class="house-map__sidebar house-map__sidebar-left" :class="{ active: showLeftSidebar }">
      <!-- 검색 영역 -->
      <section class="house-map__search-container">
        <!--검색 상자-->
        <article class="house-map__sidebar-search-box">
          <label for="house-map__siderbar-search-keyword" class="house-map__search-input-wrapper">
            <button class="house-map__search-button" @click="searchApt">
              <i class="fas fa-search"></i>
            </button>
            <input
              id="house-map__siderbar-search-keyword"
              type="text"
              placeholder="검색"
              v-model="aptNm"
              @keydown.enter="searchApt"
              class="house-map__search-keyword"
            />
          </label>
        </article>

        <!-- 검색 필터 -->
        <article class="house-map__search-filters">
          <div class="house-map__search-filter-header">
            <!-- 현위치로 법정동 지정 -->
            <button
              class="house-map__set-map-center-button"
              @click="setLwdCdFilterToMapCenter"
              :disabled="isMyLocationLoading"
            >
              <i class="fas fa-location-crosshairs" :class="{ 'fa-spin': isMyLocationLoading }"></i>
            </button>
            <!-- 법정동 필터 -->
            <div class="house-map__search-lwdCd-wrapper">
              <!-- 시/도 선택 -->
              <select
                class="house-map__search-selectBox"
                v-model="selectedSido"
                @change="onSidoChanged"
              >
                <option value="">시/도</option>
                <option v-for="sido in sidoList" :key="sido.code" :value="sido.code">
                  {{ sido.name }}
                </option>
              </select>
              <i class="fas fa-chevron-right"></i>

              <!-- 시/군/구 선택 -->
              <select
                class="house-map__search-selectBox"
                v-model="selectedSgg"
                @change="onSggChanged"
              >
                <option value="">시/군/구</option>
                <option v-for="sgg in sggList" :key="sgg.code" :value="sgg.code">
                  {{ sgg.name }}
                </option>
              </select>
              <i class="fas fa-chevron-right"></i>

              <!-- 읍/면/동 선택 -->
              <select
                class="house-map__search-selectBox"
                v-model="selectedUmd"
                @change="onUmdChanged"
              >
                <option value="">읍/면/동</option>
                <option v-for="u in umdList" :key="u.code" :value="u.code">{{ u.name }}</option>
              </select>
            </div>
            <!-- 관심지역 토글 버튼 -->
            <button class="house-map__favorite-button" @click="toggleFavoriteRegion">
              <i
                class="fas fa-star house-map__favorite-button-icon"
                :class="{ starred: isStarred }"
              ></i>
            </button>
          </div>
        </article>
      </section>

      <!-- 아파트 검색 결과 -->
      <section class="house-map__sidebar-thread">
        <div class="house-map__sidebar-thread-container">
          <article v-if="isListLoading" class="loading-indicator">
            <span> <i class="fas fa-spinner fa-spin fa-2x"></i></span>
          </article>
          <article
            v-else-if="isSelectedFavorite && favoriteApartments.length == 0"
            class="empty-state"
          >
            <p v-html="'관심 등록된 아파트가 없습니다.<br> 아파트 검색 후 관심 등록해보세요.'"></p>
          </article>
          <article v-else-if="isSelectedSearch && searchApartments.length == 0" class="empty-state">
            <p v-html="'검색 결과가 없습니다.<br> 다른 지역이나 키워드로 검색해보세요.'"></p>
          </article>
          <article
            v-else
            class="house-map__apt-info"
            v-for="item in leftTap === 'favorite' ? favoriteApartments : searchApartments"
            :key="item.aptSeq"
          >
            <div
              class="house-map__apt-info-container"
              @click="selectApt(item)"
              @mouseover="moveToLocaation(item.latitude, item.longitude)"
            >
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
                        class="fas fa-star house-map__favorite-button-icon"
                        :class="{ starred: isAptFavorite(item.aptSeq) }"
                      ></i>
                    </button>
                  </div>
                </li>
              </ul>
            </div>
          </article>
        </div>
      </section>
    </aside>

    <!-- 세 번째 사이드바: 아파트 상세 정보 -->
    <aside
      :class="['house-map__sidebar', 'house-map__sidebar-right', { active: showRightSidebar }]"
    >
      <div class="sidebar-header">
        <h2>{{ selectedApt ? selectedApt.aptNm : '아파트 정보' }}</h2>
        <button class="close-btn" @click="closeRightSidebar">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div v-if="selectedApt" class="apartment-detail">
        <div class="apartment-info">
          <p><strong>주소:</strong> {{ selectedAptAddress || '정보 없음' }}</p>
          <p><strong>건축년도:</strong> {{ selectedApt.buildYear || '정보 없음' }}년</p>
          <p v-if="selectedApt.deals && selectedApt.deals.length">
            <strong>가격대:</strong> {{ formatPrice(minPrice(selectedApt)) }} -
            {{ formatPrice(maxPrice(selectedApt)) }} 만원
          </p>
          <p v-if="selectedApt.deals && selectedApt.deals.length">
            <strong>면적대:</strong> {{ minSize(selectedApt) }} - {{ maxSize(selectedApt) }} ㎡
          </p>
        </div>

        <!-- 가격 추이 차트 -->
        <div
          v-if="selectedApt && selectedApt.deals && selectedApt.deals.length > 1"
          class="price-chart-section"
        >
          <h3>1평당 가격 추이</h3>
          <div ref="chartRef" class="deal-chart"></div>
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
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import baseURL from '@/baseURL'
import { useLwdCd } from '@/utils/useLwdCd'
import { useKakaoMap } from '@/utils/useKakaoMap'
import { useMarker } from '@/utils/useMaker.js'

const router = useRouter()

// JWT 토큰 설정
const token = localStorage.getItem('authToken')
if (token) baseURL.defaults.headers.common['Authorization'] = `Bearer ${token}`

// 차트 ref 추가
const chartRef = ref(null)

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

const { DEFAULT_DISPLAY_LEVEL, getCurrentDevicePosition, coordToRegionCode, initMap } =
  useKakaoMap()

const { markersByType, markersVisibleByType, clustererByType, toggleMarkers, createMarkers } =
  useMarker()

// Map 관련 상태
const mapContainer = ref(null)
let map = null

// 로딩 관련 상태
const isMapLoaded = ref(false) // 지도
const isMyLocationLoading = ref(false) // my-location
const isListLoading = ref(false) // 검색/관심 목록

// 사이드바 관리
const showLeftSidebar = ref(false) // 좌측: 검색/관심 목록
const showRightSidebar = ref(false) // 우측: 상세 조회
const leftTap = ref('') // 좌측 사이드바 탭 상태

// 사이드바 상태
const isSelectedSearch = computed(() => showLeftSidebar.value && leftTap.value === 'search')
const isSelectedFavorite = computed(() => showLeftSidebar.value && leftTap.value === 'favorite')

// 검색어
const aptNm = ref('')

// 아파트 목록 및 선택된 아파트
const selectedApt = ref(null)
const selectedAptAddress = ref('')
watch(selectedApt, async (cv) => {
  if (!cv) selectedAptAddress.value = ''
  const { sggCd, umdCd, bonbun, bubun } = selectedApt.value
  const { data: lwdCd } = await baseURL.get(`/api/lwdCd/${sggCd + umdCd}`)
  selectedAptAddress.value =
    (await getLwdCdFullName(lwdCd)) +
    ' ' +
    Number(bonbun) +
    (Number(bubun) ? '-' + Number(bubun) : '')
  console.log('selectedAptAddress', selectedAptAddress.value)
})

// 관심 아이템 관리
const isStarred = ref(false) // 관심 지역
const favoriteApts = ref(new Set()) // 관심 아파트

watch(
  selectedApt,
  () => {
    nextTick(() => {
      createPriceChart()
    })
  },
  { deep: true },
)

/* 사이드바 제어 */
// 좌측 사이드바 검색 탭 토글
async function toggleSearchSidebar() {
  if (showLeftSidebar.value && leftTap.value === 'search') {
    showLeftSidebar.value = false
    leftTap.value = ''
  } else {
    showLeftSidebar.value = true
    leftTap.value = 'search'
    searchApt()
  }
}
// 좌측 사이드바 관심 탭 토글
async function toggleFavoriteSidebar() {
  if (showLeftSidebar.value && leftTap.value === 'favorite') {
    showLeftSidebar.value = false
    leftTap.value = ''
  } else {
    showLeftSidebar.value = true
    leftTap.value = 'favorite'
    loadFavoriteApartments()
  }
}
// 우측 사이드바 열기
function openRightSidebar() {
  showRightSidebar.value = true
}
// 우측 사이드바 닫기
function closeRightSidebar() {
  showRightSidebar.value = false
}

async function selectApt(apt) {
  selectedApt.value = apt
  if (apt.latitude && apt.longitude) {
    moveToLocaation(apt.latitude, apt.longitude)
  }
  openRightSidebar()
}

// 마커 제어
const searchApartments = ref([]) // 검색 아파트 목록
const favoriteApartments = ref([]) // 관심 아파트 목록

const toggleSearchMarkers = () => toggleMarkers('search', map)
const toggleFavoriteMarkers = () => toggleMarkers('favorite', map)

/* 아파트 조회 */
// 아파트 검색
async function searchApt() {
  leftTap.value = 'search'
  isListLoading.value = true

  try {
    // 아파트 검색
    const { data } = await baseURL.get('/api/apartments/apt', {
      params: {
        aptNm: aptNm.value.trim(),
        // code: selectedUmd.value || selectedSgg.value || selectedSido.value || '',
        code: selectedUmd.value || selectedSgg.value || '',
      },
    })
    searchApartments.value = data

    // 마커 표시
    await createMarkers('search', searchApartments, map, async (apt) => {
      const { data: lwdCd } = await baseURL.get(`/api/lwdCd/${apt.sggCd + apt.umdCd}`)
      return getLwdCdFullName(lwdCd) + ' ' + apt.bonbun + (apt.bubun ? '-' + apt.bubun : '')
    })

    markersByType.search.forEach((marker) => {
      // 마커/아파트에 추가 정보 할당
      const apartment = marker.item
      marker.setTitle(apartment.aptNm)

      const latLng = marker.getPosition()
      apartment.latitude = latLng.getLat()
      apartment.longitude = latLng.getLng()

      window.kakao.maps.event.addListener(marker, 'click', () => selectApt(apartment))
    })

    await checkFavoriteStatusForApartments(data)
  } catch (error) {
    console.error('아파트 검색 실패:', error)
  } finally {
    isListLoading.value = false
  }
  await loadFavoriteApartments()
}
// 지역으로 검색
async function searchAptByLwdCd() {
  const cached = aptNm.value
  // aptNm.value = ''
  searchApt()
  aptNm.value = cached
}

// 관심 아파트 검색
async function loadFavoriteApartments() {
  const token = localStorage.getItem('authToken')
  if (!token) {
    alert('로그인 후 이용 가능합니다.')
    router.push({ name: 'Login' })
    return
  }

  isListLoading.value = true
  try {
    const params = {}
    // const code = selectedUmd.value || selectedSgg.value || selectedSido.value || ''
    const code = selectedUmd.value || selectedSgg.value || ''

    if (aptNm.value.trim()) params.aptName = aptNm.value.trim()
    const codeLength = code.length
    if (codeLength == 2 || codeLength == 5 || codeLength == 8 || codeLength == 10) {
      if (codeLength >= 2) params.si = code.slice(0, 2)
      if (codeLength >= 5) params.gun = code.slice(2, 5)
      if (codeLength >= 8) params.gu = code.slice(5, 8)
    }
    const { data } = await baseURL.get('/api/interestHouse/interestHouses', {
      headers: { Authorization: `Bearer ${token}` },
      params,
    })

    favoriteApartments.value = data
    await createMarkers(
      'favorite',
      favoriteApartments,
      map,
      async (apt) => {
        const { data: lwdCd } = await baseURL.get(`/api/lwdCd/${apt.sggCd + apt.umdCd}`)
        return getLwdCdFullName(lwdCd) + ' ' + apt.bonbun + (apt.bubun ? '-' + apt.bubun : '')
      },
      {
        markerImage: {
          src: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
          size: [30, 40],
          options: {
            offset: new window.kakao.maps.Point(15, 40),
          },
        },
        zIndex: 10,
      },
    )

    markersByType.favorite.forEach((marker) => {
      // 마커/아파트에 추가 정보 할당
      const apartment = marker.item
      marker.setTitle(apartment.aptNm)

      const latLng = marker.getPosition()
      apartment.latitude = latLng.getLat()
      apartment.longitude = latLng.getLng()

      window.kakao.maps.event.addListener(marker, 'click', () => selectApt(apartment))
    })

    await checkFavoriteStatusForApartments(data)
  } catch (error) {
    console.error('관심 아파트 목록 로드 실패:', error)
  } finally {
    isListLoading.value = false
  }
}

/* 관심 지역 제어 */
// 선택한 법정동이 관심 지역인지 확인
watch(
  selectedUmd,
  async (newCode) => {
    if (!newCode) {
      isStarred.value = false
      return
    }
    try {
      const { data } = await baseURL.get('/api/interestRegion/isInterestRegion', {
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
// 관심지역 토글
async function toggleFavoriteRegion() {
  if (!token) {
    alert('로그인 해주세요.')
    router.push({ name: 'Login' })
    return
  }
  if (!selectedUmd.value) return

  try {
    if (isStarred.value) {
      await baseURL.delete('/api/interestRegion', {
        params: { lwdCd: selectedUmd.value + '00' },
      })
      isStarred.value = false
    } else {
      await baseURL.post('/api/interestRegion', { lwdCd: selectedUmd.value + '00' })
      isStarred.value = true
    }
  } catch (error) {
    console.error('관심지역 업데이트 실패:', error)
  }
}

// 툴팁 표시 함수
function showTooltip(event, deal) {
  // 기존 툴팁 제거
  hideTooltip()

  const tooltip = document.createElement('div')
  tooltip.id = 'chart-tooltip'
  tooltip.style.cssText = `
    position: fixed;
    background: rgba(0, 0, 0, 0.9);
    color: white;
    padding: 12px;
    border-radius: 8px;
    font-size: 12px;
    z-index: 10000;
    pointer-events: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    max-width: 200px;
  `

  const pricePerPyeong = Math.round(deal.pricePerPyeong / 10000)
  const totalPrice = formatPrice(deal.dealAmount)

  tooltip.innerHTML = `
    <div style="font-weight: bold; margin-bottom: 8px; color: #60a5fa;">
      ${deal.date.toLocaleDateString('ko-KR')}
    </div>
    <div style="margin-bottom: 4px;">
      <strong>거래가격:</strong> ${totalPrice}만원
    </div>
    <div style="margin-bottom: 4px;">
      <strong>평당가격:</strong> ${pricePerPyeong}만원/평
    </div>
    <div style="margin-bottom: 4px;">
      <strong>전용면적:</strong> ${deal.excluUseAr}㎡ (${Math.round(deal.excluUseAr * 0.3025)}평)
    </div>
    <div>
      <strong>층수:</strong> ${deal.floor}층
    </div>
  `

  document.body.appendChild(tooltip)

  // 툴팁 위치 조정
  const rect = tooltip.getBoundingClientRect()
  const x = event.clientX + 10
  const y = event.clientY - rect.height - 10

  tooltip.style.left = Math.min(x, window.innerWidth - rect.width - 10) + 'px'
  tooltip.style.top = Math.max(y, 10) + 'px'
}

// 툴팁 숨기기 함수
function hideTooltip() {
  const tooltip = document.getElementById('chart-tooltip')
  if (tooltip) {
    tooltip.remove()
  }
}

// 거래 날짜를 Date 객체로 변환
function parseDate(deal) {
  if (!deal.dealYmd) return null
  const year = deal.dealYmd.substring(0, 4)
  const month = deal.dealYmd.substring(4, 6)
  const day = deal.dealDay ? String(deal.dealDay).padStart(2, '0') : '01'
  return new Date(`${year}-${month}-${day}`)
}

// 가격 추이 차트 생성
function createPriceChart() {
  if (!selectedApt.value || !selectedApt.value.deals || !chartRef.value) return

  // 기존 차트 제거
  chartRef.value.innerHTML = ''

  const deals = selectedApt.value.deals
    .filter((deal) => deal.dealYmd && deal.dealAmount && deal.excluUseAr)
    .map((deal) => ({
      date: parseDate(deal),
      pricePerPyeong: calculatePricePerPyeong(deal.dealAmount, deal.excluUseAr),
      dealAmount: deal.dealAmount,
      excluUseAr: deal.excluUseAr,
      floor: deal.floor,
    }))
    .filter((deal) => deal.date && deal.pricePerPyeong > 0)
    .sort((a, b) => a.date - b.date)

  if (deals.length === 0) {
    chartRef.value.innerHTML = '<div class="empty-chart">가격 추이 데이터가 없습니다.</div>'
    return
  }

  // 간단한 SVG 차트 생성
  const width = 300
  const height = 200
  const margin = { top: 20, right: 20, bottom: 40, left: 60 }
  const chartWidth = width - margin.left - margin.right
  const chartHeight = height - margin.top - margin.bottom

  const minPrice = Math.min(...deals.map((d) => d.pricePerPyeong))
  const maxPrice = Math.max(...deals.map((d) => d.pricePerPyeong))
  const priceRange = maxPrice - minPrice || 1

  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg')
  svg.setAttribute('width', width)
  svg.setAttribute('height', height)
  svg.style.background = '#f9fafb'
  svg.style.borderRadius = '8px'

  // 차트 제목
  const title = document.createElementNS('http://www.w3.org/2000/svg', 'text')
  title.setAttribute('x', width / 2)
  title.setAttribute('y', 15)
  title.setAttribute('text-anchor', 'middle')
  title.setAttribute('font-size', '12')
  title.setAttribute('font-weight', 'bold')
  title.textContent = '1평당 가격 추이'
  svg.appendChild(title)

  // Y축 라벨 (가격)
  const yAxisLabel = document.createElementNS('http://www.w3.org/2000/svg', 'text')
  yAxisLabel.setAttribute('x', 15)
  yAxisLabel.setAttribute('y', margin.top + chartHeight / 2)
  yAxisLabel.setAttribute('text-anchor', 'middle')
  yAxisLabel.setAttribute('font-size', '10')
  yAxisLabel.setAttribute('transform', `rotate(-90, 15, ${margin.top + chartHeight / 2})`)
  yAxisLabel.textContent = '만원/평'
  svg.appendChild(yAxisLabel)

  // 데이터 포인트와 라인 그리기
  let pathData = ''
  deals.forEach((deal, index) => {
    const x = margin.left + (index / (deals.length - 1 || 1)) * chartWidth
    const y =
      margin.top + chartHeight - ((deal.pricePerPyeong - minPrice) / priceRange) * chartHeight

    // 라인 패스 데이터
    if (index === 0) {
      pathData += `M ${x} ${y}`
    } else {
      pathData += ` L ${x} ${y}`
    }

    // 데이터 포인트
    const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle')
    circle.setAttribute('cx', x)
    circle.setAttribute('cy', y)
    circle.setAttribute('r', 4)
    circle.setAttribute('fill', '#2563eb')
    circle.setAttribute('stroke', '#fff')
    circle.setAttribute('stroke-width', 2)
    circle.style.cursor = 'pointer'

    // 호버 효과
    circle.addEventListener('mouseenter', (e) => {
      circle.setAttribute('r', 6)
      circle.setAttribute('fill', '#1d4ed8')
      showTooltip(e, deal)
    })

    circle.addEventListener('mouseleave', () => {
      circle.setAttribute('r', 4)
      circle.setAttribute('fill', '#2563eb')
      hideTooltip()
    })

    svg.appendChild(circle)
  })

  // 라인 그리기
  if (pathData) {
    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path')
    path.setAttribute('d', pathData)
    path.setAttribute('stroke', '#2563eb')
    path.setAttribute('stroke-width', 2)
    path.setAttribute('fill', 'none')
    svg.insertBefore(path, svg.firstChild.nextSibling)
  }

  // Y축 눈금
  for (let i = 0; i <= 4; i++) {
    const price = minPrice + (priceRange * i) / 4
    const y = margin.top + chartHeight - (i / 4) * chartHeight

    const line = document.createElementNS('http://www.w3.org/2000/svg', 'line')
    line.setAttribute('x1', margin.left - 5)
    line.setAttribute('y1', y)
    line.setAttribute('x2', margin.left)
    line.setAttribute('y2', y)
    line.setAttribute('stroke', '#6b7280')
    line.setAttribute('stroke-width', 1)
    svg.appendChild(line)

    const text = document.createElementNS('http://www.w3.org/2000/svg', 'text')
    text.setAttribute('x', margin.left - 8)
    text.setAttribute('y', y + 3)
    text.setAttribute('text-anchor', 'end')
    text.setAttribute('font-size', '9')
    text.setAttribute('fill', '#6b7280')
    text.textContent = Math.round(price / 10000)
    svg.appendChild(text)
  }

  chartRef.value.appendChild(svg)
}

// 1평당 가격 계산 (1㎡ = 0.3025평)
function calculatePricePerPyeong(dealAmount, excluUseAr) {
  if (!dealAmount || !excluUseAr) return 0
  const price = parseInt(dealAmount.replace(/,/g, '')) * 10000 // 만원을 원으로 변환
  const pyeong = excluUseAr * 0.3025 // ㎡를 평으로 변환
  return Math.round(price / pyeong)
}

/* 지도 이동 제어 */
// 기기의 현재 위치로 이동
async function moveToCurrentLocation() {
  if (!map) return

  try {
    const position = await getCurrentDevicePosition()
    const latLng = new window.kakao.maps.LatLng(position.latitude, position.longitude)

    map.setLevel(DEFAULT_DISPLAY_LEVEL)
    map.panTo(latLng)
  } catch (error) {
    console.error('현재 위치를 가져올 수 없습니다:', error)
    alert('현재 위치를 가져올 수 없습니다.\n위치 서비스를 확인해주세요.')
  }
}
// 전달받은 위경도로 지도 이동
function moveToLocaation(latitude, longitude) {
  if (map && latitude && longitude) {
    const position = new window.kakao.maps.LatLng(latitude, longitude)
    map.setLevel(DEFAULT_DISPLAY_LEVEL)
    map.panTo(position)
  }
}

/* 법정동 필터 제어 */
// 법정동 필터를 맵 중심으로 설정
async function setLwdCdFilterToMapCenter() {
  if (!map) return

  isMyLocationLoading.value = true
  try {
    const center = map.getCenter()
    await setLwdCdFilter(center.getLat(), center.getLng())
  } catch (error) {
    console.error('내 위치 지정 실패:', error)
    alert('내 위치로 지정할 수 없습니다.')
  } finally {
    isMyLocationLoading.value = false
  }
}
// 위경도 기반으로 법정동 필터 설정
async function setLwdCdFilter(latitude, longitude) {
  try {
    const region = await coordToRegionCode(latitude, longitude)
    await selectLocation(region.code)
  } catch (error) {
    console.error('지역 설정 중 오류:', error)
  }
}
// 시/도 변경 처리
async function onSidoChanged() {}
// 시/군/구 변경 처리
async function onSggChanged() {}
// 읍/면/동 변경 처리
async function onUmdChanged() {
  if (selectedUmd.value) searchAptByLwdCd()
}

// 아파트 관심 등록 여부 확인
function isAptFavorite(aptSeq) {
  return favoriteApts.value.has(aptSeq)
}

async function checkIsAptFavorite(aptSeq) {
  const token = localStorage.getItem('authToken')
  if (!token) return false

  try {
    const { data } = await baseURL.get('/api/interestHouse/isInterestHouses', {
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
    router.push({ name: 'Login' })
    return
  }

  const aptSeq = apt.aptSeq
  const isFavorite = favoriteApts.value.has(aptSeq)

  try {
    if (isFavorite) {
      await baseURL.delete('/api/interestHouse', {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        data: { aptSeq },
      })
      favoriteApts.value.delete(aptSeq)
    } else {
      await baseURL.post(
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

onMounted(async () => {
  await updateSidoList() // 법정동 목록 로드

  try {
    map = await initMap(mapContainer.value)
    if (!map) throw new Error('지도 객체 생성 실패')
    isMapLoaded.value = true

    // 클러스터 생성
    const clustererOptions = {
      map: map, // 클러스터를 표시할 지도 객체
      averageCenter: true, // 클러스터 중심을 평균 위치로 설정
      minLevel: DEFAULT_DISPLAY_LEVEL + 1, // 클러스터 할 최소 지도 레벨
    }
    clustererByType.search = new window.kakao.maps.MarkerClusterer(clustererOptions)
    clustererByType.favorite = null //new window.kakao.maps.MarkerClusterer(clustererOptions)

    window.kakao.maps.event.addListener(map, 'zoom_changed', () => {
      const level = map.getLevel()

      if (level <= 7) {
        // marker.setMap(map) // 마커 보이기
      } else {
        // marker.setMap(null) // 마커 숨기기
      }
    })
  } catch (error) {
    console.error('지도 초기화 실패:', error)
  }
  // 현재 위치 기반 지역 설정
  await setLwdCdFilterToMapCenter()
})
</script>

<style scoped>
.house-map {
  position: relative;
  display: flex;
  overflow: hidden;
  flex: 1;
  /*height: 100vh;*/
  width: 100%;
  --sidebar-nav-width: 66px;
}

/* 사이드바 네비게이션 */
.house-map__sidebar-nav {
  background: white;
  border: 1px solid #d9d9d9;
  z-index: 1000;
  user-select: none;
  width: var(--sidebar-nav-width);
}

.house-map__sidebar-nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.house-map__remote-control-item,
.house-map__sidebar-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #3c3c3c;
  padding: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  user-select: none;
}
.house-map__remote-control-item:hover,
.house-map__sidebar-nav-item:hover {
  background-color: #f0f9ff;
  color: #0475f4;
}
.house-map__remote-control-item:hover {
  background-color: inherit;
}
.house-map__sidebar-nav-item.active {
  background-color: #0475f4;
  color: #ffffff;
}

.house-map__remote-control-item,
.house-map__sidebar-nav-item-label {
  font-size: 12px;
  text-align: center;
  margin-top: 4px;
}

/* 리모컨 */
.house-map__remote-control {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);

  gap: 10px;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 8px 12px;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.house-map__remote-control-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
}
.house-map__remote-control-item.active {
  color: #0475f4;
}
.house-map__remote-control-item.active:hover {
  color: #0356b3;
}

/* 사이드바 */
.house-map__sidebar {
  display: flex;
  flex-direction: column;

  position: absolute;
  z-index: 999;
  overflow: hidden;

  height: 100%;
  width: 350px;
  transition: transform 0.3s ease;

  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);

  padding: 10px;
  box-sizing: border-box;
}

.house-map__sidebar-left {
  left: var(--sidebar-nav-width);
  transform: translateX(-100%);
}

.house-map__sidebar-left.active {
  transform: translateX(0);
}

.house-map__sidebar-right {
  overflow-y: auto;
  right: 0;
  transform: translateX(100%);
}

.house-map__sidebar-right.active {
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

/* 검색 영역 */
.house-map__search-container {
  display: flex;
  flex-direction: column;
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

.house-map__search-filters {
  font-size: 14px;
}

.house-map__search-filter-header {
  display: flex;
  justify-content: space-between;
  padding: 8px;
  margin: 8px 0;
}

.house-map__set-map-center-button {
  margin: 0;
  padding: 2px;
  background: none;
  border: 2px solid #cccccc;
  border-radius: 8px;
  cursor: pointer;
  font-size: 20px;
  transition: all 0.2s;
}

.house-map__set-map-center-button:hover {
  background-color: #e5e7eb;
}

.house-map__set-map-center-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.house-map__search-lwdCd-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
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
  transition: background-color 0.2s;
  max-width: 100px;
}

.house-map__search-selectBox:hover {
  background-color: #f9fafb;
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

/* 아파트 검색 결과 */
.house-map__sidebar-thread {
  flex: 1;
  overflow-y: scroll;
  min-width: 0;
  box-sizing: border-box;
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

/* 지도 영역 */
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

.price-chart-section {
  margin-top: 16px;
}

.deal-chart {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.empty-chart {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
  font-style: italic;
  background: #f9fafb;
  border-radius: 8px;
}
</style>
