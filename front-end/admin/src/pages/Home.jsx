/* eslint-disable react-hooks/immutability */
import { Notification, Warning2 } from "iconsax-react";
import RevenueChart from "../components/BarChart";
import Calenda from "../components/Calenda";
import { BsBookmark } from "react-icons/bs";
import { FaRegUser   } from "react-icons/fa";
import { IoNewspaperOutline } from "react-icons/io5";
import { RiErrorWarningLine } from "react-icons/ri";
import UserChart from "../components/LineChart";
import { useEffect, useState } from "react";
import {fetchDataChart, fetchDataCount} from "../services/ChartService"
import ReportByTypeBar from "../components/BarChart";
const Home = () => {
  const [chartData, setChartData] = useState([]);
  const [countData, setCountData] = useState([]);

  useEffect(()=>{
    // eslint-disable-next-line react-hooks/immutability
    getChartData(),
    getCountData()
  }, [])

  const getChartData = async ()=>{
    let res = await fetchDataChart()
    if(res){
      setChartData(res)
    }
  }

  const getCountData = async() =>{
    let res = await fetchDataCount();
    if(res){
      setCountData(res);
    }
  }

  if (!chartData) {
    return <div className="flex justify-center items-center h-64">
      <div className="w-12 h-12 border-4 border-blue-500 border-dashed rounded-full animate-spin"></div>
    </div>;
  }  
  if (!countData) {
    return <div className="flex justify-center items-center h-64">
      <div className="w-12 h-12 border-4 border-blue-500 border-dashed rounded-full animate-spin"></div>
    </div>;
  }  
  
  return (
    <>
      <div className="flex flex-col w-full min-w-0 bg-w">
        <div className="flex gap-4 flex-wrap">
          <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
            <div className="flex justify-between items-center">
              <div className="flex gap-4">
                <div className="flex items-center size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                  <BsBookmark size="20" color="grey"/>
                </div>
                <div className="text-xl font-bold text-yellow-400"> Quantity Report</div>
              </div>
              <div className="flex items-center justify-center size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                <RiErrorWarningLine size="20" color="#000"/>
              </div>
            </div>
            <div className="flex gap-2 items-center justify-between mt-5">
              <div className="flex items-center">
                <span className="text-5xl font-bold mr-2">{countData.reports}</span>
              </div>
            </div>
          </div>

          <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
            <div className="flex justify-between items-center">
              <div className="flex gap-4">
                <div className="flex items-center size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                  <FaRegUser size="20" color="grey"/>
                </div>
                <div className="text-xl font-bold text-green-300"> Quantity user</div>
              </div>
              <div className="flex items-center justify-center size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                <RiErrorWarningLine size="20" color="#000"/>
              </div>
            </div>
            <div className="flex gap-2 items-center justify-between mt-5">
              <div className="flex items-center">
                <span className="text-5xl font-bold mr-2">{countData.users}</span>
              </div>
            </div>
          </div>


          <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
            <div className="flex justify-between items-center">
              <div className="flex gap-4">
                <div className="flex items-center size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                  <IoNewspaperOutline size="20" color="grey"/>
                </div>
                <div className="text-xl font-bold text-blue-300"> Quantity Post</div>
              </div>
              <div className="flex items-center justify-center size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                <RiErrorWarningLine size="20" color="#000"/>
              </div>
            </div>
            <div className="flex gap-2 items-center justify-between mt-5">
              <div className="flex items-center">
                <span className="text-5xl font-bold mr-2">{countData.posts}</span>
              </div>
            </div>
          </div>

        </div>

        <div className="flex flex-col">
        <div className="flex gap-5 mt-10">
          <div className="flex-3 h-100 bg-white rounded-xl border border-gray-300 border-2 shadow-md p-10">
            <span className="text-2xl font-bold text-green-300">USER</span>
            <div className="flex justify-between">
              <div className="text-black font-bold text-xl">Revenue</div>
              <div className="text-black font-bold text-xl">Revenue</div>
            </div>
            <UserChart data={chartData.usersChart}/>
          </div>

          <div className="flex-3 h-100 bg-white rounded-xl border border-gray-300 border-2 shadow-md p-10">
            <span className="text-2xl font-bold text-green-300">POST</span>
            <div className="flex justify-between">
              <div className="text-black font-bold text-xl">Revenue</div>
              <div className="text-black font-bold text-xl">Revenue</div>
            </div>
            <div style={{ width: "100%", height: 350 }}>
            <UserChart data={chartData.postsChart}/>
            </div>
          </div>
        </div>
        </div>
        
        <div className="flex gap-5 mt-10">
          <div className="flex-1 h-100 bg-white rounded-xl border border-gray-300 border-2 shadow-md p-10">
            <span className="text-2xl font-bold text-yellow-300">REPORT</span>
            <div className="flex justify-between">
              <div className="text-black font-bold text-xl">Revenue</div>
              <div className="text-black font-bold text-xl">Revenue</div>
            </div>
            <UserChart data={chartData.postsChart}/>
          </div>
          <div className="flex-1 h-100 bg-white rounded-xl border border-gray-300 border-2 shadow-md p-10">
            <div className="flex justify-between">
              <div className="text-black font-bold text-xl">Revenue</div>
              <div className="text-black font-bold text-xl">Revenue</div>
            </div>
            <ReportByTypeBar data={chartData.reportByType}/>
          </div>
        </div>
      </div>
    </>
  );
};

export default Home;
