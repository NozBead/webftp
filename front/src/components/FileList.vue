<script setup lang="ts">
import { RemoteFile } from '../servers/FileServers'
import { computed } from 'vue'

const props = defineProps<{
  files: Array<RemoteFile>
}>()

defineEmits<{
  pathChanged: [path: string]
}>()

const sortedFiles = computed(() =>
  [...props.files].sort((a, b) => {
    const typeDiff = b.type - a.type
    if (typeDiff == 0) {
      return a.name.localeCompare(b.name)
    }
    return typeDiff
  })
)
</script>

<template>
  <div id="list">
    <div
      @click="$emit('pathChanged', `./${file.name}/`)"
      v-for="file in sortedFiles"
      class="file-card"
    >
      <h1>{{ file.type == 0 ? 'File' : 'Directory' }}</h1>
      <h3>{{ file.name.length > 30 ? file.name.substring(0, 30) + '...' : file.name }}</h3>
    </div>
  </div>
</template>

<style scoped>
#list {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  padding: 1rem;
}

.file-card {
  width: 10rem;
  margin: 0.8rem;
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
  cursor: pointer;

  h3 {
    word-wrap: break-word;
  }
}
</style>
