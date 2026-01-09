import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken")

const fetchReport = (page) =>{
    return axios.get(`reports?page=${page}`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    })
}

const updateStatusReport = (id, newStatus) =>{
    return axios.put(`reports/${id}`,
        {status: newStatus},
        {
           headers: { Authorization: `Bearer ${accessToken}`,
           "Content-Type": "application/json"
        }
        }
    )
}

export {
    fetchReport, 
    updateStatusReport
}