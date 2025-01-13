import { createApp } from "vue";
import { createI18n } from 'vue-i18n';
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { FontAwesomeIcon } from './plugins/font-awesome'
import en from './locales/en.json';
import pl from './locales/pl.json';

const i18n = createI18n({
    locale: 'en', // Domyślny język
    fallbackLocale: 'en', // Język zapasowy
    messages: {
        en,
        pl,
    },
});

createApp(App)
  .use(router)
  .use(store)
    .use(i18n)
  .component("font-awesome-icon", FontAwesomeIcon)
  .mount("#app");
