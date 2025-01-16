import { createWebHistory, createRouter } from "vue-router";
import Home from "./components/Home.vue";
import Login from "./components/Login.vue";
import Register from "./components/Register.vue";
import Resource from "@/components/Resource.vue";
import Request from "@/components/Request.vue";
import Tasks from "@/components/Tasks.vue";
import Task from "@/components/Task.vue";
// lazy-loaded
const Profile = () => import("./components/Profile.vue")
const BoardAdmin = () => import("./components/BoardAdmin.vue")
const BoardModerator = () => import("./components/BoardModerator.vue")
const BoardUser = () => import("./components/BoardUser.vue")
const Communcation = () => import("./components/Communication.vue")
const Map = () => import("./components/Map.vue")
const Organization = () => import("./components/Organization.vue")
const Volunteer = () => import("./components/Volunteer.vue")
const Application = () => import("./components/Application.vue")

const routes = [
  {
    path: "/",
    name: "home",
    component: Home,
  },
  {
    path: "/home",
    component: Home,
  },
  {
    path: "/login",
    component: Login,
  },
  {
    path: "/register",
    component: Register,
  },
  {
    path: '/resource',
    component: Resource,
  },
  {
    path: '/request',
    component: Request,
  },
  {
    path: "/tasks",
    component: Tasks,
  },
  {
    path: "/task/:id",
    component: Task,
    props: true,
  },
  {
    path: "/communication",
    name: "communication",
    // lazy-loaded
    component: Communcation,
  },
  {
    path: "/map",
    name: "map",
    // lazy-loaded
    component: Map,
  },
  {
    path: "/profile",
    name: "profile",
    // lazy-loaded
    component: Profile,
  },
  {
    path: "/admin",
    name: "admin",
    // lazy-loaded
    component: BoardAdmin,
  },
  {
    path: "/mod",
    name: "moderator",
    // lazy-loaded
    component: BoardModerator,
  },
  {
    path: "/user",
    name: "user",
    // lazy-loaded
    component: BoardUser,
  },
  {
    path: '/organization',
    component: Organization,
  },
  {
    path: '/volunteer',
    component: Volunteer,
  },
  {
    path: '/application',
    component: Application,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// router.beforeEach((to, from, next) => {
//   const publicPages = ['/login', '/register', '/home'];
//   const authRequired = !publicPages.includes(to.path);
//   const loggedIn = localStorage.getItem('user');

//   // trying to access a restricted page + not logged in
//   // redirect to login page
//   if (authRequired && !loggedIn) {
//     next('/login');
//   } else {
//     next();
//   }
// });

export default router;