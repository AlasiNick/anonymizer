<template>
  <div class="info-card">
    <div class="info-header">
      <div class="shield-icon" aria-hidden="true">
        <svg
          width="18"
          height="20"
          viewBox="0 0 18 20"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M9 1L1.5 4V9.5C1.5 13.6 4.8 17.4 9 18.5C13.2 17.4 16.5 13.6 16.5 9.5V4L9 1Z"
            stroke="#0D7B6B"
            stroke-width="1.5"
            stroke-linejoin="round"
          />
          <path
            d="M6 10l2 2 4-4"
            stroke="#0D7B6B"
            stroke-width="1.5"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </div>
      <h3>{{ title }}</h3>
    </div>

    <p class="info-desc">{{ description }}</p>

    <ul class="guarantee-list">
      <li v-for="(item, index) in items" :key="index">
        <span class="check-mark" aria-hidden="true">
          <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
            <path
              d="M2 6l3 3 5-5"
              stroke="#0D7B6B"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </span>
        {{ item }}
      </li>
    </ul>

    <div class="supported-formats">
      <p class="formats-label">Supported formats</p>
      <div class="format-tags">
        <span class="tag" v-for="(format, index) in formats" :key="index">
          {{ format }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "InfoCard",
  props: {
    title: {
      type: String,
      required: true,
    },
    description: {
      type: String,
      required: true,
    },
    items: {
      type: Array,
      required: true,
    },
    formats: {
      type: Array,
      default: () => ["XML"],
    },
  },
};
</script>

<style lang="scss" scoped>
@import url("https://fonts.googleapis.com/css2?family=Syne:wght@600;700&family=DM+Sans:wght@400;500&family=DM+Mono:wght@500&display=swap");

.info-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 28px 32px;
  border: 1px solid #e2e6df;
  box-shadow: 0 1px 3px rgba(13, 34, 30, 0.04);

  @media (max-width: 768px) {
    padding: 24px;
  }
}

.info-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.shield-icon {
  flex-shrink: 0;
}

h3 {
  font-family: "Syne", sans-serif;
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a2220;
  margin: 0;
  letter-spacing: -0.01em;
}

.info-desc {
  font-family: "DM Sans", sans-serif;
  font-size: 0.875rem;
  color: #4a5754;
  line-height: 1.6;
  margin: 0 0 20px;
}

.guarantee-list {
  list-style: none;
  padding: 0;
  margin: 0 0 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;

  li {
    font-family: "DM Sans", sans-serif;
    font-size: 0.875rem;
    color: #1a2220;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 12px;
    background: #f7f8f6;
    border-radius: 6px;
    border-left: 2px solid #0d7b6b;
  }
}

.check-mark {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.supported-formats {
  border-top: 1px solid #eef0ec;
  padding-top: 16px;
}

.formats-label {
  font-family: "DM Mono", monospace;
  font-size: 0.7rem;
  color: #8a9895;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin: 0 0 10px;
}

.format-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  background: #e0f4f0;
  color: #095f52;
  padding: 3px 10px;
  border-radius: 4px;
  font-family: "DM Mono", monospace;
  font-size: 0.78rem;
  font-weight: 500;
  border: 1px solid #aaddd5;
}
</style>
