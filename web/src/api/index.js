import axios from 'axios'

const request = axios.create({
  baseURL: '/api/v1/knowledge',
  timeout: 30000
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) return res.data
    return Promise.reject(new Error(res.message || 'Error'))
  },
  error => Promise.reject(error)
)

export default request
