import axios from "./customize-axio.s";
const accessToken = localStorage.getItem("accessToken");

const fetchCommentData = (postId) =>{
    return axios.get(`comments/admin/${postId}`,
        {
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        }
    )
}

const putStatusComment = (id, status) =>{
    return axios.put(`comment/admin/${id}`,
        {status: status},
        {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                "Content-Type": "application/json"
            }
        }
    )
}
export {
    fetchCommentData,
    putStatusComment
}