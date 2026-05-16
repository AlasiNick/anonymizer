<template>
  <div class="files-section">
    <div class="section-header">
      <p class="section-label">Ready to process</p>
      <h2>Queued document</h2>
    </div>

    <div v-if="file" class="file-item">
      <div class="file-left">
        <div class="file-type-badge" aria-label="XML file">XML</div>
        <div class="file-meta">
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
        </div>
      </div>

      <button
        class="remove-btn"
        @click="$emit('remove-file')"
        :aria-label="`Remove ${file.name}`"
      >
        <svg
          width="14"
          height="14"
          viewBox="0 0 14 14"
          fill="none"
          aria-hidden="true"
        >
          <path
            d="M1 1l12 12M13 1L1 13"
            stroke="currentColor"
            stroke-width="1.5"
            stroke-linecap="round"
          />
        </svg>
        Remove
      </button>
    </div>

    <div class="process-area">
      <div class="process-info" aria-hidden="true">
        <span class="info-chip">
          <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
            <circle cx="6" cy="6" r="5" stroke="#C9873A" stroke-width="1.2" />
            <path
              d="M6 5v4M6 3.5v.5"
              stroke="#C9873A"
              stroke-width="1.2"
              stroke-linecap="round"
            />
          </svg>
          Output includes anonymized file + risk assessment report
        </span>
      </div>

      <button
        class="process-btn"
        :disabled="loading"
        @click="$emit('process')"
        :aria-busy="loading"
      >
        <span v-if="loading" class="loading-state">
          <span class="spinner" aria-hidden="true"></span>
          Processing…
        </span>
        <span v-else>
          {{ processButtonText }}
          <svg
            width="14"
            height="14"
            viewBox="0 0 14 14"
            fill="none"
            aria-hidden="true"
          >
            <path
              d="M2 7h10M8 3l4 4-4 4"
              stroke="currentColor"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
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
      default: "Begin Anonymization",
    },
  },
  emits: ["remove-file", "process"],
  methods: {
    formatFileSize(bytes) {
      if (!bytes || bytes === 0) return "0 B";
      const k = 1024;
      const sizes = ["B", "KB", "MB", "GB"];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + " " + sizes[i];
    },
  },
};
</script>

<style lang="scss" scoped>
@import url("https://fonts.googleapis.com/css2?family=Syne:wght@600;700&family=DM+Sans:wght@400;500&family=DM+Mono:wght@500&display=swap");

.files-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 28px 32px;
  border: 1px solid #e2e6df;
  margin-top: 24px;
  box-shadow: 0 1px 3px rgba(13, 34, 30, 0.04);

  @media (max-width: 768px) {
    padding: 20px;
  }
}

.section-header {
  margin-bottom: 20px;
}

.section-label {
  font-family: "DM Mono", monospace;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #0d7b6b;
  margin: 0 0 4px;
}

h2 {
  font-family: "Syne", sans-serif;
  font-size: 1.2rem;
  font-weight: 700;
  color: #1a2220;
  margin: 0;
  letter-spacing: -0.01em;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border: 1px solid #e2e6df;
  border-radius: 8px;
  background: #f7f8f6;
  gap: 16px;

  @media (max-width: 600px) {
    flex-direction: column;
    align-items: flex-start;
  }
}

.file-left {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.file-type-badge {
  font-family: "DM Mono", monospace;
  font-size: 0.7rem;
  font-weight: 500;
  color: #095f52;
  background: #e0f4f0;
  border: 1px solid #aaddd5;
  border-radius: 4px;
  padding: 4px 8px;
  flex-shrink: 0;
  letter-spacing: 0.04em;
}

.file-meta {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.file-name {
  font-family: "DM Sans", sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  color: #1a2220;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  font-family: "DM Mono", monospace;
  font-size: 0.75rem;
  color: #8a9895;
}

.remove-btn {
  background: transparent;
  color: #c0392b;
  border: 1px solid #dec5c3;
  border-radius: 6px;
  padding: 7px 14px;
  font-family: "DM Sans", sans-serif;
  font-size: 0.82rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
  flex-shrink: 0;

  &:hover {
    background: #fdf0ee;
    border-color: #c0392b;
  }
}

.process-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eef0ec;
  gap: 16px;
  flex-wrap: wrap;
}

.process-info {
  display: flex;
  align-items: center;
}

.info-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: "DM Sans", sans-serif;
  font-size: 0.8rem;
  color: #4a5754;
  background: #fdf2e3;
  border: 1px solid #edd5aa;
  padding: 6px 12px;
  border-radius: 20px;
}

.process-btn {
  background: #1a2220;
  color: #ffffff;
  border: none;
  border-radius: 6px;
  padding: 12px 24px;
  font-family: "DM Sans", sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition:
    background 0.2s ease,
    transform 0.15s ease;
  letter-spacing: 0.01em;
  white-space: nowrap;

  &:hover:not(:disabled) {
    background: #0d7b6b;
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.loading-state {
  display: flex;
  align-items: center;
  gap: 10px;
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
