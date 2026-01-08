import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken")

const postReportAction = (data)=>{
    return axios.post(`report-actions`, 
        {
            report_id: data.report_id,
            admin_id: data.admin_id,
            action: data.action, 
            note: data.note
        }, {
            headers: {
                Authorization: `Bearer ${accessToken}`, 
                "Content-Type":"application/json"
            }
        }
    )
}

export {
    postReportAction
}