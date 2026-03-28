<template>
  <div class="info-card">
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <ul>
      <li v-for="(item, index) in items" :key="index">{{ item }}</li>
    </ul>
    <div class="supported-formats">
      <h4>Supported Formats</h4>
      <div class="format-tags">
        <span class="tag" v-for="(format, index) in formats" :key="index">{{ format }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'InfoCard',
  props: {
    title: {
      type: String,
      required: true
    },
    description: {
      type: String,
      required: true
    },
    items: {
      type: Array,
      required: true
    },
    formats: {
      type: Array,
      default: () => ['CSV', 'JSON', 'XML', 'Excel']
    }
  }
}
</script>

<style lang="scss" scoped>
.info-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #eee;

  h3 {
    font-size: 1.4rem;
    color: #333;
    margin-bottom: 16px;
  }

  ul {
    list-style-type: none;
    padding: 0;
    margin-bottom: 24px;

    li {
      color: #666;
      margin-bottom: 12px;
      padding-left: 24px;
      position: relative;

      &::before {
        content: '✓';
        color: #3865F2;
        position: absolute;
        left: 0;
      }
    }
  }

  @media (max-width: 768px) {
    padding: 24px;
  }
}

.supported-formats {
  h4 {
    font-size: 1.1rem;
    color: #333;
    margin-bottom: 12px;
  }
}

.format-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  .tag {
    background: #F5F8FF;
    color: #3865F2;
    padding: 4px 12px;
    border-radius: 16px;
    font-size: 0.9rem;
    font-weight: 500;
  }
}
</style>