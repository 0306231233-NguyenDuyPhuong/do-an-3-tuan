import axios from "axios";

const API_URL = "http://localhost:8989/api/auth";
const accessToken = localStorage.getItem("accessToken");

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

export const fetchUser = (page, search, role)=>{
  return axios.get(`http://localhost:8989/api/users?page=${page}&search=${search}&role=${role}`, 
    {headers: {
      Authorization: `Bearer ${accessToken}`
    }}
  )
}

export const fetchUserById = (id)=>{
  return axios.get(`http://localhost:8989/api/users/${id}`, 
    {headers: {
      Authorization: `Bearer ${accessToken}`
    }}
  )
}

export const putStatusUser = (id, newStatus) =>{
    return axios.put(`http://localhost:8989/api/admin/user/${id}`,
        {status: newStatus},
        {
            headers:{
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        }
    })
}

export const refreshAccessToken = async () =>{
  try{
    const refreshToken = localStorage.getItem("refreshToken");

    if(!refreshToken) return;
    const res = await axios.post(
      "http://localhost:8989/api/auth/refresh",
      {refreshToken}
    );
    const newAccessToken = res.data.accessToken;
    localStorage.setItem("accessToken", newAccessToken);
    console.log("SuCCEss")
  } catch(error){
    localStorage.clear();
    console.log(error)
  }
}