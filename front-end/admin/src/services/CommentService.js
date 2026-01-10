import axios from "./customize-axio.s";
const accessToken = localStorage.getItem("accessToken");

const fetchCommentData = (postId) =>{
    return axios.get(`comments/${postId}`,
        {
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        }
    )
}

export {
    fetchCommentData
}