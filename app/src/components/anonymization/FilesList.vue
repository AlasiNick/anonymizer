<template>
  <div class="files-section">
    <h2>Uploaded Files</h2>
    <div class="files-list">
      <div v-for="file in files" :key="file.name" class="file-item">
        <div class="file-info">
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
        </div>
        <button class="remove-btn" @click="$emit('remove-file', file)">Remove</button>
      </div>
    </div>
    <div class="actions">
      <button class="process-btn" @click="$emit('process')">
        {{ processButtonText }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FilesList',
  props: {
    files: {
      type: Array,
      required: true
    },
    processButtonText: {
      type: String,
      default: 'Process Files'
    }
  },
  methods: {
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes';
      const k = 1024;
      const sizes = ['Bytes', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
  },
  emits: ['remove-file', 'process']
}
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
    margin-bottom: 20px;
  }

  @media (max-width: 768px) {
    padding: 24px;
  }
}

.files-list {
  margin-top: 20px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;

  &:last-child {
    border-bottom: none;
  }
}

.file-info {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .file-name {
    font-weight: 500;
    color: #333;
  }

  .file-size {
    font-size: 0.9rem;
    color: #666;
  }
}

.remove-btn {
  background: transparent;
  color: #DC3545;
  border: 1px solid #DC3545;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #FFF5F5;
  }
}

.actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.process-btn {
  background: #3865F2;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 32px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #2851D8;
    transform: translateY(-2px);
  }
}
</style>