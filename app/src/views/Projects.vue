<template>
  <BaseLayout>
    <div class="projects">
      <div class="header">
        <h1>{{ $t('projects') }}</h1>
        <button class="new-project-btn" @click="showCreateModal = true">
          <span class="icon">+</span>
          <router-link to="/pseudonymize" class="new-project-txt"> New Project</router-link>
        </button>
      </div>

      <div class="projects-grid" v-if="dynamicProjects.length || staticProjects.length">
  <!-- Dynamic projects from store -->
  <ProjectCard
    v-for="project in dynamicProjects"
    :key="'dynamic-' + project.createdAt"
    :project="project"
    @view-project="openProjectPreview"
    @edit-project="editProject"
  />

  <!-- Static projects -->
  <ProjectCard
    v-for="project in staticProjects"
    :key="'static-' + project.id"
    :project="project"
    @view-project="openProjectPreview"
    @edit-project="editProject"
  />
</div>

      <div v-else class="empty-state">
        <!-- <img src="../assets/empty-projects.svg" alt="No projects" class="empty-icon" /> --> 
        <h2>No Projects Yet</h2>
        <p>Create your first project to start managing your data processing tasks</p>
        <button class="new-project-btn" @click="showCreateModal = true">
          <span class="icon">+</span>
          Create Project
        </button>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h2>{{ selectedProject.name }}</h2>
        <p><strong>Description:</strong> {{ selectedProject.description || 'No description' }}</p>
        <pre>{{ selectedProject.processedOutput }}</pre>
        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>
  </BaseLayout>
</template>

<script>
import { computed } from 'vue'
import BaseLayout from '../layouts/BaseLayout.vue'
import ProjectCard from '../components/projects/ProjectCard.vue'
import { useProjectStore } from '../stores/projectStore'

export default {
  name: 'Projects',
  components: {
    BaseLayout,
    ProjectCard
  },
  setup() {
    const staticProjects = [
      {
        id: 1,
        name: 'Anonymization example',
        description: 'Anonymizing customer database for analytics',
        status: 'active',
        createdAt: '2024-03-20',
        filesCount: 3,
        processedOutput: 'Anonymized customer data for analytics'

      },
      {
        id: 2,
        name: 'Employee Records',
        description: 'Processing HR data for compliance',
        status: 'completed',
        createdAt: '2024-03-15',
        filesCount: 5,
        processedOutput: 'Anonymized employee records for GDPR compliance'
      }
    ]

    let dynamicProjects = []

    try {
      const projectStore = useProjectStore()
      dynamicProjects = computed(() => projectStore.uploadedProjects)
    } catch (e) {
      console.warn('Pinia store not initialized — only using static projects.')
    }
    const editProject = (id) => {
      console.log('Editing project:', id)
    }


    return {
      staticProjects,
      dynamicProjects,
      editProject
    }
  }, 
    data() {
    return {
      selectedProject: null,
      showModal: false
    }
  },
  methods: {
    openProjectPreview(project) {
      this.selectedProject = project;
      this.showModal = true;
    },
    closeModal() {
      this.showModal = false;
      this.selectedProject = null;
    }
  }
}
</script>


<style scoped>
.projects {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

h1 {
  font-size: 2.5rem;
  color: #333;
  margin: 0;
}

.new-project-btn {
  background: #3865F2;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;

  &:hover {
    background-color: #2851D8;
    transform: translateY(-2px);
  }

  .new-project-txt {
    font-size: 1rem;
    font-weight: 500;
    color: white;
    text-decoration: none;
  }

  .icon {
    font-size: 1.2rem;
    font-weight: bold;
  }
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;

  .empty-icon {
    width: 120px;
    height: 120px;
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
}

@media (max-width: 768px) {
  .projects {
    padding: 20px;
  }

  .header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  h1 {
    font-size: 2rem;
  }

  .projects-grid {
    grid-template-columns: 1fr;
  }
}

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
  max-width: 600px;
  width: 90%;
  max-height: 80%;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.modal-content pre {
  background: #f6f8fa;
  padding: 12px;
  border-radius: 6px;
  white-space: pre-wrap;
  word-break: break-word;
  margin-top: 12px;
}

.close-btn {
  margin-top: 20px;
  padding: 8px 16px;
  background: #3865F2;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
}
</style>
