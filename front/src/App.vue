<script setup lang="ts">
import { ref } from 'vue'

function sendFile(e: Event) {
  for (const file of e.currentTarget.files) {
    const request = new XMLHttpRequest()
    request.open('POST', 'http://localhost:8082', true)
    request.upload.onprogress = (e) => {
      console.log(e)
    }
    request.send(file)
  }
}
</script>

<template>
  <input type="file" multiple @change="sendFile" />
</template>

<style scoped></style>
