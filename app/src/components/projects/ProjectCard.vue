<template>
    <div class="project-card">
      <div class="project-header">
        <h3>{{ project.name }}</h3>
        <span class="status" :class="project.status">{{ project.status }}</span>
      </div>
      <p class="description">{{ project.description }}</p>
      <div class="meta">
        <span class="date">Created: {{ formatDate(project.createdAt) }}</span>
        <span class="files">Files: {{ project.filesCount }}</span>
      </div>
      <div class="actions">
        <button class="action-btn view" @click="viewProject">
          View Details
        </button>
        <button class="action-btn edit" @click="editProject">
          Edit
        </button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ProjectCard',
    props: {
      project: {
        type: Object,
        required: true
      }
    },
    methods: {
      formatDate(date) {
        return new Date(date).toLocaleDateString();
      },
      viewProject() {
        this.$emit('view-project', this.project);
      },
      editProject() {
        this.$emit('edit-project', this.project);
      }
    }
  }
  </script>
  
  <style scoped>
  .project-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    border: 1px solid #eee;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
  }
  
  .project-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
  
  .project-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
  }
  
  h3 {
    font-size: 1.2rem;
    color: #333;
    margin: 0;
  }
  
  .status {
    font-size: 0.875rem;
    padding: 4px 12px;
    border-radius: 12px;
    font-weight: 500;
  
    &.active {
      background: #E3F2FD;
      color: #1976D2;
    }
  
    &.completed {
      background: #E8F5E9;
      color: #2E7D32;
    }
  }
  
  .description {
    color: #666;
    font-size: 0.9rem;
    line-height: 1.5;
    margin-bottom: 16px;
  }
  
  .meta {
    display: flex;
    gap: 16px;
    font-size: 0.875rem;
    color: #666;
    margin-bottom: 16px;
  }
  
  .actions {
    display: flex;
    gap: 12px;
  }
  
  .action-btn {
    flex: 1;
    padding: 8px 16px;
    border-radius: 6px;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  
    &.view {
      background: #F5F5F5;
      color: #333;
      border: 1px solid #E0E0E0;
  
      &:hover {
        background-color: #EEEEEE;
      }
    }
  
    &.edit {
      background: transparent;
      color: #3865F2;
      border: 1px solid #3865F2;
  
      &:hover {
        background-color: #F5F8FF;
      }
    }
  }
  </style>
  