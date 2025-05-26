/* kakaoMapUtil.js */

const DEFAULT_DISPLAY_LEVEL = 5
let kakaoScriptPromise = null

/* 스크립트 로드 */
const loadKakaoMapScript = async () => {
  if (window.kakao && window.kakao.maps && window.kakao.maps.load) return
  if (!kakaoScriptPromise) {
    kakaoScriptPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script')
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_JS_KEY}&autoload=false&libraries=services,clusterer`
      script.async = true
      script.onload = () => window.kakao.maps.load(resolve)
      script.onerror = () => reject(new Error('Kakao Maps 스크립트 로드 실패'))
      document.head.appendChild(script)
    })
  }
  return kakaoScriptPromise
}

/* 기기의 현재 위치를 반환 */
const getCurrentDevicePosition = async () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocation API를 지원하지 않습니다.'))
      return
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords
        resolve({ latitude, longitude })
      },
      (error) => reject(new Error(`위치 정보 가져오기 실패: ${error.message}`)),
    )
  })
}

/* 위경도로부터 주소 조회 */
const coordToRegionCode = async (lat, lng) => {
  return new Promise((resolve, reject) => {
    const geocoder = new window.kakao.maps.services.Geocoder()
    geocoder.coord2RegionCode(lng, lat, (result, status) => {
      if (status === window.kakao.maps.services.Status.OK) {
        const region = result.find((r) => r.region_type === 'B')
        if (region) resolve(region)
        else reject(new Error('법정동 주소를 찾을 수 없습니다.'))
      } else {
        reject(new Error('좌표로부터 주소 조회 실패'))
      }
    })
  })
}

/* 주소로부터 위경도 조회 */
const addressToPosition = async (address) => {
  return new Promise((resolve, reject) => {
    const geocoder = new window.kakao.maps.services.Geocoder()
    geocoder.addressSearch(address, (result, status) => {
      if (status === window.kakao.maps.services.Status.OK) {
        resolve({
          latitude: parseFloat(result[0].y),
          longitude: parseFloat(result[0].x),
        })
      } else {
        reject(new Error('주소를 좌표로 변환할 수 없습니다.'))
      }
    })
  })
}

const initMap = async (container) => {
  await loadKakaoMapScript()

  const options = {}
  options.level = DEFAULT_DISPLAY_LEVEL
  const pos = await getCurrentDevicePosition().catch(() => ({
    latitude: 37.501286,
    longitude: 127.0396029,
  }))
  options.center = new window.kakao.maps.LatLng(pos.latitude, pos.longitude)

  let map = new window.kakao.maps.Map(container, options)
  map.addControl(new window.kakao.maps.ZoomControl(), window.kakao.maps.ControlPosition.RIGHT)
  // new window.kakao.maps.Marker({ position: options.center, map: map })
  return map
}

export function useKakaoMap() {
  return {
    DEFAULT_DISPLAY_LEVEL,
    loadKakaoMapScript,
    getCurrentDevicePosition,
    coordToRegionCode,
    addressToPosition,
    initMap,
  }
}
