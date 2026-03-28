import { createI18n } from 'vue-i18n';
import en from '../locales/en.json';
import ee from '../locales/ee.json';

const i18n = createI18n({
  legacy: false, // Set to false to use Composition API
  locale: 'en', // Default language
  fallbackLocale: 'en', // Fallback if translation is missing
  messages: {
    en,
    ee
  },
  globalInjection: true // Enables global access to i18n instance
});

export default i18n; 