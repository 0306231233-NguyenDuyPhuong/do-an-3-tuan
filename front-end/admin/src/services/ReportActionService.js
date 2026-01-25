import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken")

const postReportAction = (report_id, admin_id, action )=>{
    return axios.post(`report-actions`, 
        {
            report_id: report_id,
            admin_id: admin_id,
            action: action, 
            note: ""
        }, {
            headers: {
                Authorization: `Bearer ${accessToken}`, 
                "Content-Type":"application/json"
            }
        }
    )
}

const fetchReportAction = (id) =>{
    return axios.get(`report-actions/${id}`,{
            headers: {
                Authorization: `Bearer ${accessToken}`, 
                "Content-Type":"application/json"
            }
        })
}
export {
    postReportAction,
    fetchReportAction
}