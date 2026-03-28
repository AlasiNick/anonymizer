<template>
  <button 
    class="primary-button"
    :class="{ 'with-icon': $slots.icon }"
    @click="$emit('click')"
  >
    <span v-if="$slots.icon" class="icon">
      <slot name="icon"></slot>
    </span>
    <span class="button-text">
      <slot></slot>
    </span>
  </button>
</template>

<script>
export default {
  name: 'PrimaryButton',
  emits: ['click']
}
</script>

<style scoped>
.primary-button {
  background: #3865F2;
  color: white;
  border: none;
  border-radius: 80px;
  padding: 12px 24px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  text-decoration: none;
}

.primary-button:hover {
  background-color: #2851D8;
  transform: translateY(-2px);
}

.primary-button .icon {
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  font-weight: bold;
}

.primary-button .button-text {
  color: white;
}
</style>