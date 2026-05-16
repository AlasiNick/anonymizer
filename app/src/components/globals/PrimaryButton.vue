<template>
  <button
    class="primary-button"
    :class="{ 'with-icon': $slots.icon }"
    @click="$emit('click')"
  >
    <span v-if="$slots.icon" class="icon" aria-hidden="true">
      <slot name="icon"></slot>
    </span>
    <span class="button-text">
      <slot></slot>
    </span>
  </button>
</template>

<script>
export default {
  name: "PrimaryButton",
  emits: ["click"],
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500&display=swap");

.primary-button {
  background: #0d7b6b;
  color: #000;
  border: none;
  border-radius: 6px;
  padding: 11px 22px;
  font-family: "DM Sans", sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  letter-spacing: 0.02em;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition:
    background 0.2s ease,
    transform 0.15s ease,
    box-shadow 0.2s ease;
  text-decoration: none;
  position: relative;
}

.primary-button::after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: 6px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.15);
  pointer-events: none;
}

.primary-button:hover {
  background: #095f52;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(13, 123, 107, 0.3);
}

.primary-button:active {
  transform: translateY(0);
  box-shadow: none;
}

.primary-button .icon {
  display: flex;
  align-items: center;
  font-size: 1rem;
}

.primary-button .button-text {
  color: #000;
}
</style>
