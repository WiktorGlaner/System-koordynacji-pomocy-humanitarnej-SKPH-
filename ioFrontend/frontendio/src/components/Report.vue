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
      <p>{{ $t('report-denied') }}</p>
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
      this.options = [];

      // Dodajemy opcje widoczne tylko dla `ROLE_ORGANIZATION` i `ROLE_AUTHORITY`
      if (
          this.currentUser &&
          (this.currentUser.roles.includes("ROLE_ORGANIZATION") ||
              this.currentUser.roles.includes("ROLE_AUTHORITY"))
      ) {
        this.options.push(
            { value: "tasks", label: this.$t("task-report") },
            { value: "applications", label: this.$t("request-report") },
            { value: "resources", label: this.$t("resource-report") }
        );
      }

      // Dodajemy opcje widoczne tylko dla `ROLE_DONOR`
      if (this.currentUser && this.currentUser.roles.includes("ROLE_DONOR")) {
        this.options.push(
            { value: "donations", label: this.$t("donation-report") },
            { value: "taxes", label: this.$t("tax-report") }
        );
      }
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
