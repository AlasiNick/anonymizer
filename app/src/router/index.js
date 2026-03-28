import { createRouter, createWebHistory } from 'vue-router';
import AnonymizeData from '../views/AnonymizeData.vue';
import PseudonymizeData from '../views/PseudonymizeData.vue';
import Projects from '../views/Projects.vue';
import RiskAssessment from '../views/RiskAssessment.vue';
import Home from '../views/Home.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/anonymize',
    name: 'AnonymizeData',
    component: AnonymizeData,
  },
  {
    path: '/pseudonymize',
    name: 'PseudonymizeData',
    component: PseudonymizeData,
  },
  {
    path: '/projects',
    name: 'Projects',
    component: Projects,
  },
  {
    path: '/risk-assessment',
    name: 'RiskAssessment',
    component: RiskAssessment,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
