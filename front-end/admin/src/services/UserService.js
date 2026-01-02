// src/services/UserService.js
import axios from "axios";

const API_URL = "http://localhost:8989/api/auth";

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
