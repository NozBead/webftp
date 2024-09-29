<script setup lang="ts">
const props = defineProps<{
  type: string
  name: string
  label: string
  required: boolean
}>()
</script>

<template>
  <div class="input-container">
    <input :type="type" :name="name" placeholder="" :required="required" />
    <label :for="name">{{ label }}</label>
  </div>
</template>

<style scoped>
.input-container {
  position: relative;
  margin: 1rem 0.2rem 0 0.2rem;

  label {
    position: absolute;
    color: white;
    border-radius: 5px;
    padding: 0 0.5rem 0 0.5rem;
    z-index: 3;
    top: -0.8rem;
    left: 10px;
    transition: 0.2s ease;
    background-color: var(--primary-color);
  }

  input {
    position: relative;
    z-index: 2;
    background-color: unset;
    border-radius: 10px;
    border-style: solid;
    padding-left: 1rem;
    height: 2.5rem;
    box-sizing: border-box;
    width: 100%;
  }

  input:placeholder-shown + label {
    transform: translateY(1.2rem);
    background-color: unset;
    color: lightgray;
    z-index: 1;
  }

  input:focus + label {
    transform: none;
    background-color: var(--primary-color);
    color: white;
    z-index: 3;
  }
}
</style>
