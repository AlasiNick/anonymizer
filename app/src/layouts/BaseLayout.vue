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
@import url("https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&family=DM+Mono:wght@400;500&display=swap");

.base-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f7f8f6;
  position: relative;

  // Subtle grid texture in the background
  background-image:
    linear-gradient(rgba(13, 123, 107, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(13, 123, 107, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
}

.content {
  flex-grow: 1;
  padding: 80px 160px 60px;
  transition: all 0.3s ease;
  position: relative;

  @media (max-width: 1100px) {
    padding: 80px 60px 60px;
  }

  @media (max-width: 768px) {
    padding: 70px 20px 40px;
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
