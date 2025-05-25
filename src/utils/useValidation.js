export function useValidation() {
  const passwordRegex = /^.{4,}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  const phoneRegex = /^(01[016789]|02|0[3-9][0-9])\d{7,8}$/

  const isValidPassword = (password) => passwordRegex.test(password)
  const isValidEmail = (email) => emailRegex.test(email)
  const isValidPhone = (phone) => phoneRegex.test(phone)

  return { isValidPassword, isValidEmail, isValidPhone }
}
