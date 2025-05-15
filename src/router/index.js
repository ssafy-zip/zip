import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import JoinView from '@/views/JoinView.vue'
import FindIdView from '@/views/FindIdView.vue'
import FindPasswordView from '@/views/FindPasswordView.vue'
import HouseMapView from '@/views/HouseMapView.vue'
import BbsView from '@/views/BbsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', name: 'Login', component: LoginView },
    { path: '/join', name: 'Join', component: JoinView },
    { path: '/findId', name: 'FindId', component: FindIdView },
    { path: '/findPassword', name: 'FindPassword', component: FindPasswordView },
    { path: '/houseMap', name: 'HouseMap', component: HouseMapView },
    { path: '/bbs', name: 'Bbs', component: BbsView },
  ],
})

export default router
