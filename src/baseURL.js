import axios from 'axios'

const baseURL = axios.create({
  baseURL: 'https://yaguhang.kro.kr:6443',
})

export default baseURL
