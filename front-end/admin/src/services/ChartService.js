import axios from "../services/customize-axio.s";
const accessToken = localStorage.getItem("accessToken");

const fetchDataChart = () =>{
    return axios.get("admin/dashbord/statistics", 
        {
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        }
    )
}

const fetchDataCount = () =>{
    return axios.get("admin/dashboard/count", 
        {
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        }
    )
}
export{
    fetchDataChart, 
    fetchDataCount
}