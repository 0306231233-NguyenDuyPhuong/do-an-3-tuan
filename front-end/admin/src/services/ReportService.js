import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken")

const fetchReport = (page, status, type, report_id) =>{
    const statusList = [0, 1, 2, 3]
    const typeList = ["post", "user", "comment"]
    if(report_id!=""){
        return axios.get(`reports?page=${page}&report_id=${report_id}`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    })
    }
    if(typeList.includes(type)){
        return axios.get(`reports?page=${page}&report_type=${type}`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    })
    }
    if(statusList.includes(status)){
        return axios.get(`reports?page=${page}&status=${status}`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    })
    } 
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