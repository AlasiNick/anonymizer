<template>
  <div class="language-switcher">
    <select 
      v-model="currentLocale" 
      @change="changeLanguage"
      class="language-select"
      :title="$t('language')"
    >
      <option value="en">ENG</option>
      <option value="ee">EST</option>
    </select>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

export default {
  name: 'LanguageSwitcher',
  setup() {
    const { locale } = useI18n();
    const currentLocale = ref(locale.value);

    const changeLanguage = () => {
      locale.value = currentLocale.value;
      localStorage.setItem('language', currentLocale.value);
    };

    return {
      currentLocale,
      changeLanguage
    };
  }
};
</script>

<style lang="scss" scoped>
.language-switcher {
  display: inline-block;
  border-radius: 18px;
  background: #FFF;
  box-shadow: 0px 4px 15px 0px rgba(0, 0, 0, 0.25);
}

.language-select {
  background: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #3865F2;
  font-weight: 700;
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: rgba(255, 255, 255, 0.5);
  }

  &:focus {
    outline: none;
    border-color: white;
  }

  option {
    background: white;
    color: #3865F2;
    font-family: Montserrat;
    font-size: 14px;
    font-weight: 700;
    border-radius: 18px;
  }
}
</style> 