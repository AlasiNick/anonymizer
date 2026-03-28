import { defineStore } from 'pinia'

export const useProjectStore = defineStore('project', {
  state: () => ({
    uploadedProjects: []
  }),
  actions: {
    addProject(project) {
      this.uploadedProjects.push(project)
    },
    clearProjects() {
      this.uploadedProjects = []
    }
  }
})
