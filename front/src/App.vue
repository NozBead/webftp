<script setup lang="ts">
import ServerList from './components/ServerList.vue'

// function sendFile(e: Event) {
//   for (const file of e.currentTarget.files) {
//     const request = new XMLHttpRequest()
//     request.open('POST', 'http://localhost:8082', true)
//     request.upload.onprogress = (e) => {
//       console.log(e)
//     }
//     request.send(file)
//   }
// }
</script>

<template>
  <ServerList />
</template>

<style scoped></style>
