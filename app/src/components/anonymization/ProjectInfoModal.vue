<template>
  <div v-if="show" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h2>Name Your Project</h2>
      <input
        type="text"
        v-model="projectName"
        placeholder="Project Name"
        class="modal-input"
      />
      <textarea
        v-model="projectDescription"
        placeholder="Project Description"
        class="modal-textarea"
      ></textarea>
      <button class="confirm-btn" @click="$emit('confirm', { name: projectName, description: projectDescription })">
        Create Project
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProjectInfoModal',
  props: {
    show: {
      type: Boolean,
      required: true
    },
    initialName: {
      type: String,
      default: ''
    },
    initialDescription: {
      type: String,
      default: ''
    }
  },
  emits: ['close', 'confirm'],
  data() {
    return {
      projectName: this.initialName,
      projectDescription: this.initialDescription
    }
  },
  watch: {
    initialName(newVal) {
      this.projectName = newVal;
    },
    initialDescription(newVal) {
      this.projectDescription = newVal;
    }
  }
}
</script>

<style lang="scss" scoped>
.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 16px;
  box-sizing: border-box;
}

.modal-input,
.modal-textarea {
  width: 100%;
  max-width: 100%;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  box-sizing: border-box;
}

.modal-textarea {
  min-height: 100px;
  resize: vertical;
}

.confirm-btn {
  background: #3865F2;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #2851D8;
  }
}
</style>