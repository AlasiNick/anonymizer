<template>
  <BaseLayout>
    <div class="anonymize">
      <div class="header">
        <h1>Anonymize Data</h1>
        <p class="subtitle">Remove identifiers to achieve irreversible privacy protection</p>
      </div>

      <div class="upload-section">
        <UploadCard
          inputId="file-upload-anon"
          labelText="Choose files to anonymize"
          @file-upload="handleFileUpload"
          @trigger-upload="triggerFileInput"
        />

        <InfoCard
          title="About Anonymization"
          description="Anonymization removes or masks personal identifiers irreversibly, ensuring:"
          :items="[
            'Compliance with GDPR and HIPAA',
            'Irreversible identity protection',
            'Risk mitigation in data breaches',
            'Safe publishing of datasets'
          ]"
        />
      </div>

      <FilesList
        v-if="uploadedFiles.length > 0"
        :files="uploadedFiles"
        processButtonText="Start Anonymization"
        @remove-file="removeFile"
        @process="processAnonymization"
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

export default {
  name: 'AnonymizeData',
  components: {
    BaseLayout,
    UploadCard,
    InfoCard,
    FilesList,
    ProjectInfoModal
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
      document.getElementById('file-upload-anon').click();
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
    processAnonymization() {
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

      formData.append("processType", "anonymize");

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
.anonymize {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
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