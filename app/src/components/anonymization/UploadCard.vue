<template>
  <div class="upload-card">
    <img src="../../assets/upload.svg" alt="Upload" class="upload-icon" />
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
    <label :for="inputId" class="sr-only">{{ labelText }}</label>
    <input 
      type="file" 
      :id="inputId"
      class="file-input" 
      @change="$emit('file-upload', $event)" 
      multiple
      :aria-label="labelText"
      :title="labelText"
    />
    <button class="upload-btn" @click="$emit('trigger-upload')">
      <span class="icon">+</span>
      Select Files
    </button>
  </div>
</template>

<script>
export default {
  name: 'UploadCard',
  props: {
    title: {
      type: String,
      default: 'Upload Your Data'
    },
    description: {
      type: String,
      default: 'Drag and drop your files here or click to browse'
    },
    labelText: {
      type: String,
      default: 'Choose files'
    },
    inputId: {
      type: String,
      required: true
    }
  },
  emits: ['file-upload', 'trigger-upload']
}
</script>

<style lang="scss" scoped>
.upload-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  border: 2px dashed #E0E0E0;
  transition: all 0.3s ease;

  &:hover {
    border-color: #3865F2;
  }

  .upload-icon {
    width: 64px;
    height: 64px;
    margin-bottom: 24px;
  }

  h2 {
    font-size: 1.8rem;
    color: #333;
    margin-bottom: 12px;
  }

  p {
    color: #666;
    margin-bottom: 24px;
  }

  @media (max-width: 768px) {
    padding: 24px;
  }
}

.file-input {
  display: none;
}

.upload-btn {
  background: #3865F2;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;

  &:hover {
    background: #2851D8;
    transform: translateY(-2px);
  }

  .icon {
    font-size: 1.2rem;
    font-weight: bold;
  }
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