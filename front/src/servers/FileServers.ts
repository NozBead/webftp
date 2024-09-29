export class Server {
  host: string = ''
  port: number = 21
  root: string = ''
  login: string = ''
  password: string = ''
}

interface RemoteFile {
  name: string
  host: string
  port: number
  root: string | undefined
  username: string
  password: string
}

export class FileServers {
  baseUrl: string

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl
  }

  async updateServer(name: string, server: Server) {
    await fetch(`${this.baseUrl}/server/${name}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(server)
    })
  }

  async listServers(): Promise<Map<string, Server>> {
    const response = await fetch(`${this.baseUrl}/server`, {
      headers: {
        Accept: 'application/json'
      }
    })
    return new Map(Object.entries(await response.json()))
  }

  listFiles(server: string, path: string) {
    fetch(`${this.baseUrl}/server/${server}/${path}`, {
      headers: {
        Accept: 'application/json'
      }
    })
  }
}
