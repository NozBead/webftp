<script setup lang="ts">
import DynLabelInput from './DynLabelInput.vue'
import { Server, FileServers } from '../servers/FileServers'
import { ref, useTemplateRef } from 'vue'

defineEmits<{
  serverSelected: [server: string]
}>()

const api = new FileServers('http://localhost:8080')

const servers = ref<Map<string, Server>>()
const serverDialog = useTemplateRef('serverDialog')
const serverForm = useTemplateRef('serverForm')

api.listServers().then((result) => {
  servers.value = result
})

function handleClose() {
  if (serverDialog.value?.returnValue == 'confirm') {
    const inputs = serverForm.value?.elements
    api.updateServer(
      (inputs?.namedItem('name') as HTMLInputElement).value,
      formToServer(inputs as HTMLFormControlsCollection)
    )
  }
}

function formToServer(inputs: HTMLFormControlsCollection): Server {
  const server: { [k: string]: any } = new Server()
  for (const key in server) {
    server[key as keyof Server] = (inputs.namedItem(key) as HTMLInputElement).value
  }
  return server as Server
}

function serverToForm(inputs: HTMLFormControlsCollection, server: Server, name?: string) {
  for (const key in server) {
    const element = inputs.namedItem(key)
    if (element != null) {
      ;(element as HTMLInputElement).value = server[key as keyof Server].toString()
    }
  }

  const nameInput = inputs.namedItem('name') as HTMLInputElement
  if (name != undefined) {
    nameInput.value = name
    nameInput.disabled = true
  } else {
    nameInput.disabled = false
  }
  return server as Server
}

function openServerDialog(server: Server = new Server(), name?: string) {
  const inputs = serverForm.value?.elements as HTMLFormControlsCollection
  serverToForm(inputs, server, name)
  serverDialog.value?.showModal()
}
</script>

<template>
  <dialog
    ref="serverDialog"
    @close="handleClose"
    @click="
      (e) => {
        if (e.target === serverDialog) {
          serverDialog?.close()
        }
      }
    "
  >
    <form ref="serverForm" method="dialog">
      <div>
        <h2>Server</h2>
        <DynLabelInput type="text" name="name" label="Name" :required="true" />
      </div>

      <div>
        <h2>Connection</h2>
        <div class="form-line">
          <DynLabelInput type="text" name="host" label="Host" :required="true" />
          <DynLabelInput type="number" name="port" label="Port" :required="true" />
        </div>
        <DynLabelInput type="text" name="root" label="Root Path" :required="false" />
      </div>

      <div>
        <h2>Authentication</h2>
        <div class="form-line">
          <DynLabelInput type="text" name="login" label="Username" :required="true" />
          <DynLabelInput type="password" name="password" label="Password" :required="false" />
        </div>
      </div>

      <div id="buttons">
        <button type="submit" value="confirm">Confirm</button>
        <button type="submit" value="cancel" formnovalidate>Cancel</button>
      </div>
    </form>
  </dialog>
  <div id="list">
    <ul>
      <li
        @click="$emit('serverSelected', name)"
        @dblclick="openServerDialog(server, name)"
        v-for="[name, server] in servers"
      >
        {{ name }}
      </li>
      <li id="add" @click="openServerDialog()">
        <b><span>+</span> Add server...</b>
      </li>
    </ul>
  </div>
</template>

<style scoped>
dialog {
  border: none;
  border-radius: 20px;
  background-color: var(--primary-color);
  padding: 2rem 5rem;
  form {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    justify-content: center;

    .form-line {
      display: flex;
    }

    h2 {
      margin-bottom: 0;
    }

    #buttons {
      display: flex;
      flex-direction: row-reverse;
      align-items: center;
      justify-content: end;
      padding: 1.5rem 0 1.5rem 0;

      button {
        margin: 1rem;
        border-radius: 10px;
        padding: 0.6rem;
      }
    }
  }
}

#list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem;
  width: 15rem;
  font-size: 1.2rem;
  flex-shrink: 0;
  background-color: var(--primary-color);

  ul {
    padding: 0;
    list-style: none;
    #add {
      cursor: pointer;
      span {
        font-size: 1.5rem;
      }
    }
  }
}
</style>
