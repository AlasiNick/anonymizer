<template>
  <BaseLayout>
    <div class="anon-view">
      <nav class="breadcrumb" aria-label="Navigation">
        <PrimaryButton @click="goToHome" class="back-btn">
          <template #icon>
            <svg
              width="14"
              height="14"
              viewBox="0 0 14 14"
              fill="none"
              aria-hidden="true"
            >
              <path
                d="M10 7H4M4 7l3-3M4 7l3 3"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </template>
          Back
        </PrimaryButton>
      </nav>

      <header class="page-header">
        <div class="header-label" aria-hidden="true">
          <span class="label-line"></span>
          <span class="label-text">Privacy pipeline</span>
          <span class="label-line"></span>
        </div>
        <h1>Data Anonymization</h1>
        <p class="page-subtitle">
          Strip personal identifiers from your dataset — irreversibly and
          verifiably.
        </p>
      </header>

      <div class="tool-grid">
        <UploadCard
          inputId="file-upload-anon"
          title="Drop your file here"
          description="Select an XML file to begin. Your data never leaves this pipeline."
          labelText="Choose XML file to anonymize"
          @file-upload="handleFileUpload"
          @trigger-upload="triggerFileInput"
        />

        <InfoCard
          title="What anonymization ensures"
          description="Identifiers are removed or masked through an irreversible process:"
          :items="[
            'GDPR & data protection compliance',
            'Irreversible identity protection',
            'Reduced breach exposure risk',
            'Safe publication of datasets',
          ]"
        />
      </div>

      <FilesList
        v-if="uploadedFile"
        :file="uploadedFile"
        :loading="loading"
        processButtonText="Begin Anonymization"
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
      if (!this.uploadedFile) {
        alert("Please upload an XML file.");
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
        alert(
          "ZIP with anonymized data and risk assessment downloaded successfully!",
        );
      } catch (error) {
        console.error(error);
        alert(error.message || "Failed to process XML.");
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
      if (!file) return;
      if (!file.name.toLowerCase().endsWith(".xml")) {
        alert("Only XML files are accepted.");
        return;
      }
      this.uploadedFile = file;
    },
    async fileToBase64(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result.split(",")[1]);
        reader.onerror = reject;
      });
    },
    removeFile() {
      this.uploadedFile = null;
    },
    confirmProjectCreation({ name, description }) {
      const store = useProjectStore();
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
@import url("https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&family=DM+Mono:wght@400;500&display=swap");

.anon-view {
  max-width: 1100px;
  margin: 0 auto;
}

.breadcrumb {
  margin-bottom: 36px;
}

.back-btn {
  background: transparent !important;
  color: #4a5754 !important;
  border: 1px solid #d4d9d7 !important;
  box-shadow: none !important;
  font-size: 0.85rem !important;
  padding: 8px 16px !important;

  &:hover {
    background: #eef0ec !important;
    transform: none !important;
    box-shadow: none !important;
    color: #1a2220 !important;
  }
}

.page-header {
  text-align: center;
  margin-bottom: 48px;
}

.header-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}

.label-text {
  font-family: "DM Mono", monospace;
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: #0d7b6b;
}

.label-line {
  display: block;
  width: 40px;
  height: 1px;
  background: #c4ceca;
}

h1 {
  font-family: "Syne", sans-serif;
  font-size: 3rem;
  font-weight: 800;
  color: #1a2220;
  margin: 0 0 16px;
  letter-spacing: -0.04em;
  line-height: 1.1;

  @media (max-width: 768px) {
    font-size: 2.2rem;
  }
}

.page-subtitle {
  font-family: "DM Sans", sans-serif;
  font-size: 1.05rem;
  color: #4a5754;
  margin: 0;
  max-width: 480px;
  margin-inline: auto;
  line-height: 1.6;
}

.tool-grid {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 20px;
  margin-bottom: 0;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}
</style>
