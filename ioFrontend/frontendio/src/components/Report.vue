
<!--<template>-->
<!--  <div>-->
<!--    <h1>Generowanie Raportów</h1>-->

<!--    &lt;!&ndash; Sprawdzamy, czy użytkownik ma odpowiednią rolę &ndash;&gt;-->
<!--    <div v-if="allowedRole">-->
<!--      <div>-->
<!--        <label for="report-type">Wybierz typ raportu:</label>-->
<!--        <select v-model="selectedOption" id="report-type">-->
<!--          <option v-for="option in options" :key="option.value" :value="option.value">-->
<!--            {{ option.label }}-->
<!--          </option>-->
<!--        </select>-->
<!--      </div>-->

<!--      <button @click="acceptOption">Zatwierdź raport</button>-->

<!--      &lt;!&ndash; Możemy tutaj dodać iframe lub inną metodę do wyświetlania wygenerowanego raportu &ndash;&gt;-->

<!--    </div>-->
<!--    <div v-else>-->
<!--      <p>Nie masz uprawnień do generowania raportów.</p>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->


<!--<script>-->
<!--import ReportService from "@/services/report.service.js";-->

<!--export default {-->
<!--  data() {-->
<!--    return {-->
<!--      options: [-->
<!--        { value: 'tasks', label: 'Raport dotyczący zadań' },-->
<!--        { value: 'applications', label: 'Raport dotyczący wniosków o pomoc' },-->
<!--        { value: 'resources', label: 'Raport dotyczący zasobów' },-->
<!--      ],-->
<!--      selectedOption: null,-->
<!--      reportUrl: null,  // Zmienna do przechowywania linku do wygenerowanego raportu-->
<!--      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_DONOR", "ROLE_AUTHORITY"],  // Role, które mają dostęp do raportów-->
<!--    };-->
<!--  },-->
<!--  computed: {-->
<!--    currentUser() {-->
<!--      return this.$store.state.auth.user;  // Pobieramy aktualnego użytkownika z Vuex-->
<!--    },-->
<!--    allowedRole() {-->
<!--      // Sprawdzamy, czy użytkownik ma odpowiednią rolę-->
<!--      return this.currentUser && this.allowedRoles.some(role => this.currentUser.roles.includes(role));-->
<!--    },-->
<!--  },-->
<!--  methods: {-->
<!--    // Funkcja, która jest wywoływana po kliknięciu przycisku "Zatwierdź raport"-->
<!--    async acceptOption() {-->
<!--      if (!this.selectedOption) {-->
<!--        alert('Proszę wybrać typ raportu.');-->
<!--        return;-->
<!--      }-->

<!--      if (!this.allowedRole) {-->
<!--        alert('Nie masz uprawnień do generowania raportu.');-->
<!--        return;-->
<!--      }-->

<!--      try {-->
<!--        // Wywołanie metody do generowania raportu z wybranym typem-->
<!--        await this.generateReport(this.selectedOption);-->
<!--      } catch (error) {-->
<!--        console.error('Błąd podczas generowania raportu:', error);-->
<!--        alert('Nie udało się wygenerować raportu.');-->
<!--      }-->
<!--    },-->

<!--    // Funkcja do generowania raportu-->
<!--    async generateReport(reportType) {-->
<!--      try {-->
<!--        await ReportService.generateReport(reportType);  // Przesyłanie typu raportu-->
<!--      } catch (error) {-->
<!--        console.error('Błąd podczas generowania raportu:', error);-->
<!--        alert('Błąd generowania raportu.');-->
<!--      }-->
<!--    },-->
<!--  },-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--label {-->
<!--  margin-right: 10px;-->
<!--}-->
<!--button {-->
<!--  margin-left: 10px;-->
<!--}-->
<!--</style>-->
<!--<template>-->
<!--  <div class="report-container">-->
<!--    <h1>Generowanie Raportów</h1>-->

<!--    &lt;!&ndash; Sprawdzamy, czy użytkownik ma odpowiednią rolę &ndash;&gt;-->
<!--    <div v-if="allowedRole">-->
<!--      <div class="report-selection">-->
<!--        <label for="report-type">Wybierz typ raportu:</label>-->
<!--        <select v-model="selectedOption" id="report-type">-->
<!--          <option v-for="option in allowedRoles" :key="option.value" :value="option.value">-->
<!--            {{ option.label }}-->
<!--          </option>-->
<!--        </select>-->
<!--        <button @click="downloadReport">-->
<!--          <i class="fa fa-download"></i> Pobierz raport PDF-->
<!--        </button>-->
<!--        <button @click="showReportPreview">-->
<!--          <i class="fa fa-eye"></i> Podgląd raportu-->
<!--        </button>-->
<!--      </div>-->

<!--      &lt;!&ndash; Podgląd raportu w iframe &ndash;&gt;-->
<!--      <div class="report-preview" v-if="reportUrl">-->
<!--        <h2>Podgląd raportu:</h2>-->
<!--        <iframe :src="reportUrl" frameborder="0"></iframe>-->
<!--      </div>-->
<!--    </div>-->
<!--    <div v-else>-->
<!--      <p>Nie masz uprawnień do generowania raportów.</p>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import ReportService from "@/services/report.service.js";-->

<!--export default {-->
<!--  data() {-->
<!--    return {-->
<!--      options: [-->
<!--        {userRole: ["ROLE_DONOR", "ROLE_AUTHORITY"] ,  value: "tasks", label: "Raport dotyczący zadań"},-->
<!--        {userRole: ["ROLE_DONOR", "ROLE_AUTHORITY"], value: "applications", label: "Raport dotyczący wniosków o pomoc" },-->
<!--        {userRole: ["ROLE_DONOR", "ROLE_AUTHORITY"],  value: "resources", label: "Raport dotyczący zasobów" },-->
<!--      ],-->
<!--      selectedOption: null,-->
<!--      reportUrl: null, // URL dla iframe-->
<!--      userRoles: ["ROLE_ORGANIZATION", "ROLE_DONOR", "ROLE_AUTHORITY"], // Role z dostępem do raportów-->

<!--    };-->
<!--  },-->

<!--  computed: {-->
<!--    filteredOptions() {-->
<!--      // Filtruj opcje na podstawie roli użytkownika-->
<!--      console.log(this.currentUser)-->
<!--      return this.options.filter(option => option.userRole.includes(this.currentUser.roles[0]));-->
<!--      //return this.currentUser && this.userRole.some((role) => this.currentUser.roles.includes(role));-->
<!--    },-->

<!--    currentUser() {-->
<!--      return this.$store.state.auth.user; // Pobieramy aktualnego użytkownika z Vuex-->
<!--    },-->
<!--    allowedRole() {-->
<!--      // Sprawdzamy, czy użytkownik ma odpowiednią rolę-->
<!--      return this.currentUser && this.userRoles.some((role) => this.currentUser.roles.includes(role));-->

<!--    },-->
<!--  },-->
<!--  methods: {-->
<!--    // Pobieranie raportu PDF-->
<!--    async downloadReport() {-->
<!--      if (!this.selectedOption) {-->
<!--        alert("Proszę wybrać typ raportu.");-->
<!--        return;-->
<!--      }-->
<!--      try {-->
<!--        await ReportService.generateReport(this.selectedOption); // Pobieranie raportu-->
<!--      } catch (error) {-->
<!--        console.error("Błąd podczas pobierania raportu:", error);-->
<!--        alert("Nie udało się pobrać raportu.");-->
<!--      }-->
<!--    },-->

<!--    // Podgląd raportu w iframe-->
<!--    async showReportPreview() {-->
<!--      if (!this.selectedOption) {-->
<!--        alert("Proszę wybrać typ raportu.");-->
<!--        return;-->
<!--      }-->
<!--      try {-->
<!--        const url = await ReportService.previewReport(this.selectedOption);-->
<!--        this.reportUrl = url; // Przechowywanie URL raportu-->
<!--      } catch (error) {-->
<!--        console.error("Błąd podczas podglądu raportu:", error);-->
<!--        alert("Nie udało się wyświetlić raportu.");-->
<!--      }-->
<!--    },-->
<!--  },-->
<!--};-->
<!--</script>-->
<!--<template>-->
<!--  <div class="report-container">-->
<!--    <h1>Generowanie Raportów</h1>-->

<!--    &lt;!&ndash; Sprawdzamy, czy użytkownik ma odpowiednią rolę &ndash;&gt;-->
<!--    <div v-if="allowedRole">-->
<!--      <div class="report-selection">-->
<!--        <label for="report-type">Wybierz typ raportu:</label>-->
<!--        <select v-model="selectedOption" id="report-type">-->
<!--          <option v-for="option in options" :key="option.value" :value="option.value">-->
<!--            {{ option.label }}-->
<!--          </option>-->
<!--        </select>-->
<!--        <button @click="downloadReport">-->
<!--          <i class="fa fa-download"></i> Pobierz raport PDF-->
<!--        </button>-->
<!--        <button @click="showReportPreview">-->
<!--          <i class="fa fa-eye"></i> Podgląd raportu-->
<!--        </button>-->
<!--      </div>-->

<!--      &lt;!&ndash; Podgląd raportu w iframe &ndash;&gt;-->
<!--      <div class="report-preview" v-if="reportUrl">-->
<!--        <h2>Podgląd raportu:</h2>-->
<!--        <iframe :src="reportUrl" frameborder="0"></iframe>-->
<!--      </div>-->
<!--    </div>-->
<!--    <div v-else>-->
<!--      <p>Nie masz uprawnień do generowania raportów.</p>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import ReportService from "@/services/report.service.js";-->

<!--export default {-->
<!--  data() {-->
<!--    return {-->
<!--      options: [-->
<!--        { value: "tasks", label: "Raport dotyczący zadań" },-->
<!--        { value: "applications", label: "Raport dotyczący wniosków o pomoc" },-->
<!--        { value: "resources", label: "Raport dotyczący zasobów" },-->
<!--      ],-->
<!--      selectedOption: null,-->
<!--      reportUrl: null, // URL dla iframe-->
<!--      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_DONOR", "ROLE_AUTHORITY"], // Role z dostępem do raportów-->
<!--    };-->
<!--  },-->
<!--  computed: {-->
<!--    currentUser() {-->
<!--      return this.$store.state.auth.user; // Pobieramy aktualnego użytkownika z Vuex-->
<!--    },-->
<!--    allowedRole() {-->
<!--      // Sprawdzamy, czy użytkownik ma odpowiednią rolę-->
<!--      return this.currentUser && this.allowedRoles.some((role) => this.currentUser.roles.includes(role));-->
<!--    },-->
<!--  },-->
<!--  methods: {-->
<!--    // Pobieranie raportu PDF-->
<!--    async downloadReport() {-->
<!--      if (!this.selectedOption) {-->
<!--        alert("Proszę wybrać typ raportu.");-->
<!--        return;-->
<!--      }-->
<!--      try {-->
<!--        await ReportService.generateReport(this.selectedOption); // Pobieranie raportu-->
<!--      } catch (error) {-->
<!--        console.error("Błąd podczas pobierania raportu:", error);-->
<!--        alert("Nie udało się pobrać raportu.");-->
<!--      }-->
<!--    },-->

<!--    // Podgląd raportu w iframe-->
<!--    async showReportPreview() {-->
<!--      if (!this.selectedOption) {-->
<!--        alert("Proszę wybrać typ raportu.");-->
<!--        return;-->
<!--      }-->
<!--      try {-->
<!--        const url = await ReportService.previewReport(this.selectedOption);-->
<!--        this.reportUrl = url; // Przechowywanie URL raportu-->
<!--      } catch (error) {-->
<!--        console.error("Błąd podczas podglądu raportu:", error);-->
<!--        alert("Nie udało się wyświetlić raportu.");-->
<!--      }-->
<!--    },-->
<!--  },-->
<!--};-->
<!--</script>-->

<template>
  <div class="report-container">
    <h1> {{ $t('report-title') }}</h1>

    <!-- Sprawdzamy, czy użytkownik ma odpowiednią rolę -->
    <div v-if="allowedRole">
      <div class="report-selection">
        <label for="report-type">{{ $t('report-type') }}</label>
        <select v-model="selectedOption" id="report-type">
          <option v-for="option in options" :key="option.value" :value="option.value">
            {{ $t(option.label) }}
          </option>
        </select>
        <button @click="downloadReport">
          <i class="fa fa-download"></i> {{ $t('report-download') }}
        </button>
        <button @click="showReportPreview">
          <i class="fa fa-eye"></i> {{ $t('report-view') }}
        </button>
      </div>

      <!-- Podgląd raportu w iframe -->
      <div class="report-preview" v-if="reportUrl">
        <h2>{{ $t('view-report') }}</h2>
        <iframe :src="reportUrl" frameborder="0"></iframe>
      </div>
    </div>
    <div v-else>
      <p>Nie masz uprawnień do generowania raportów.</p>
    </div>
  </div>
</template>

<script>
import ReportService from "@/services/report.service.js";

export default {
  data() {
    return {
      options: [],
      selectedOption: null,
      reportUrl: null,
      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_DONOR", "ROLE_AUTHORITY"],
    };
  },

  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    allowedRole() {
      return (
          this.currentUser &&
          this.allowedRoles.some((role) => this.currentUser.roles.includes(role))
      );
    },
  },

  watch: {
    "$i18n.locale": {
      handler() {
        this.updateOptions();
      },
      immediate: true,
    },
  },

  methods: {
    updateOptions() {
      this.options = [
        { value: "tasks", label: this.$t("task-report") },
        { value: "applications", label: this.$t("request-report") },
        { value: "resources", label: this.$t("resource-report") },
        { value: "donations", label: this.$t("donation-report") },
        { value: "taxes", label: this.$t("tax-report") },
      ];
    },

    // Pobieranie raportu PDF
    async downloadReport() {
      if (!this.selectedOption) {
        alert(this.$t("please-select-report-type"));
        return;
      }
      try {
        await ReportService.generateReport(
            this.selectedOption,
            this.$store.state.auth.user.id
        );
        alert(this.$t("report-downloaded-successfully"));
      } catch (error) {
        console.error("Błąd podczas pobierania raportu:", error);
        alert(this.$t("report-download-failed"));
      }
    },

    // Podgląd raportu w iframe
    async showReportPreview() {
      if (!this.selectedOption) {
        alert(this.$t("please-select-report-type")); // Użycie tłumaczenia
        return;
      }
      try {
        const url = await ReportService.previewReport(
            this.selectedOption,
            this.$store.state.auth.user.id
        );
        this.reportUrl = url; // Przechowywanie URL raportu
      } catch (error) {
        console.error("Błąd podczas podglądu raportu:", error);
        alert(this.$t("report-preview-failed"));
      }
    },
  },

  created() {
    // Inicjalizacja opcji raportów przy montowaniu komponentu
    this.updateOptions();
  },
};
</script>

<style scoped>

.report-container {
  font-family: 'Roboto', sans-serif;
  margin: 40px auto;
  max-width: 900px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}


h1 {
  text-align: center;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 20px;
}


.report-selection {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

label {
  font-weight: 600;
  color: #34495e;
  font-size: 16px;
}


select {
  padding: 12px 16px;
  border: 1px solid #dcdcdc;
  border-radius: 6px;
  font-size: 16px;
  color: #34495e;
  background-color: #f9f9f9;
  transition: border-color 0.3s ease;
}

select:focus {
  border-color: #3498db;
  outline: none;
}

button {
  padding: 12px 20px;
  background-color: #3498db;
  color: white;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

button:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #2980b9;
  transform: scale(1.02);
}

button:active:not(:disabled) {
  background-color: #2471a3;
  transform: scale(1);
}


.report-preview {
  margin-top: 20px;
  text-align: center;
}

iframe {
  width: 100%;
  height: 600px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
</style>
