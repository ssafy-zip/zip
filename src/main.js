import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import { quillEditor } from 'vue3-quill'

import authPlugin from '@/plugins/auth'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.use(quillEditor)

app.use(authPlugin)

app.mount('#app')
