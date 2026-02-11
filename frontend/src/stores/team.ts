import { defineStore } from 'pinia'
import axios from 'axios'
import { useUserStore } from './user'

export interface Team {
  id: number
  name: string
  description: string
  creatorId: number
  createdAt: string
  updatedAt: string
  status: number
  members?: TeamMember[]
}

export interface TeamMember {
  id: number
  teamId: number
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

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [] as Team[],
    currentTeam: null as Team | null,
    loading: false,
    error: null as string | null
  }),

  getters: {
    getTeams: (state) => state.teams,
    getCurrentTeam: (state) => state.currentTeam
  },

  actions: {
    async fetchTeams() {
      this.loading = true
      this.error = null
      try {
        const userStore = useUserStore()
        const currentUser = userStore.getUser
        if (!currentUser) {
          throw new Error('用户未登录')
        }
        const response = await axios.get(`/api/teams/user/${currentUser.id}`)
        this.teams = response.data
      } catch (error: any) {
        this.error = error.message || '获取团队列表失败'
      } finally {
        this.loading = false
      }
    },

    async fetchTeam(id: number) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/teams/${id}`)
        this.currentTeam = response.data
      } catch (error: any) {
        this.error = error.message || '获取团队详情失败'
      } finally {
        this.loading = false
      }
    },

    async createTeam(teamData: {
      name: string
      description: string
    }) {
      this.loading = true
      this.error = null
      try {
        const userStore = useUserStore()
        const currentUser = userStore.getUser
        if (!currentUser) {
          throw new Error('用户未登录')
        }
        const teamWithCreator = {
          ...teamData,
          creatorId: currentUser.id,
          status: 1
        }
        const response = await axios.post('/api/teams', teamWithCreator)
        this.teams.push(response.data)
        return response.data
      } catch (error: any) {
        this.error = error.message || '创建团队失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateTeam(id: number, teamData: {
      name: string
      description: string
      status?: number
    }) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.put(`/api/teams/${id}`, teamData)
        const index = this.teams.findIndex(t => t.id === id)
        if (index !== -1) {
          this.teams[index] = response.data
        }
        if (this.currentTeam?.id === id) {
          this.currentTeam = response.data
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || '更新团队失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteTeam(id: number) {
      this.loading = true
      this.error = null
      try {
        await axios.delete(`/api/teams/${id}`)
        this.teams = this.teams.filter(t => t.id !== id)
        if (this.currentTeam?.id === id) {
          this.currentTeam = null
        }
      } catch (error: any) {
        this.error = error.message || '删除团队失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchTeamMembers(teamId: number) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/teams/${teamId}/members`)
        if (this.currentTeam?.id === teamId) {
          this.currentTeam.members = response.data
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || '获取团队成员失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async addTeamMember(teamId: number, memberData: {
      userId: number
      role: number
    }) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.post(`/api/teams/${teamId}/members`, memberData)
        if (this.currentTeam?.id === teamId) {
          if (!this.currentTeam.members) {
            this.currentTeam.members = []
          }
          this.currentTeam.members.push(response.data)
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || '添加团队成员失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async removeTeamMember(teamId: number, userId: number) {
      this.loading = true
      this.error = null
      try {
        await axios.delete(`/api/teams/${teamId}/members/${userId}`)
        if (this.currentTeam?.id === teamId && this.currentTeam.members) {
          this.currentTeam.members = this.currentTeam.members.filter(m => m.userId !== userId)
        }
      } catch (error: any) {
        this.error = error.message || '移除团队成员失败'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
