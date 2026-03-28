<template>
  <BaseLayout>
    <div class="pseudonymize">
      <div class="header">
        <h1>Pseudonymize Data</h1>
        <p class="subtitle">Create reversible data masks while maintaining data utility</p>
        <!-- Button container -->
        <div class="action-button">
          <PrimaryButton @click="$router.push({ name: 'AnonymizeData' })">
            <template #icon>
                <path d="M18 6L6 18"></path>
                <path d="M6 6l12 12"></path>
            </template>
            Anonymize
          </PrimaryButton>
        </div>
      </div>

      <div class="upload-section">
        <UploadCard
          inputId="file-upload-pseudo"
          labelText="Choose files to pseudonymize"
          @file-upload="handleFileUpload"
          @trigger-upload="triggerFileInput"
        />

        <InfoCard
          title="About Pseudonymization"
          description="Pseudonymization replaces sensitive data with reversible identifiers, allowing for:"
          :items="[
            'Data analysis while protecting privacy',
            'Reversible data transformation',
            'Compliance with data protection regulations',
            'Secure data sharing between organizations'
          ]"
        />
      </div>

      <FilesList
        v-if="uploadedFiles.length > 0"
        :files="uploadedFiles"
        processButtonText="Start Pseudonymization"
        @remove-file="removeFile"
        @process="processPseudonymization"
      />

      <ProjectInfoModal
        :show="showProjectInfoModal"
        @close="showProjectInfoModal = false"
        @confirm="confirmProjectCreation"
      />
    </div>
  </BaseLayout>
</template>

<script>
import BaseLayout from '../layouts/BaseLayout.vue';
import { useProjectStore } from '../stores/projectStore';
import UploadCard from '../components/anonymization/UploadCard.vue';
import InfoCard from '../components/anonymization/InfoCard.vue';
import FilesList from '../components/anonymization/FilesList.vue';
import ProjectInfoModal from '../components/anonymization/ProjectInfoModal.vue';
import PrimaryButton from '../components/globals/PrimaryButton.vue';

export default {
  name: 'PseudonymizeData',
  components: {
    BaseLayout,
    UploadCard,
    InfoCard,
    FilesList,
    ProjectInfoModal,
    PrimaryButton 
  },
  data() {
    return {
      uploadedFiles: [],
      processedContent: '',
      showProjectInfoModal: false,
      newProjectName: '',
      newProjectDescription: ''
    }
  },
  methods: {
    triggerFileInput() {
      document.getElementById('file-upload-pseudo').click();
    },
    handleFileUpload(event) {
      const files = Array.from(event.target.files);
      this.uploadedFiles.push(...files);
    },
    removeFile(file) {
      const index = this.uploadedFiles.indexOf(file);
      if (index > -1) {
        this.uploadedFiles.splice(index, 1);
      }
    },
    processPseudonymization() {
      console.log('Starting pseudonymization for files:', this.uploadedFiles);

      if (this.uploadedFiles.length === 0) {
        alert("Please upload at least one file.");
        return;
      }

      const formData = new FormData();
      const isSingleFile = this.uploadedFiles.length === 1;

      if (isSingleFile) {
        formData.append("file", this.uploadedFiles[0]);
      } else {
        const baseType = this.getFileExtension(this.uploadedFiles[0]);
        const mismatch = this.uploadedFiles.some(file => this.getFileExtension(file) !== baseType);

        if (mismatch) {
          alert("All uploaded files must have the same extension (.json or .xml).");
          return;
        }

        this.uploadedFiles.forEach(file => {
          formData.append("files", file);
        });
      }

      formData.append("processType", "pseudonymize");

      const endpoint = isSingleFile ? "/api/upload/single" : "/api/upload/multiple";

      fetch(`http://localhost:8080${endpoint}`, {
        method: "POST",
        body: formData,
      })
        .then(response => response.text())
        .then(result => {
          this.processedContent = result;
          this.showProjectInfoModal = true;
          alert("File(s) processed successfully!");
        })
        .catch(error => {
          console.error("Error uploading file(s):", error);
          alert("Error uploading file(s): " + error.message);
        });
    },
    getFileExtension(file) {
      return file.name.split('.').pop().toLowerCase();
    },
    confirmProjectCreation({ name, description }) {
      const store = useProjectStore();
      const isSingleFile = this.uploadedFiles.length === 1;

      store.addProject({
        name: name || (isSingleFile ? this.uploadedFiles[0].name : `${this.uploadedFiles.length} files`),
        description: description || 'No description provided.',
        files: this.uploadedFiles,
        processedOutput: this.processedContent,
        createdAt: new Date(),
        status: 'active',
        filesCount: this.uploadedFiles.length
      });

      this.showProjectInfoModal = false;
      this.$router.push({ path: '/projects' });
    }
  }
}
</script>

<style lang="scss" scoped>
.pseudonymize {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

.action-button {
  position: absolute;
  top: 2px; 
  right: 12px;

  @media (max-width: 768px) {
    position: static;
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.header {
  text-align: center;
  margin-bottom: 40px;

  h1 {
    font-size: 2.5rem;
    color: #333;
    margin-bottom: 12px;
  }

  .subtitle {
    font-size: 1.2rem;
    color: #666;
  }
}

.upload-section {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 24px;
  margin-bottom: 40px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}
</style>