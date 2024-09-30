<script setup lang="ts">
import ServerList from './components/ServerList.vue'
import FileList from './components/FileList.vue'
import BreadCrumb from './components/BreadCrumb.vue'
import { RemoteFile, FileServers } from './servers/FileServers'
import { ref, watchEffect, computed } from 'vue'

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

const api: FileServers = new FileServers('http://localhost:8080')

const path = ref<Array<string>>(window.location.pathname.split('/'))
const filePath = computed(() => path.value.slice(2))
const currentServer = computed(() => path.value[1])
const files = ref<Array<RemoteFile>>(new Array())

watchEffect(() => {
  if (currentServer.value != undefined) {
    api
      .listFiles(currentServer.value, filePath.value.join('/'))
      .then((response: Array<RemoteFile>) => (files.value = response))
  }
})

function updatePath() {
  path.value = window.location.pathname.split('/')
}

function selectServer(name: string) {
  history.pushState({}, '', encodeURI(`/${name}/`))
  updatePath()
}

function changePath(path: string) {
  history.pushState({}, '', encodeURI(path))
  updatePath()
}

addEventListener('popstate', updatePath)
</script>

<template>
  <main>
    <ServerList @serverSelected="selectServer" />
    <div id="content">
      <BreadCrumb @pathChanged="changePath" :path="filePath" :server="currentServer" />
      <FileList @pathChanged="changePath" v-if="currentServer" :files="files" />
    </div>
  </main>
</template>

<style scoped>
main {
  display: flex;
  flex-direction: row;
  height: 100dvh;
}

#content {
  overflow: scroll;
}
</style>
