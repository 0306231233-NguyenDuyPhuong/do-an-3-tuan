import axios from "axios";
const accessToken = localStorage.getItem("accessToken")
const API_URL = "http://localhost:8989/api/auth";
import Axios from "../services/customize-axio.s";
export const postLoginUser = (data) => {
  return axios.post(`${API_URL}/login`, {
    username: data.username,
    password: data.password,
  }, {
    headers: {
      "Content-Type": "application/json",
    }
  });
};

export const fetchUser = () =>{
  return Axios.get(`users`, 
    {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
  )
}

export const fetchUserById = (id) => {
  return Axios.get(`users/${id}`,
    {
      headers:{
        Authorization: `Bearer ${accessToken}`
      }
    }
  )
}
