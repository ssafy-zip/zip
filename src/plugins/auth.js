// src/plugins/auth.js
import { reactive } from 'vue'

const state = reactive({
  token: localStorage.getItem('authToken') || '',
})

function setAuthToken(token) {
  state.token = token
  localStorage.setItem('authToken', token)
}

function logout() {
  state.token = ''
  localStorage.removeItem('authToken')
}

export default {
  install(app) {
    // 전역 프로퍼티 등록
    app.config.globalProperties.$auth = state
    app.config.globalProperties.$setAuthToken = setAuthToken
    app.config.globalProperties.$logout = logout
  },
}
