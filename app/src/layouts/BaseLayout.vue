<template>
  <div class="base-layout">
    <Sidebar />
    <main class="content">
      <div class="avatar-container">
        <UIAvatar username="Nick A" size="md" />
      </div>
      <slot></slot> <!-- renders the page-specific content -->
    </main>
  </div>
</template>

<script>
import Sidebar from './Sidebar.vue';
import UIAvatar from '../components/globals/UIAvatar.vue';

export default {
  name: 'BaseLayout',
  components: {
    Sidebar,
    UIAvatar
  },
};
</script>

<style lang="scss">
.base-layout {
  display: flex;
  min-height: 100vh;
  background-color: #F5F5F5;
  position: relative;
}

.content {
  flex-grow: 1;
  margin-left: 450px;
  padding: 40px;
  padding-top: 80px;
  min-height: 100vh;
  background-color: #F5F5F5;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  width: calc(100% - 450px);

  @media (max-width: 950px) {
    margin-left: 0;
    width: 100%;
    padding: 30px;
    padding-top: 70px;

    .sidebar--collapsed & {
      margin-left: 300px;
      width: calc(100% - 300px);
    }
  }
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
</style>