<template>
  <div class="inner-link-container">
    <iframe :src="iframeSrc" class="inner-link-iframe" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const iframeSrc = computed(() => route.meta?.link as string || '')
</script>

<style scoped>
.inner-link-container {
  width: 100%;
  height: 100%;
}
.inner-link-iframe {
  width: 100%;
  height: calc(100vh - 84px);
  border: none;
}
</style>
