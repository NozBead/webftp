<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  files: Array<RemoteFile>
}>()
</script>

<template>
  <div id="list">
    <div @click="" v-for="file in files" class="file-card"></div>
  </div>
</template>

<style scoped>
#list {
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
}

.file-card {
  display: flex;
}
</style>
