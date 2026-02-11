import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = '/api'

export interface Api {
  id: number
  projectId: number
  folderId?: number
  name: string
  path: string
  method: string
  description?: string
  status: number
  createdAt: string
  updatedAt: string
}

export interface ApiFolder {
  id: number
  projectId: number
  parentId?: number
  name: string
  description?: string
  sort: number
  createdAt: string
  updatedAt: string
  children?: ApiFolder[]
  apis?: Api[]
}

export interface ApiDetail extends Api {
  params?: ApiParam[]
  headers?: ApiHeader[]
  body?: string
  bodyType?: string
  response?: string
  usage?: string
}

export interface ApiParam {
  id?: number
  name: string
  value?: string
  type: string
  description?: string
  required?: boolean
  enabled: boolean
}

export interface ApiHeader {
  id?: number
  name: string
  value: string
  description?: string
  required?: boolean
}

export const useApiStore = defineStore('api', () => {
  // State
  const apis = ref<Api[]>([])
  const folders = ref<ApiFolder[]>([])
  const currentApi = ref<ApiDetail | null>(null)
  const loading = ref(false)

  // Getters
  const getApis = computed(() => apis.value)
  const getFolders = computed(() => folders.value)
  const getCurrentApi = computed(() => currentApi.value)
  const getApisByFolder = computed(() => (folderId?: number) => {
    if (folderId) {
      return apis.value.filter(api => api.folderId === folderId)
    }
    return apis.value.filter(api => !api.folderId)
  })

  // Actions
  const fetchApisByProject = async (projectId: number) => {
    loading.value = true
    try {
      const response = await axios.get(`${API_BASE_URL}/apis/project/${projectId}`)
      apis.value = response.data
      return response.data
    } catch (error) {
      console.error('获取API列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchFoldersByProject = async (projectId: number) => {
    loading.value = true
    try {
      const response = await axios.get(`${API_BASE_URL}/api-folders/project/${projectId}`)
      folders.value = response.data
      return response.data
    } catch (error) {
      console.error('获取文件夹列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchApiById = async (id: number) => {
    loading.value = true
    try {
      const response = await axios.get(`${API_BASE_URL}/apis/${id}`)
      currentApi.value = response.data
      return response.data
    } catch (error) {
      console.error('获取API详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createApi = async (api: Partial<Api>) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/apis`, api)
      apis.value.push(response.data)
      return response.data
    } catch (error) {
      console.error('创建API失败:', error)
      throw error
    }
  }

  const updateApi = async (id: number, api: Partial<Api>) => {
    try {
      const response = await axios.put(`${API_BASE_URL}/apis/${id}`, api)
      const index = apis.value.findIndex(a => a.id === id)
      if (index !== -1) {
        apis.value[index] = { ...apis.value[index], ...response.data }
      }
      if (currentApi.value?.id === id) {
        currentApi.value = { ...currentApi.value, ...response.data }
      }
      return response.data
    } catch (error) {
      console.error('更新API失败:', error)
      throw error
    }
  }

  const deleteApi = async (id: number) => {
    try {
      await axios.delete(`${API_BASE_URL}/apis/${id}`)
      apis.value = apis.value.filter(a => a.id !== id)
      if (currentApi.value?.id === id) {
        currentApi.value = null
      }
    } catch (error) {
      console.error('删除API失败:', error)
      throw error
    }
  }

  const createFolder = async (folder: Partial<ApiFolder>) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/api-folders`, folder)
      folders.value.push(response.data)
      return response.data
    } catch (error) {
      console.error('创建文件夹失败:', error)
      throw error
    }
  }

  const updateFolder = async (id: number, folder: Partial<ApiFolder>) => {
    try {
      const response = await axios.put(`${API_BASE_URL}/api-folders/${id}`, folder)
      const index = folders.value.findIndex(f => f.id === id)
      if (index !== -1) {
        folders.value[index] = { ...folders.value[index], ...response.data }
      }
      return response.data
    } catch (error) {
      console.error('更新文件夹失败:', error)
      throw error
    }
  }

  const deleteFolder = async (id: number) => {
    try {
      await axios.delete(`${API_BASE_URL}/api-folders/${id}`)
      folders.value = folders.value.filter(f => f.id !== id)
    } catch (error) {
      console.error('删除文件夹失败:', error)
      throw error
    }
  }

  const searchApis = async (projectId: number, keyword: string) => {
    loading.value = true
    try {
      const response = await axios.get(`${API_BASE_URL}/apis/project/${projectId}/search?keyword=${keyword}`)
      apis.value = response.data
      return response.data
    } catch (error) {
      console.error('搜索API失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    apis,
    folders,
    currentApi,
    loading,
    getApis,
    getFolders,
    getCurrentApi,
    getApisByFolder,
    fetchApisByProject,
    fetchFoldersByProject,
    fetchApiById,
    createApi,
    updateApi,
    deleteApi,
    createFolder,
    updateFolder,
    deleteFolder,
    searchApis
  }
})
