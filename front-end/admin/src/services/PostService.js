import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken");
const fetchPostAdmin = (page)=>{
    return axios.get(`posts/admin?page=${page}`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    })
}

const fetchPostById = (id) =>{
    return axios.get(`posts/${id}`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    })
}

const putStatusPost = (id, newStatus) =>{
    return axios.put(`posts/admin/${id}`,
        {status: newStatus},
        {
            headers:{
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        }
    })
}
export {
    fetchPostAdmin,
    fetchPostById,
    putStatusPost
}