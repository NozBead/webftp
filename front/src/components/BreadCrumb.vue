<script setup lang="ts">
const props = defineProps<{
  path: Array<string>
  server: string
}>()

defineEmits<{
  pathChanged: [path: string]
}>()
</script>

<template>
  <div id="bread">
    <h2 class="dir" @click="$emit('pathChanged', `/${server}/`)">{{ server }}</h2>
    <template v-for="(dir, index) in path">
      <h2>></h2>
      <h2
        class="dir"
        @click="$emit('pathChanged', `/${server}/${path.slice(0, index + 1).join('/')}/`)"
      >
        {{ dir }}
      </h2>
    </template>
  </div>
</template>

<style scoped>
#bread {
  display: flex;
  flex-direction: row;
  * {
    margin: 1rem;
  }

  .dir {
    cursor: pointer;
  }
}
</style>
