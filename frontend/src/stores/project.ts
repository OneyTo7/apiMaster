import { defineStore } from 'pinia'
import axios from 'axios'
import { useUserStore } from './user'

export interface Project {
  id: number
  name: string
  description: string
  teamId: number
  creatorId: number
  createdAt: string
  updatedAt: string
  status: number
  members?: ProjectMember[]
  team?: {
    id: number
    name: string
  }
}

export interface ProjectMember {
  id: number
  projectId: number
  userId: number
  role: number
  joinedAt: string
  user?: {
    id: number
    username: string
    nickname: string
    email: string
  }
}

export const useProjectStore = defineStore('project', {
  state: () => ({
    projects: [] as Project[],
    currentProject: null as Project | null,
    loading: false,
    error: null as string | null
  }),

  getters: {
    getProjects: (state) => state.projects,
    getCurrentProject: (state) => state.currentProject
  },

  actions: {
    async fetchProjects() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/projects')
        this.projects = response.data
      } catch (error: any) {
        this.error = error.message || '获取项目列表失败'
      } finally {
        this.loading = false
      }
    },

    async fetchProject(id: number) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/projects/${id}`)
        this.currentProject = response.data
      } catch (error: any) {
        this.error = error.message || '获取项目详情失败'
      } finally {
        this.loading = false
      }
    },

    setCurrentProject(project: Project | null) {
      this.currentProject = project
    },

    async createProject(projectData: {
      name: string
      description: string
      teamId: number
    }) {
      this.loading = true
      this.error = null
      try {
        const userStore = useUserStore()
        const currentUser = userStore.getUser
        if (!currentUser) {
          throw new Error('用户未登录')
        }
        const projectWithCreator = {
          ...projectData,
          creatorId: currentUser.id,
          status: 1
        }
        const response = await axios.post('/api/projects', projectWithCreator)
        this.projects.push(response.data)
        return response.data
      } catch (error: any) {
        this.error = error.message || '创建项目失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateProject(id: number, projectData: {
      name: string
      description: string
      status?: number
    }) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.put(`/api/projects/${id}`, projectData)
        const index = this.projects.findIndex(p => p.id === id)
        if (index !== -1) {
          this.projects[index] = response.data
        }
        if (this.currentProject?.id === id) {
          this.currentProject = response.data
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || '更新项目失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteProject(id: number) {
      this.loading = true
      this.error = null
      try {
        await axios.delete(`/api/projects/${id}`)
        this.projects = this.projects.filter(p => p.id !== id)
        if (this.currentProject?.id === id) {
          this.currentProject = null
        }
      } catch (error: any) {
        this.error = error.message || '删除项目失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchProjectMembers(projectId: number) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/projects/${projectId}/members`)
        if (this.currentProject?.id === projectId) {
          this.currentProject.members = response.data
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || '获取项目成员失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async addProjectMember(projectId: number, memberData: {
      userId: number
      role: number
    }) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.post(`/api/projects/${projectId}/members`, memberData)
        if (this.currentProject?.id === projectId) {
          if (!this.currentProject.members) {
            this.currentProject.members = []
          }
          this.currentProject.members.push(response.data)
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || '添加项目成员失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async removeProjectMember(projectId: number, userId: number) {
      this.loading = true
      this.error = null
      try {
        await axios.delete(`/api/projects/${projectId}/members/${userId}`)
        if (this.currentProject?.id === projectId && this.currentProject.members) {
          this.currentProject.members = this.currentProject.members.filter(m => m.userId !== userId)
        }
      } catch (error: any) {
        this.error = error.message || '移除项目成员失败'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
