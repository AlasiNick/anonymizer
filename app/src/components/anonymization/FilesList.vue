<template>
  <div class="files-section">
    <h2>Uploaded XML Document</h2>

    <div v-if="file" class="file-item">
      <div class="file-info">
        <div class="file-header">
          <span class="xml-badge">XML</span>

          <span class="file-name">
            {{ file.name }}
          </span>
        </div>

        <span class="file-size">
          {{ formatFileSize(file.size) }}
        </span>
      </div>

      <button class="remove-btn" @click="$emit('remove-file')">Remove</button>
    </div>

    <div class="actions">
      <button class="process-btn" :disabled="loading" @click="$emit('process')">
        <span v-if="loading"> Processing... </span>

        <span v-else>
          {{ processButtonText }}
        </span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "FilesList",

  props: {
    file: {
      type: Object,
      required: true,
    },

    loading: {
      type: Boolean,
      default: false,
    },

    processButtonText: {
      type: String,
      default: "Start Anonymization",
    },
  },

  emits: ["remove-file", "process"],

  methods: {
    formatFileSize(bytes) {
      if (!bytes || bytes === 0) {
        return "0 Bytes";
      }

      const k = 1024;

      const sizes = ["Bytes", "KB", "MB", "GB"];

      const i = Math.floor(Math.log(bytes) / Math.log(k));

      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
    },
  },
};
</script>

<style lang="scss" scoped>
.files-section {
  background: white;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #eee;
  margin-top: 24px;

  h2 {
    color: #333;
    margin-bottom: 24px;
  }

  @media (max-width: 768px) {
    padding: 24px;
  }
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 20px;

  border: 1px solid #eee;
  border-radius: 12px;

  background: #fafafa;

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}

.file-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.xml-badge {
  background: #3865f2;
  color: white;

  font-size: 0.75rem;
  font-weight: 600;

  padding: 4px 8px;

  border-radius: 6px;
}

.file-name {
  font-weight: 600;
  color: #333;

  word-break: break-word;
}

.file-size {
  font-size: 0.9rem;
  color: #666;
}

.remove-btn {
  background: transparent;

  color: #dc3545;

  border: 1px solid #dc3545;

  border-radius: 8px;

  padding: 8px 14px;

  font-size: 0.9rem;

  cursor: pointer;

  transition: all 0.2s ease;

  &:hover {
    background: #fff5f5;
  }
}

.actions {
  margin-top: 24px;

  display: flex;
  justify-content: flex-end;
}

.process-btn {
  background: #3865f2;
  color: white;

  border: none;

  border-radius: 8px;

  padding: 12px 32px;

  font-size: 1rem;
  font-weight: 500;

  cursor: pointer;

  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    background: #2851d8;
    transform: translateY(-2px);
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
}
</style>
