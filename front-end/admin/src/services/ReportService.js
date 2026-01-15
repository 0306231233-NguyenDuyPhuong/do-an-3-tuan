import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken")

const fetchReport = ({
    page,
    status, 
    report_type, 
    report_id
}) => {
    return axios.get(`reports`, {
            headers: {
                Authorization: `Bearer ${accessToken}`
            },
            params: {
                page, 
                status, 
                ...(report_type && {report_type}),
                report_id
            }
        })
}

const updateStatusReport = (id, newStatus) => {

    return axios.put(`reports/${id}`,
        { status: newStatus },
        {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                "Content-Type": "application/json"
            }
        }
    )
}

export {
    fetchReport,
    updateStatusReport
}