import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken");
const fetchPostAdmin = ({
    page=1,
    search="",
    sort="", 
    date,
    dateStart, 
    dateEnd
})=>{
    return axios.get(`posts/admin`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        },
        params: {
            page, 
            search, 
            sort, 
            ...(date && {date}),
            ...(dateStart && dateEnd && {
                dateStart, 
                dateEnd,
            })
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