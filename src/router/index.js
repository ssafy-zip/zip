import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import JoinView from '@/views/JoinView.vue'
import FindIdView from '@/views/FindIdView.vue'
import FindPasswordView from '@/views/FindPasswordView.vue'
import HouseMapView from '@/views/HouseMapView.vue'
import BbsView from '@/views/BbsView.vue'
import BbsWriteView from '@/views/BbsWriteView.vue'
import BbsDetailView from '@/views/BbsDetailView.vue'
import NewsListView from '@/views/NewsListView.vue'
import MyInfoView from '@/views/MyInfoView/MyInfoView.vue'
import MyInfoProfileView from '@/views/MyInfoView/MyInfoProfileView.vue'
import MyInfoCommentsView from '@/views/MyInfoView/MyInfoCommentsView.vue'
import MyInfoPostsView from '@/views/MyInfoView/MyInfoPostsView.vue'
import MyInfoFavoritesView from '@/views/MyInfoView/MyInfoFavoritesView.vue'
import ChatView from '@/views/ChatView.vue'
import HomeView from '@/views/HomeView.vue'
import ResetPasswordForm from '@/views/ResetPasswordForm.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'Home', component: HomeView },
    { path: '/login', name: 'Login', component: LoginView },
    { path: '/join', name: 'Join', component: JoinView },
    { path: '/findId', name: 'FindId', component: FindIdView },
    { path: '/findPassword', name: 'FindPassword', component: FindPasswordView },
    { path: '/reset-password', name: 'ResetPasswordForm', component: ResetPasswordForm },
    { path: '/houseMap', name: 'HouseMap', component: HouseMapView },
    { path: '/bbs', name: 'Bbs', component: BbsView },
    { path: '/bbs/write', name: 'BbsWrite', component: BbsWriteView },
    { path: '/bbs/:id', name: 'BbsDetail', component: BbsDetailView, props: true },
    { path: '/news', name: 'News', component: NewsListView },
    { path: '/chat', name: 'Chat', component: ChatView },
    {
      path: '/myInfo',
      name: 'MyInfo',
      component: MyInfoView,
      redirect: {
        name: 'MyInfoProfileView',
      },
      children: [
        {
          path: 'profile',
          name: 'MyInfoProfileView',
          component: MyInfoProfileView,
        },
        {
          path: 'favorites',
          name: 'MyInfoFavoritesView',
          component: MyInfoFavoritesView,
        },
        {
          path: 'posts',
          name: 'MyInfoPostsView',
          component: MyInfoPostsView,
        },
        {
          path: 'comments',
          name: 'MyInfoCommentsView',
          component: MyInfoCommentsView,
        },
      ],
    },
  ],
})

export default router
