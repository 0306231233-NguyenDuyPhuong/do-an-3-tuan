import axios from "axios";

const instance = axios.create({
    baseURL: 'http://localhost:8989/api/'
});

// Add a response interceptor
instance.interceptors.response.use(function (response) {
    // Any status code that lies within the range of 2xx causes this function to trigger
    // Do something with response data
    return response.data;
  }, function (error) {
    // Any status codes that fall outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error);
  });


export default instance;