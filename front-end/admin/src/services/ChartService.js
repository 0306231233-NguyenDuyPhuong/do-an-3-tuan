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

export{
    fetchDataChart
}