import { createWebHistory, createRouter } from "vue-router";
import Home from "./components/Home.vue";
import Login from "./components/Login.vue";
import Register from "./components/Register.vue";
import Resource from "@/components/Resource.vue";
import Tasks from "@/components/Tasks.vue";
import TaskInfo from "@/components/TaskInfo.vue";
import TaskEdit from "./components/TaskEdit.vue";
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
const VolunteerInfo = () => import("./components/VolunteerInfo.vue")
const ApplicationInfo = () => import("./components/ApplicationInfo.vue")

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
  path: "/tasks",
  component: Tasks,
  },
  {
  path: "/tasks/info/:id",
  component: TaskInfo,
  props: true,
  },
  {
  path: "/tasks/edit/:id",
  component: TaskEdit,
  props: true,
  },
  {
    path: "/tasks/rate/:id",
    component: TaskEdit,
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
    path: "/volunteer/info/:id",
    component: VolunteerInfo,
    props: true,
  },
  {
    path: '/application',
    component: Application,
  },
  {
    path: "/application/info/:id",
    component: ApplicationInfo,
    props: true,
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