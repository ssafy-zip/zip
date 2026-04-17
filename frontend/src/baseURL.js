import axios from 'axios'

const baseURL = axios.create({
  // baseURL: 'https://yaguhang.kro.kr:6443',
  // baseURL: 'http://localhost:8080',
  baseURL: 'http://localhost:8082',
})

export default baseURL
