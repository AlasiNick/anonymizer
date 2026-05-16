<template>
  <div class="language-switcher">
    <select
      v-model="currentLocale"
      @change="changeLanguage"
      class="language-select"
      :title="$t('language')"
      :aria-label="$t('language')"
    >
      <option value="en">ENG</option>
      <option value="ee">EST</option>
    </select>
  </div>
</template>

<script>
import { ref } from "vue";
import { useI18n } from "vue-i18n";

export default {
  name: "LanguageSwitcher",
  setup() {
    const { locale } = useI18n();
    const currentLocale = ref(locale.value);

    const changeLanguage = () => {
      locale.value = currentLocale.value;
      localStorage.setItem("language", currentLocale.value);
    };

    return {
      currentLocale,
      changeLanguage,
    };
  },
};
</script>

<style lang="scss" scoped>
.language-switcher {
  display: inline-block;
  border-radius: 6px;
  background: #ffffff;
  border: 1px solid #e2e6df;
  box-shadow: 0 1px 4px rgba(13, 34, 30, 0.08);
}

.language-select {
  background: transparent;
  border: none;
  color: #0d7b6b;
  font-family: "DM Mono", monospace;
  font-weight: 500;
  font-size: 12px;
  letter-spacing: 0.06em;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  appearance: none;
  -webkit-appearance: none;
  padding-right: 28px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='6' fill='none'%3E%3Cpath d='M1 1l4 4 4-4' stroke='%230D7B6B' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;

  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px rgba(13, 123, 107, 0.25);
  }

  option {
    background: white;
    color: #1a2220;
    font-family: "DM Mono", monospace;
    font-size: 12px;
  }
}
</style>
