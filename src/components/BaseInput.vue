<template>
  <div class="input-group">
    <label v-if="icon" :for="id" class="input-label">
      <i :class="icon"></i>
    </label>

    <div class="input-box">
      <input
        v-model="proxyValue"
        :id="id"
        :type="computedType"
        :placeholder="placeholder"
        :autocomplete="autocomplete"
        :inputmode="inputmode"
        :autofocus="autofocus"
      />

      <div v-show="valid" class="input-valid">
        <i class="fa-solid fa-check"></i>
      </div>

      <!-- clear 버튼 -->
      <button
        v-if="clearable && proxyValue"
        class="input-action-button"
        type="button"
        @click="proxyValue = ''"
        aria-label="입력 지우기"
      >
        <i class="fa-solid fa-xmark"></i>
      </button>

      <!-- 비밀번호 보기/숨기기 -->
      <button
        v-if="isPassword"
        class="input-action-button"
        type="button"
        @click="togglePassword"
        aria-label="비밀번호 보기/숨기기"
        :title="showPassword ? '비밀번호 숨기기' : '비밀번호 보기'"
      >
        <i :class="showPassword ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'"></i>
      </button>

      <!-- slot 삽입도 가능 -->
      <slot></slot>
    </div>
  </div>

  <p v-if="errorMessage" class="form-error">{{ errorMessage }}</p>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: String,
  id: String,
  type: {
    type: String,
    default: 'text',
  },
  placeholder: String,
  icon: String,
  errorMessage: String,
  clearable: Boolean,
  autocomplete: {
    type: String,
    default: 'off',
  },
  inputmode: String,
  autofocus: Boolean,
  valid: Boolean,
})
const emit = defineEmits(['update:modelValue'])

const showPassword = ref(false)

const isPassword = computed(() => props.type === 'password')

const computedType = computed(() =>
  isPassword.value ? (showPassword.value ? 'text' : 'password') : props.type,
)

const proxyValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const togglePassword = () => {
  showPassword.value = !showPassword.value
}
</script>

<style scoped src="@/assets/css/authForm.css"></style>
