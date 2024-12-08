<template>
  <div class="container">
    <div id = "map">

    </div>
  </div>
</template>

<script>
export default {
  name: 'Map',
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
    var map = L.map('map').setView([51.75, 19.45], 14);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);
  }
};
</script>

<style>
#map { height: 80vh; width: 50vw; margin: 5vh; }
</style>