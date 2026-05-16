<template>
  <BaseLayout>
    <div class="home">
      <section class="welcome-section">
        <div class="welcome-badge" aria-hidden="true">
          <span class="badge-dot"></span>
          System ready
        </div>
        <h1>{{ $t("welcome") }}</h1>
        <p class="subtitle">{{ $t("platformSubtitle") }}</p>
      </section>

      <section class="quick-actions" aria-labelledby="actions-heading">
        <div class="actions-header">
          <p class="actions-label" aria-hidden="true">Tools</p>
          <h2 id="actions-heading">{{ $t("quickActions") }}</h2>
        </div>

        <div class="action-cards">
          <router-link to="/anonymize" class="action-card">
            <div class="card-icon-wrap" aria-hidden="true">
              <img
                src="../assets/dataprocessing.svg"
                alt=""
                class="card-icon"
              />
            </div>
            <div class="card-body">
              <h3>{{ $t("anonymizeData") }}</h3>
              <p>{{ $t("anonymizeDescription") }}</p>
            </div>
            <div class="card-arrow" aria-hidden="true">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <path
                  d="M4 9h10M10 5l4 4-4 4"
                  stroke="currentColor"
                  stroke-width="1.5"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </div>
          </router-link>
        </div>
      </section>
    </div>
  </BaseLayout>
</template>

<script>
import BaseLayout from "../layouts/BaseLayout.vue";

export default {
  name: "Home",
  components: {
    BaseLayout,
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&family=DM+Mono:wght@400;500&display=swap");

.home {
  max-width: 1000px;
  margin: 0 auto;
}

.welcome-section {
  margin-bottom: 72px;
}

.welcome-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-family: "DM Mono", monospace;
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #0d7b6b;
  margin-bottom: 20px;
}

.badge-dot {
  display: block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #0d7b6b;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.85);
  }
}

h1 {
  font-family: "Syne", sans-serif;
  font-size: 3.6rem;
  font-weight: 800;
  color: #1a2220;
  margin: 0 0 16px;
  letter-spacing: -0.04em;
  line-height: 1.05;
}

.subtitle {
  font-family: "DM Sans", sans-serif;
  font-size: 1.1rem;
  color: #4a5754;
  line-height: 1.65;
  max-width: 500px;
  margin: 0;
}

.actions-header {
  margin-bottom: 24px;
}

.actions-label {
  font-family: "DM Mono", monospace;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #8a9895;
  margin: 0 0 6px;
}

h2 {
  font-family: "Syne", sans-serif;
  font-size: 1.4rem;
  font-weight: 700;
  color: #1a2220;
  margin: 0;
  letter-spacing: -0.02em;
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.action-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px 28px;
  text-decoration: none;
  color: inherit;
  border: 1px solid #e2e6df;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 1px 3px rgba(13, 34, 30, 0.04);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.15s ease;
  position: relative;
}

.action-card:hover {
  border-color: #0d7b6b;
  box-shadow: 0 4px 16px rgba(13, 123, 107, 0.12);
  transform: translateY(-2px);
}

.action-card:hover .card-arrow {
  color: #0d7b6b;
  transform: translateX(3px);
}

.card-icon-wrap {
  width: 52px;
  height: 52px;
  background: #0d7b6b;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-icon {
  width: 28px;
  height: 28px;
}

.card-body {
  flex: 1;
  min-width: 0;
}

h3 {
  font-family: "Syne", sans-serif;
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a2220;
  margin: 0 0 6px;
  letter-spacing: -0.01em;
}

p {
  font-family: "DM Sans", sans-serif;
  font-size: 0.875rem;
  color: #4a5754;
  line-height: 1.5;
  margin: 0;
}

.card-arrow {
  color: #c4ceca;
  flex-shrink: 0;
  transition:
    color 0.2s ease,
    transform 0.2s ease;
}

@media (max-width: 768px) {
  h1 {
    font-size: 2.4rem;
  }

  .action-cards {
    grid-template-columns: 1fr;
  }
}
</style>
