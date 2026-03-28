<template>
  <div class="sidebar" :class="{ 'sidebar--collapsed': isCollapsed }">
    <!-- Logo -->
    <div class="icon_top mb-xl">
      <img src="../assets/cyberwiseLogo.svg" alt="Cyberwise Logo" />
      <span class="logo-text">WiseSpace</span>
    </div>

    <!-- Navigation Links -->
    <router-link to="/" class="sidebar-item">
      <img src="../assets/home.svg" alt="Home" class="icon" />
      <span>{{ $t('home') }}</span>
    </router-link>

    <div class="data-processing">
      <div class="sidebar-item-data">
        <router-link to="/pseudonymize" class="sidebar-item-data">
      <img src="../assets/dataprocessing.svg" alt="Data processing" class="icon" />
      <span>{{ $t('re-identification') }}</span>
        </router-link>
      </div>
      <div class="sub-items">
        <router-link to="/projects" class="sub-item">{{ $t('projects') }}</router-link>
      </div>
    </div>

    <router-link to="/risk-assessment" class="sidebar-item mt-xl">
      <img src="../assets/riskassessment.svg" alt="Risk assessment" class="icon" />
      <span>{{ $t('riskAssessment') }}</span>
    </router-link>

    <!-- Language Switcher -->
    <div class="settings-section">
      <LanguageSwitcher />
    </div>

    <!-- Social Media Icons -->
    <div class="social-icons">
      <a href="https://facebook.com" target="_blank" rel="noopener">
        <img src="../assets/facebook.svg" alt="Facebook" />
      </a>
      <a href="https://www.linkedin.com/company/cyber8wise/posts/?feedView=all" target="_blank" rel="noopener">
        <img src="../assets/linkedin.svg" alt="LinkedIn" />
      </a>
      <a href="https://instagram.com" target="_blank" rel="noopener">
        <img src="../assets/instagram.svg" alt="Instagram" />
      </a>
      <a href="https://youtube.com" target="_blank" rel="noopener">
        <img src="../assets/youtube.svg" alt="YouTube" />
      </a>
    </div>

    <!-- Copyright -->
    <div class="copyright">
      © {{ currentYear }}
      <a
        href="https://cyberwise.space/"
        class="copyright-link"
        target="_blank"
        rel="noopener"
      >
        CyberWise.
      </a>
      {{ $t('allRightsReserved') }}
    </div>
  </div>

  <!-- Mobile Sidebar Toggle -->
  <transition name="fade-move">
  <div v-if="isCollapsed" class="topbar-settings visible" :style="topbarStyle">
    <LanguageSwitcher />
  </div>
</transition>

  <button
    class="mobile-toggle"
    @click="toggleSidebar"
    :title="isCollapsed ? 'Close Menu' : 'Open Menu'"
    :aria-label="isCollapsed ? 'Close Menu' : 'Open Menu'"
    :aria-expanded="isCollapsed.toString()"
  >
    <span class="mobile-toggle__icon"></span>
  </button>
</template>

<script>
import LanguageSwitcher from '../components/globals/LanguageSwitcher.vue';

const sidebarWidth = 450; // Width of the sidebar in pixels
export default {
  name: "Sidebar",
  components: { LanguageSwitcher },
  data() {
    return {
      isCollapsed: false,
      currentYear: new Date().getFullYear()
    };
  },
  computed: {
    topbarStyle() {
      if (!this.isCollapsed) return {};

      if (window.innerWidth < 768) {
        // Mobile: full-screen sidebar
        return { right: '2rem' };
      } else {
        // Tablet: sidebar takes ~66%
        return { right: '42vw' };
      }
    }
  },
  watch: {
    '$route'() {
      if (window.innerWidth < 950) {
        this.isCollapsed = false;
      }
    }
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },
    checkWidth() {
      if (window.innerWidth < 950) {
        this.isCollapsed = false;
      }
    }
  },
  mounted() {
    this.checkWidth();
    window.addEventListener('resize', this.checkWidth);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.checkWidth);
  }
};  
</script>


<style scoped lang="scss">
$sidebar-collapse-breakpoint: 950px;

.sidebar {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  padding: 24px 20px;
  background: linear-gradient(180deg, #3865F2 39%, #161C60 100%);
  height: 100vh;
  width: 450px;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 2;
  overflow-y: auto;
  transition: all 0.3s ease;

  @media (max-width: #{$sidebar-collapse-breakpoint}) {
    transform: translateX(-100%);
    box-shadow: none;
  }

  &.sidebar--collapsed {
    @media (max-width: #{$sidebar-collapse-breakpoint}) {
      transform: translateX(0);
      box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
    }
  }
}

.mobile-toggle {
  display: none;
  position: fixed;
  left: 20px;
  top: 20px;
  background: rgba(56, 101, 242, 0.9);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  width: 44px;
  height: 44px;
  cursor: pointer;
  z-index: 1000;
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.1),
    0 2px 4px rgba(56, 101, 242, 0.2);

  @media (max-width: #{$sidebar-collapse-breakpoint}) {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .sidebar--collapsed & {
    left: calc(450px - 64px);
    background: rgba(22, 28, 96, 0.9);
  }

  &:hover {
    transform: translateY(-2px);
    background: rgba(56, 101, 242, 1);
    box-shadow:
      0 8px 24px rgba(0, 0, 0, 0.15),
      0 4px 8px rgba(56, 101, 242, 0.3);
  }

  &:active {
    transform: translateY(1px);
  }

  &__icon {
    width: 18px;
    height: 2px;
    background: white;
    position: relative;
    border-radius: 1px;
    transition: all 0.3s ease;

    &::before,
    &::after {
      content: '';
      position: absolute;
      width: 100%;
      height: 100%;
      background: white;
      left: 0;
      border-radius: 1px;
      transition: all 0.3s ease;
    }

    &::before {
      top: -5px;
    }

    &::after {
      bottom: -5px;
    }
  }

  .sidebar--collapsed & {
    &__icon {
      background: transparent;

      &::before {
        top: 0;
        transform: rotate(45deg);
      }

      &::after {
        bottom: 0;
        transform: rotate(-45deg);
      }
    }
  }
}

.icon_top {
  display: flex;
  align-items: center;
  gap: 28px;
  margin-right: 20px;
  margin-top: 80px;
  margin-bottom: 20px;

  img {
    width: 75px;
    height: auto;

    @media (max-width: #{$sidebar-collapse-breakpoint}) {
      width: 80px;
    }

    @media (max-width: 768px) {
      width: 70px;
    }
  }

  .logo-text {
    font-family: 'Montserrat', sans-serif;
    font-size: 54px;
    color: white;
    text-shadow: 0px 0px 10px rgba(56, 101, 242, 0.5);
    font-weight: bold;

    @media (max-width: 768px) {
      font-size: 42px;
    }
  }
}

.sidebar-item {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
  font-size: 34px;
  font-weight: bold;
  margin-top: 40px;

  @media (max-width: 768px) {
    font-size: 32px;
  }
}

.sidebar-item-data {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 15px;
  margin-top: 25px;

  @media (max-width: 768px) {
    font-size: 28px;
  }
}

.icon {
  width: 50px;
  height: 40px;
  margin-right: 30px;

  @media (max-width: 768px) {
    width: 40px;
    height: 40px;
  }
}

.section-title {
  font-family: 'Montserrat', sans-serif;
  font-size: 24px;
  color: white;
  margin-bottom: 15px;
  margin-top: 50px;
  text-decoration: none;
  font-weight: bold;
  margin-left: 90px;

  @media (max-width: 768px) {
    font-size: 30px;
    margin-left: 60px;
  }
}

.sub-items {
  margin-left: 111px;

  @media (max-width: 768px) {
    margin-left: 70px;
  }
}

.sub-item {
  font-family: 'Montserrat', sans-serif;
  font-size: 28px;
  font-weight: 600;
  color: white;
  text-decoration: none;
  display: block;
  margin-bottom: 10px;

  @media (max-width: 768px) {
    font-size: 28px;
  }

  &::before {
    content: '\2022';
    font-size: 18px;
    margin-right: 10px;
    color: white;
  }

  &:hover {
    text-decoration: solid;
  }
}

.settings-section {
  margin-top: auto;
  display: flex;
  justify-content: right;
  position: fixed;

  @media (max-width: #{$sidebar-collapse-breakpoint}) {
    display: none; 
  }
}

.topbar-settings {
  position: fixed;
  top: 25px;
  z-index: 1001;
  transition: all 0.8s ease-in-out;

  @media (min-width: #{$sidebar-collapse-breakpoint}) {
    display: none; 
  }
}

.fade-move-enter-active,
.fade-move-leave-active {
  transition: all 0.4s ease;
}

.fade-move-enter-from,
.fade-move-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-move-enter-to,
.fade-move-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.social-icons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-top: 60px;
  margin-bottom: 20px;
  flex-wrap: wrap; // allows them to wrap on smaller screens

  a {
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.2s ease;
    width: 48px;
    height: 48px;

    &:hover {
      transform: scale(1.1);
    }

    img {
      width: 100%;
      height: auto;
      max-width: 50px;

      @media (max-width: #{$sidebar-collapse-breakpoint}) {
        max-width: 40px;
      }
    }
  }

  @media (max-width: 768px) {
    gap: 20px;
    margin-top: 40px;
  }
}


.copyright {
  position: fixed;
  bottom: 30px;
  width: calc(450px - 40px);
  padding: 20px;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  font-family: 'Montserrat', sans-serif;
  border-top: 2px solid rgba(255, 255, 255, 0.1);

  @media (max-width: #{$sidebar-collapse-breakpoint}) {
    width: calc(100% - 80px);
    bottom: 50px;
    font-size: 18px;
    justify-content: center;
  }
}

.copyright-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-weight: bold;

  &:hover {
    text-decoration: underline;
  }
}
</style>
