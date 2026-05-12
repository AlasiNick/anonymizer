<template>
  <BaseLayout>
    <PrimaryButton @click="goToHome">Back to Home</PrimaryButton>
    <div class="anonymize">
      <div class="header">
        <h1>Anonymize Data</h1>
        <p class="subtitle">
          Remove identifiers to achieve irreversible privacy protection
        </p>
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
            'Compliance with GDPP',
            'Irreversible identity protection',
            'Risk mitigation in data breaches',
            'Safe publishing of datasets',
          ]"
        />
      </div>

      <FilesList
        v-if="uploadedFile"
        :file="uploadedFile"
        :loading="loading"
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
import BaseLayout from "../layouts/BaseLayout.vue";
import { useProjectStore } from "../stores/projectStore";
import UploadCard from "../components/anonymization/UploadCard.vue";
import InfoCard from "../components/anonymization/InfoCard.vue";
import FilesList from "../components/anonymization/FilesList.vue";
import ProjectInfoModal from "../components/anonymization/ProjectInfoModal.vue";
import PrimaryButton from "../components/globals/PrimaryButton.vue";

export default {
  name: "AnonymizeData",
  components: {
    BaseLayout,
    UploadCard,
    InfoCard,
    FilesList,
    ProjectInfoModal,
    PrimaryButton,
  },
  data() {
    return {
      uploadedFile: null,
      processedContent: null,
      showProjectInfoModal: false,
      loading: false,
    };
  },
  methods: {
    async processAnonymization() {
      console.log("PROCESS TRIGGERED");

      if (!this.uploadedFile) {
        alert("Please upload XML file.");
        return;
      }

      try {
        this.loading = true;

        const api = (await import("../services/api")).default;

        const base64Content = await this.fileToBase64(this.uploadedFile);

        await api.downloadUnmappedFieldsCsv({
          fileName: this.uploadedFile.name,
          base64Content,
        });

        alert("CSV downloaded successfully!");
      } catch (error) {
        console.error(error);
        alert(error.message || "Failed to process XML");
      } finally {
        this.loading = false;
      }
    },
    triggerFileInput() {
      document.getElementById("file-upload-anon").click();
    },
    goToHome() {
      this.$router.push({ path: "/" });
    },
    handleFileUpload(event) {
      const file = event.target.files?.[0];

      if (!file) {
        return;
      }

      if (!file.name.toLowerCase().endsWith(".xml")) {
        alert("Only XML files are allowed.");
        return;
      }

      this.uploadedFile = file;
    },
    async fileToBase64(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.readAsDataURL(file);

        reader.onload = () => {
          const result = reader.result;

          const base64 = result.split(",")[1];

          resolve(base64);
        };

        reader.onerror = reject;
      });
    },
    removeFile() {
      this.uploadedFile = null;
    },
    getFileExtension(file) {
      return file.name.split(".").pop().toLowerCase();
    },
    confirmProjectCreation({ name, description }) {
      const store = useProjectStore();
      const isSingleFile = this.uploadedFiles.length === 1;

      store.addProject({
        name: name || this.uploadedFile.name,
        description: description || "No description provided.",
        files: [this.uploadedFile],
        processedOutput: this.processedContent,
        createdAt: new Date(),
        status: "active",
        filesCount: 1,
      });

      this.showProjectInfoModal = false;
      this.$router.push({ path: "/projects" });
    },
  },
};
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
