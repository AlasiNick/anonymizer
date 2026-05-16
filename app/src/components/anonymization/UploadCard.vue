<template>
  <div
    class="upload-card"
    :class="{ 'drag-over': isDragging }"
    @dragover.prevent="isDragging = true"
    @dragleave="isDragging = false"
    @drop.prevent="handleDrop"
  >
    <div class="upload-visual" aria-hidden="true">
      <div class="upload-icon-wrap">
        <svg
          width="32"
          height="32"
          viewBox="0 0 32 32"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M16 4L16 22M16 4L10 10M16 4L22 10"
            stroke="#0D7B6B"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M6 26H26"
            stroke="#0D7B6B"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>
      </div>
      <div class="upload-grid-lines" aria-hidden="true"></div>
    </div>

    <h2>{{ title }}</h2>
    <p class="upload-desc">{{ description }}</p>

    <label :for="inputId" class="sr-only">{{ labelText }}</label>
    <input
      type="file"
      :id="inputId"
      class="file-input"
      @change="$emit('file-upload', $event)"
      :aria-label="labelText"
      :title="labelText"
    />

    <button class="upload-btn" @click="$emit('trigger-upload')">
      <span class="upload-btn-icon" aria-hidden="true">+</span>
      Select File
    </button>

    <p class="format-note">Accepted format: <span class="mono">XML</span></p>
  </div>
</template>

<script>
export default {
  name: "UploadCard",
  props: {
    title: {
      type: String,
      default: "Drop your file here",
    },
    description: {
      type: String,
      default: "Select an XML file to begin the anonymization process",
    },
    labelText: {
      type: String,
      default: "Choose file to anonymize",
    },
    inputId: {
      type: String,
      required: true,
    },
  },
  emits: ["file-upload", "trigger-upload"],
  data() {
    return {
      isDragging: false,
    };
  },
  methods: {
    handleDrop(event) {
      this.isDragging = false;
      const files = event.dataTransfer.files;
      if (files.length > 0) {
        this.$emit("file-upload", { target: { files } });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import url("https://fonts.googleapis.com/css2?family=Syne:wght@600;700&family=DM+Sans:wght@400;500&family=DM+Mono:wght@500&display=swap");

.upload-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 48px 40px;
  text-align: center;
  border: 1.5px dashed #c4ceca;
  transition:
    border-color 0.2s ease,
    background 0.2s ease;
  position: relative;
  overflow: hidden;

  &:hover {
    border-color: #0d7b6b;
    background: #fafcfb;
  }

  &.drag-over {
    border-color: #0d7b6b;
    background: #e0f4f0;
  }

  h2 {
    font-family: "Syne", sans-serif;
    font-size: 1.5rem;
    font-weight: 700;
    color: #1a2220;
    margin: 0 0 10px;
    letter-spacing: -0.02em;
  }

  @media (max-width: 768px) {
    padding: 32px 24px;
  }
}

.upload-visual {
  position: relative;
  margin-bottom: 28px;
  display: flex;
  justify-content: center;
}

.upload-icon-wrap {
  width: 72px;
  height: 72px;
  background: #e0f4f0;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #aaddd5;
  position: relative;
  z-index: 1;
}

.upload-desc {
  font-family: "DM Sans", sans-serif;
  font-size: 0.9rem;
  color: #4a5754;
  margin: 0 0 28px;
  line-height: 1.6;
}

.file-input {
  display: none;
}

.upload-btn {
  background: #0d7b6b;
  color: #ffffff;
  border: none;
  border-radius: 6px;
  padding: 11px 24px;
  font-family: "DM Sans", sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition:
    background 0.2s ease,
    transform 0.15s ease;
  letter-spacing: 0.01em;

  &:hover {
    background: #095f52;
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
}

.upload-btn-icon {
  font-size: 1.1rem;
  font-weight: 400;
  line-height: 1;
}

.format-note {
  font-family: "DM Sans", sans-serif;
  font-size: 0.8rem;
  color: #8a9895;
  margin: 16px 0 0;
}

.mono {
  font-family: "DM Mono", monospace;
  font-size: 0.78rem;
  background: #eef0ec;
  color: #0d7b6b;
  padding: 1px 6px;
  border-radius: 3px;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
