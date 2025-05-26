/* lwdCdUtil.js */

import { ref, watch } from 'vue'
// import baseURL from 'baseURL'
import baseURL from '@/baseURL'

export function useLwdCd() {
  const sidoList = ref([])
  const sggList = ref([])
  const umdList = ref([])
  const selectedSido = ref('')
  const selectedSgg = ref('')
  const selectedUmd = ref('')

  const getLwdCdFullName = (lwdCd) => {
    return (
      lwdCd.sidoName +
      (lwdCd.sggName ? ' ' + lwdCd.sggName : '') + // '세종특별자치시'는 sggName이 null
      (lwdCd.umdName ? ' ' + lwdCd.umdName : '') +
      (lwdCd.riName ? ' ' + lwdCd.riName : '')
    )
  }

  // 시도 목록 조회
  async function updateSidoList() {
    selectedSido.value = ''
    sidoList.value = []

    try {
      const { data } = await baseURL.get('/api/lwdCd/sido/simple')
      console.log('시도 목록 : ', data)
      sidoList.value = data
    } catch (error) {
      console.error('시도 목록 조회 실패:', error)
    }
  }

  // 시군구 목록 조회
  async function updateSggList() {
    selectedSgg.value = ''
    sggList.value = []

    if (!selectedSido.value) return

    try {
      console.log(selectedSido.value)
      const { data } = await baseURL.get(`/api/lwdCd/sgg/${selectedSido.value}/simple`)
      sggList.value = data
    } catch (error) {
      console.error('시군구 목록 조회 실패:', error)
    }
  }

  // 읍면동 목록 조회
  async function updateUmdList() {
    selectedUmd.value = ''
    umdList.value = []

    if (!selectedSgg.value) return

    try {
      const { data } = await baseURL.get(`/api/lwdCd/umd/${selectedSgg.value}/simple`)
      umdList.value = data
    } catch (error) {
      console.error('읍면동 목록 조회 실패:', error)
    }
  }

  // 시도 검색에 따른 시군구 조회
  watch(selectedSido, async (newSido) => {
    const isValid = sidoList.value.some((v) => v.code === newSido)
    if (!isValid) {
      selectedSido.value = ''
    }
    await updateSggList()
  })

  // 시군구 검색에 따른 읍면동 조회
  watch(selectedSgg, async (newSgg) => {
    const isValid = sggList.value.some((v) => v.code === newSgg)
    if (!isValid) {
      selectedSgg.value = ''
    }
    await updateUmdList()
  })

  // 읍면동 유효성 검색
  watch(selectedUmd, (newUmd) => {
    const isValid = umdList.value.some((v) => v.code === newUmd)
    if (!isValid) {
      selectedUmd.value = ''
    }
  })

  // 법정동 동시에 선택
  async function selectLocation(code) {
    selectedSido.value = code.slice(0, 2)
    await updateSggList()
    selectedSgg.value = code.slice(0, 5)
    await updateUmdList()
    selectedUmd.value = code.slice(0, 8)
  }

  return {
    // state
    sidoList,
    sggList,
    umdList,
    selectedSido,
    selectedSgg,
    selectedUmd,

    // methods
    updateSidoList,
    updateSggList,
    updateUmdList,
    selectLocation,
    getLwdCdFullName,
  }
}
