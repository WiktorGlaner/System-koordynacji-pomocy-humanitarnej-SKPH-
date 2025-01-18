<template>
  <div class="col-md-12">
    <div class="card card-container">
      <img
        id="profile-img"
        src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
        class="profile-img-card"
      />
      <Form @submit="handleRegister" :validation-schema="schema">
        <div v-if="!successful">
          <div class="form-group">
            <label for="username">{{ $t('login-username') }}</label>
            <Field name="username" type="text" class="form-control" />
            <ErrorMessage name="username" class="error-feedback" />
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <Field name="email" type="email" class="form-control" />
            <ErrorMessage name="email" class="error-feedback" />
          </div>
          <div class="form-group">
            <label for="password">{{ $t('login-password') }}</label>
            <Field name="password" type="password" class="form-control" />
            <ErrorMessage name="password" class="error-feedback" />
          </div>
          <div class="form-group">
            <label for="role">{{ $t('signup-role') }}</label>
            <Field name="role" as="select" class="form-control">
              <option value="" disabled>{{ $t('signup-select-role') }}</option>
              <option value="victim">{{ $t('signup-victim') }}</option>
              <option value="donor">{{ $t('signup-donor') }}</option>
              <option value="volunteer">{{ $t('signup-volunteer') }}</option>
              <option value="organization">{{ $t('signup-organization') }}</option>
              <option value="authority">{{ $t('signup-authority') }}</option>
            </Field>
            <ErrorMessage name="role" class="error-feedback" />
          </div>
          <div class="form-group">
            <button class="btn btn-primary btn-block mt-4 mx-auto d-block" :disabled="loading">
              <span
                v-show="loading"
                class="spinner-border spinner-border-sm"
              ></span>
              {{ $t('signup-button') }}
            </button>
          </div>
        </div>
      </Form>

      <div
        v-if="message"
        class="alert"
        :class="successful ? 'alert-success' : 'alert-danger'"
      >
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script>
import { Form, Field, ErrorMessage } from "vee-validate";
import * as yup from "yup";


export default {
  name: "Register",
  components: {
    Form,
    Field,
    ErrorMessage,
  },
  data() {
    const schema = yup.object().shape({
      username: yup
        .string()
        .required(this.$t('signup-usernamereq'))
        .min(3, this.$t('signup-usernamemin'))
        .max(20, this.$t('signup-usernamemax')),
      email: yup
        .string()
        .required(this.$t('signup-emailreq'))
        .email(this.$t('signup-emailinv'))
        .max(50, this.$t('signup-emailmax')),
      password: yup
        .string()
        .required(this.$t('signup-passreq'))
        .min(6, this.$t('signup-passmin'))
        .max(40, this.$t('signup-passmax')),
      role: yup.string().required(this.$t('signup-rolereq'))
    });

    return {
      successful: false,
      loading: false,
      message: "",
      schema,
    };
  },
    watch: {
    '$i18n.locale': function(newLang) {
      this.schema = yup.object().shape({
      username: yup
        .string()
        .required(this.$t('signup-usernamereq'))
        .min(3, this.$t('signup-usernamemin'))
        .max(20, this.$t('signup-usernamemax')),
      email: yup
        .string()
        .required(this.$t('signup-emailreq'))
        .email(this.$t('signup-emailinv'))
        .max(50, this.$t('signup-emailmax')),
      password: yup
        .string()
        .required(this.$t('signup-passreq'))
        .min(6, this.$t('signup-passmin'))
        .max(40, this.$t('signup-passmax')),
      role: yup.string().required(this.$t('signup-rolereq'))
    });
    }
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    },
  },
  mounted() {
    if (this.loggedIn) {
      this.$router.push("/profile");
    }
  },
  methods: {
    handleRegister(user) {
      this.message = "";
      this.successful = false;
      this.loading = true;

      this.$store.dispatch("auth/register", user).then(
        (data) => {
          this.message = data.message;
          this.successful = true;
          this.loading = false;
        },
        (error) => {
          this.message =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();
          this.successful = false;
          this.loading = false;
        }
      );
    },
  },
};
</script>

<style scoped>
label {
  display: block;
  margin-top: 10px;
}

.card-container.card {
  max-width: 350px !important;
  padding: 40px 40px;
}

.card {
  background-color: #f7f7f7;
  padding: 20px 25px 30px;
  margin: 0 auto 25px;
  margin-top: 50px;
  -moz-border-radius: 2px;
  -webkit-border-radius: 2px;
  border-radius: 2px;
  -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
}

.profile-img-card {
  width: 96px;
  height: 96px;
  margin: 0 auto 10px;
  display: block;
  -moz-border-radius: 50%;
  -webkit-border-radius: 50%;
  border-radius: 50%;
}

.error-feedback {
  color: red;
}

</style>
