<template>
  <div class="base-layout">
    <div class="settings-section">
      <LanguageSwitcher />
    </div>
    <main class="content">
      <slot></slot>
    </main>
  </div>
</template>

<script>
import LanguageSwitcher from "../components/globals/LanguageSwitcher.vue";

export default {
  name: "BaseLayout",
  components: {
    LanguageSwitcher,
  },
};
</script>

<style lang="scss">
.base-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
  position: relative;
}

.content {
  flex-grow: 1;
  padding: 60px 160px 60px 160px;
  padding-top: 80px;
  background-color: #f5f5f5;
  transition: all 0.3s ease;
  position: relative;
}

.avatar-container {
  position: absolute;
  top: 20px;
  right: 40px;
  z-index: 2;

  @media (max-width: 950px) {
    right: 20px;
    top: 15px;
  }
}

.settings-section {
  position: fixed;
  top: 20px;
  right: 40px;
  z-index: 10;
  display: flex;
  justify-content: flex-end;
}
</style>
