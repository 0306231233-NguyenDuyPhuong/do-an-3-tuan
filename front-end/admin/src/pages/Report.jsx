import { useEffect, useState } from "react";
import { fetchReport, updateStatusReport } from "../services/ReportService";
import { Eye, Warning2 , Notification} from "iconsax-react";
import { NavLink, Outlet } from "react-router-dom";
import ReactPaginate from 'react-paginate';

const Report = () => {
    const [ listReport, setListReports] = useState([]);
    let [total, setTotatl] = useState(null);
    const statusClasses = {
    0: "bg-yellow-100 text-yellow-500 border border-yellow-300",
    1: "bg-blue-100 text-blue-500 border border-blue-300",
    2: "bg-green-100 text-green-500 border border-green-300",
    3: "bg-red-100 text-red-500 border border-red-300",
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/immutability
        getReport(1)
    }, []);

    const getReport = async (page) => {
        let res = await fetchReport(page);
        setTotatl(res.total);
        console.log(">>>>>>>>>>Report: ", res)
        if (res && res.data) {
            setListReports(res.data.rows)
        }
    }

    const updateStatus = async(id) =>{
        try {
            await updateStatusReport(id, 1);
            getReport();
        } catch (error) {
            alert("Update status error: ", error);
            <NavLink to={"/report"}/>
        }
    }

    const handlePageClick = (event) =>{
      console.log(">>> seleted: ",event)
      getReport(+event.selected+1);
    }

    return (
        <>
                <div className="flex gap-5">
                    <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
                    <div className="flex justify-between items-center">
                      <div className="flex gap-4">
                        <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                          <Notification size="25" color="#000"/>
                        </div>
                        <div className="text-xl font-bold"> Quantity Post</div>
                      </div>
                      <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                        <Warning2 size="20" color="#000"/>
                      </div>
                    </div>
                    <div className="flex gap-2 items-center justify-between mt-5">
                      <div className="flex items-center">
                        <span className="text-5xl font-bold mr-2 text-green-400">{total}</span>
                        <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 flex justify-center items-center">tang</span>
                      </div>
                      <div className="text-xl text-gray-400">
                        vs last week
                      </div>
                    </div>
                  </div>
                <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
                    <div className="flex justify-between items-center">
                      <div className="flex gap-4">
                        <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                          <Notification size="25" color="#000"/>
                        </div>
                        <div className="text-xl font-bold"> Quantity user</div>
                      </div>
                      <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                        <Warning2 size="20" color="#000"/>
                      </div>
                    </div>
                    <div className="flex gap-2 items-center justify-between mt-5">
                      <div className="flex items-center">
                        <span className="text-5xl font-bold mr-2 text-green-500">129</span>
                        <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-400 flex items-center justify-center">tang</span>
                      </div>
                      <div className="text-xl text-gray-400">
                        vs last week
                      </div>
                    </div>
                  </div>
                <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
                    <div className="flex justify-between items-center">
                      <div className="flex gap-4">
                        <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                          <Notification size="25" color="#000"/>
                        </div>
                        <div className="text-xl font-bold"> Quantity user</div>
                      </div>
                      <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                        <Warning2 size="20" color="#000"/>
                      </div>
                    </div>
                    <div className="flex gap-2 items-center justify-between mt-5">
                      <div className="flex items-center">
                        <span className="text-5xl font-bold mr-2 text-green-400">129</span>
                        <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 p-1 flex justify-center items-center">tang</span>
                      </div>
                      <div className="text-xl text-gray-400">
                        vs last week
                      </div>
                    </div>
                  </div>
                </div>

        <div className="flex border p-6 border-gray-200 rounded-2xl shadow-md overflow-x-auto mt-10">
        <table className="min-w-full border-collapse">
            <thead className="bg-gray-100">
            <tr>
                <th className="text-2xl h-15 px-4 py-2 text-center">Id</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Target type</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Reason</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Status</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Action</th>
            </tr>
            </thead>
            <tbody>
            {listReport.map((item, index) => (
                <tr key={`data-${index}`} className="hover:bg-gray-50">
                <td className="h-15 px-4 py-2 text-center text-2xl text-gray-400">{item.id}</td>
                
                <td className="h-15 px-4 py-2 text-center text-2xl text-gray-400">{item.target_type}</td>
                <td className="h-15 px-4 py-2 text-center text-2xl text-gray-400">{item.reason}</td>
                <td className="h-15 px-4 py-2 text-center">
                    <div
                    className={`inline-block px-3 py-1 rounded-md font-bold ${
                    statusClasses[item.status] ||
                    "bg-gray-100 text-gray-600 border border-gray-300"
                 }`}
                    >
                    {item.status}
                    </div>
                </td>
                <td className="h-15 px-4 py-2 text-center">
                    <NavLink to={item.target_type ==="post"? `/post/${item.target_id}`:`/user`} state={
                      {
                        reportId: item.id,
                      }
                    } className="hover:text-blue-500">
                    <Eye size="30" color="#C0C0C0" onClick={()=>updateStatus(item.id)}/>
                    </NavLink>
                </td>
                </tr>
            ))}
            </tbody>
        </table>
        <Outlet />
        </div>
      <ReactPaginate
        pageCount={Math.ceil(total / 10)}
        onPageChange={handlePageClick}
        containerClassName="flex justify-center gap-2 mt-4"
        pageLinkClassName="px-3 py-1 border rounded hover:bg-gray-100"
        previousLinkClassName="px-3 py-1 border rounded"
        nextLinkClassName="px-3 py-1 border rounded"
        activeLinkClassName="bg-black text-white"
      />


        </>
    )
}

export default Report;