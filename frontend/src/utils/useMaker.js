import { ref } from 'vue'
import { useKakaoMap } from '@/utils/useKakaoMap.js'

const { addressToPosition } = useKakaoMap()

export function useMarker() {
  const markersByType = {
    search: [],
    favorite: [],
  }

  const markersVisibleByType = {
    search: ref(true),
    favorite: ref(true),
  }

  const clustererByType = {
    search: null,
    favorite: null,
  }

  function clearMarkers(type) {
    markersByType[type]?.forEach((marker) => marker.setMap(null))
    markersByType[type] = []
    clustererByType[type]?.clear()
  }

  function toggleMarkers(type, map) {
    if (!map || !markersByType[type]) return
    const visible = markersVisibleByType[type]
    visible.value = !visible.value

    markersByType[type].forEach((marker) => {
      marker.setMap(visible.value ? map : null)
    })

    if (clustererByType[type]) {
      if (visible.value) {
        clustererByType[type].addMarkers(markersByType[type])
      } else {
        clustererByType[type].clear()
      }
    }
  }

  async function createMarkers(type, items, map, getPositionFn, options = {}) {
    try {
      if (!map) throw new Error('지도 객체가 없음')

      const markerPromises = items.value.map(async (item) => {
        try {
          const address = await getPositionFn(item)
          const pos = await addressToPosition(address)
          const loc = new window.kakao.maps.LatLng(pos.latitude, pos.longitude)
          const marker = new window.kakao.maps.Marker({
            position: loc,
            map: markersVisibleByType[type].value ? map : null,
            ...(options.markerImage && {
              image: new window.kakao.maps.MarkerImage(
                options.markerImage.src,
                new window.kakao.maps.Size(...options.markerImage.size),
                options.markerImage.options || {},
              ),
            }),
            ...(options.zIndex && {
              zIndex: options.zIndex,
            }),
          })
          marker.item = item
          return marker
        } catch (error) {
          console.error('주소 변환 오류:', error)
          return null
        }
      })

      clearMarkers(type)
      const results = await Promise.all(markerPromises)
      const validMarkers = results.filter((m) => m !== null)
      markersByType[type].push(...validMarkers)

      if (clustererByType[type] && markersVisibleByType[type].value) {
        clustererByType[type].addMarkers(validMarkers)
      }
    } catch (error) {
      console.error(`[${type}] 마커 생성 오류:`, error)
    }
  }

  return {
    markersByType,
    markersVisibleByType,
    clustererByType,
    clearMarkers,
    toggleMarkers,
    createMarkers,
  }
}
