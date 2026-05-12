import { createRouter, createWebHistory } from "vue-router";
import AnonymizeData from "../views/AnonymizeData.vue";
import Home from "../views/Home.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/anonymize",
    name: "AnonymizeData",
    component: AnonymizeData,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
