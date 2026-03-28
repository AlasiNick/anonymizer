<template>
  <BaseLayout>
    <div class="home">
      <div class="welcome-section">
        <h1>{{ $t('welcome') }}</h1>
        <p class="subtitle">{{ $t('platformSubtitle') }}</p>
      </div>

      <div class="quick-actions">
        <h2>{{ $t('quickActions') }}</h2>
        <div class="action-cards">
          <router-link to="/anonymize" class="action-card">
            <img src="../assets/dataprocessing.svg" alt="Anonymize" class="card-icon" />
            <h3>{{ $t('anonymizeData') }}</h3>
            <p>{{ $t('anonymizeDescription') }}</p>
          </router-link>

          <router-link to="/pseudonymize" class="action-card">
            <img src="../assets/dataprocessing.svg" alt="Pseudonymize" class="card-icon" />
            <h3>{{ $t('pseudonymize') }}</h3>
            <p>{{ $t('pseudonymizeDescription') }}</p>
          </router-link>

          <router-link to="/projects" class="action-card">
            <img src="../assets/dataprocessing.svg" alt="Projects" class="card-icon" />
            <h3>{{ $t('projects') }}</h3>
            <p>{{ $t('projectsDescription') }}</p>
          </router-link>
        </div>
      </div>
    </div>
  </BaseLayout>
</template>

<script>
import BaseLayout from '../layouts/BaseLayout.vue';

export default {
  name: "Home",
  components: {
    BaseLayout
  }
};
</script>
  
<style scoped>
.home {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  margin-bottom: 60px;
}

h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 16px;
}

.subtitle {
  font-size: 1.2rem;
  color: #666;
}

.quick-actions {
  margin-top: 40px;
}

h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 24px;
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-top: 20px;
}

.action-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  border: 1px solid #eee;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    border-color: #3865F2;
  }
}

.card-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

h3 {
  font-size: 1.4rem;
  color: #333;
  margin-bottom: 8px;
}

p {
  color: #666;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .home {
    padding: 20px;
  }

  h1 {
    font-size: 2rem;
  }

  .action-cards {
    grid-template-columns: 1fr;
  }
}
</style>