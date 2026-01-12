import axios from "axios";

const instance = axios.create({
    baseURL: 'http://localhost:8989/api/'
});

instance.interceptors.response.use(function (response) {
    return response.data;
  }, function (error) {
    return Promise.reject(error);
  });

instance.interceptors.request.use((config)=>{
  const token = localStorage.getItem("accessToken")
  if(token){
    config.headers.Authorization = `Bearer ${token}`
  }
  return config;
})

export default instance;