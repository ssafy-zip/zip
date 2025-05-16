<template>
  <div class="form-group">
    <label class="form-label">
      <i :class="['fa-solid', icon, 'fa-2x']"></i>
    </label>
    <div class="input-wrapper">
      <input
        :type="type"
        :placeholder="placeholder"
        :required="required"
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, required: true }, // 예: "fa-user"
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  required: { type: Boolean, default: false },
  modelValue: { type: [String, Number], default: '' },
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.form-group {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 8px;
}

.form-label {
  width: 40px;
  color: #374151;
  text-align: center;
}

.input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
}

.input-wrapper input {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}
</style>
