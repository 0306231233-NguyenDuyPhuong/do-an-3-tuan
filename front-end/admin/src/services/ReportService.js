import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken")

const fetchReport = (page, report_type) =>{
    return axios.get(`reports?page=${page}&report_type=${report_type}`, {
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