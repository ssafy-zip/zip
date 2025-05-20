<template>
  <div class="modal" v-if="visible">
    <div class="modal__overlay">
      <div class="modal__container">
        <!-- Header -->
        <div class="modal__header">
          <h2 class="modal__title">{{ title }}</h2>
          <button class="modal__close" @click="close">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <!-- Icon -->
        <div class="modal__icon">
          <i :class="icon"></i>
        </div>

        <!-- Description -->
        <p class="modal__description" v-html="description"></p>

        <!-- Form -->
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: Boolean,
  title: String,
  icon: {
    type: [String, Array, Object],
  },
  description: String,
})
const emit = defineEmits(['close', 'submit'])

function close() {
  emit('close')
}
</script>

<style scoped src="@/assets/css/inputModal.css"></style>
