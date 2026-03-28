<template>
  <BaseLayout>
    <div class="risk-assessment-content">
      <h1>Risk Assessment</h1>
      <div class="risk-assessment-container">
        <p>Welcome to the Risk Assessment tool. This feature is coming soon.</p>
        <!-- Add your risk assessment content here -->
      </div>
    </div>
  </BaseLayout>
</template>

<script>
import BaseLayout from "../layouts/BaseLayout.vue";

export default {
  name: "RiskAssessment",
  components: {
    BaseLayout,
  },
};
</script>

<style lang="scss">
@use '../styles/variables' as v;
@use '../styles/mixins' as m;

.risk-assessment-content {
  @include m.flex-column;
  @include m.container;
  align-items: center;

  @include m.mobile {
    margin-top: v.$spacing-lg;
    padding: v.$spacing-md;
  }

  h1 {
    color: v.$text-color;
    font-family: v.$primary-font;
    font-size: 32px;
    margin-bottom: v.$spacing-xl;

    @include m.mobile {
      font-size: v.$font-size-lg;
      margin-bottom: v.$spacing-lg;
    }
  }
}

.risk-assessment-container {
  background: v.$white;
  border-radius: 10px;
  padding: v.$spacing-xl;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 800px;

  @include m.mobile {
    padding: v.$spacing-lg;
  }

  p {
    font-size: v.$font-size-md;
    color: v.$text-color;
    text-align: center;
  }
}
</style> 