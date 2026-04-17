import { ref } from 'vue'
import { title as serviceTitle, content as serviceContent } from '@/constants/terms/service'
import { title as privacyTitle, content as privacyContent } from '@/constants/terms/privacy'

export function useTermsModal() {
  const show = ref(false)
  const title = ref('')
  const content = ref('')

  const open = (type) => {
    if (type === 'service') {
      title.value = serviceTitle
      content.value = serviceContent
    } else if (type === 'privacy') {
      title.value = privacyTitle
      content.value = privacyContent
    }
    show.value = true
  }

  const close = () => {
    show.value = false
  }

  return { show, title, content, open, close }
}
