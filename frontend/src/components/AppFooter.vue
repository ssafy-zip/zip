<template>
  <footer class="app-footer">
    <section class="footer__content">
      <p class="footer__copyright">&copy; All rights reserved. Zip</p>
    </section>
  </footer>
</template>

<script>
export default {
  name: 'AppFooter',
}
</script>

<style scoped>
.app-footer {
  display: flex;
  flex-direction: column;
  background-color: #111827;
  color: #d1d5db;
  padding: 12px 0;
  font-size: 14px;
}

.footer__content {
  min-width: 400px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

.footer__copyright {
  text-align: center;
  padding: 12px 0;
  margin: 0;
}
</style>
