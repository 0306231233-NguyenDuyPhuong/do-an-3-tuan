import { useEffect, useState } from "react";
import { fetchReport, /*updateStatusReport*/ } from "../services/ReportService";
import { Eye, Warning2, Notification } from "iconsax-react";
import { NavLink, Outlet } from "react-router-dom";
import ReactPaginate from "react-paginate";
import { CiSearch } from "react-icons/ci";
import ReportModel from "../components/ReportModel"
import { TbReport } from "react-icons/tb";
import { useNavigate } from "react-router-dom";
import { fetchReportAction } from "../services/ReportActionService";

const Report = () => {
  const params = new URLSearchParams(location.search)
  const page = Number(params.get("page")) || 1;
  const search = params.get("search") || ""
  const [listReport, setListReports] = useState([]);
  const [total, setTotal] = useState(0);
  //const [currentPage, setCurrentPage] = useState(1);
  const [type, setType] = useState("")
  const [status, setStatus] = useState("")
  const [pageState, setPageState] = useState(1)
  const [searchState, setSearchState] = useState("")
  const [open, setOpen] = useState(false)
  const [selectedItem, setSelectedItem] = useState(null)
  const [reportActionData, setReportActionData] = useState([])
  const [reportId, setReportId] = useState(null)
  const navigate = useNavigate();
  //const [status, setStatus] = useState(0);
  // map status number -> text
  const statusMap = {
    0: "pending",
    1: "reviewed",
    2: "resolved",
    3: "rejected",
  };

  const ACTION_MAP = {
    0: { label: "Hide", color: "bg-gray-400" },
    1: { label: "Delete", color: "bg-red-500" },
    2: { label: "Warn", color: "bg-yellow-500" },
    3: { label: "Ban", color: "bg-black" },
  }

  const statusClasses = {
    pending: "bg-yellow-100 text-yellow-500 border border-yellow-300",
    reviewed: "bg-blue-100 text-blue-500 border border-blue-300",
    resolved: "bg-green-100 text-green-500 border border-green-300",
    rejected: "bg-red-100 text-red-500 border border-red-300",
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/immutability
    getReport({ page, search:search })
    if (open && reportId) {
      // eslint-disable-next-line react-hooks/immutability
      getReportAction(reportId)
    }
  }, [page], [open, reportId]);


  const getReport = async ({
    page = 1,
    status,
    report_type,
    report_id
  } = {}) => {
    try {
      const res = await fetchReport({
        page,
        status,
        report_type,
        report_id
      });
      if (res && res.data) {
        //setCurrentPage(page);
        setTotal(res?.data?.count ?? 0);
        setListReports(res?.data?.rows ?? []);
      }
    } catch (error) {
      console.log("Get Report error: ", error);
    }
  };

  const getReportAction = async (id) => {
    const res = await fetchReportAction(id)
    if (res && res.data) {
      setReportActionData(res.data)
    }
  }

  const openModel = (item) => {
    setReportId(item.id)
    setSelectedItem(item)
    getReportAction(item.id)
    setOpen(true)
  }

  const closeModeal = () => {
    setOpen(false)
    selectedItem(null)
  }

  const handleView = (item) => {
    if (item.target_type === "post") {
      navigate(`/post/${item.target_id}?page=${pageState}`, {
        state: { reportId: item.id }
      })
    } else if (item.target_type === "user") {
      navigate(`/user/${item.target_id}`, {
        state: { reportId: item.id }
      })
    }
  }

  // const handleClick = async () => {
  //   if (item.target_type === "post") {
  //     navigate(`/post/${item.target_id}`, { state: { highlightCommentId: item.id } });
  //   } else if (item.target_type === "user") {
  //     navigate(`/user/${item.target_id}`);
  //   } else {
  //     const res = await fetchCommentData(item.target_id);
  //     const postId = res.data.rows[0].id
  //     navigate(`/post/${postId}`, { state: { highlightCommentId: item.target_id } });
  //   }
  // }

  // const updateStatus = async (id) => {
  //   try {
  //     await updateStatusReport(id, 0);
  //     getReport(currentPage);
  //   } catch (error) {
  //     alert("Update status error", error);
  //   }
  // };

  const handlePageClick = (event) => {
        const selectedPage = event.selected +1;
    setPageState(selectedPage)
    navigate(`?page=${selectedPage}`);
  };

  // const handleReportComment = async (comment) => {
  //   await fetchCommentData(comment.id);
  // }


  if (!listReport) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <div className="flex gap-5">
        <div className="flex-1 bg-white rounded-xl border border-gray-300  p-10 shadow-md">
          <div className="flex justify-between items-center">
            <div className="flex gap-4 items-center">
              <div className="size-10 border rounded-md p-1">
                <Notification size="25" />
              </div>
              <div className="text-xl font-bold">Quantity Report</div>
            </div>
            <Warning2 size="20" />
          </div>
          <div className="mt-5 flex justify-between items-center">
            <span className="text-5xl font-bold text-green-400">{total}</span>
            <span className="text-gray-400">vs last week</span>
          </div>
        </div>
        <div className="flex-1 bg-white rounded-xl">
          
        </div>
        <div className="flex-1 bg-white rounded-xl">
          
        </div>
      </div>

      <div className="flex gap-5 items-center mt-10">
        {/* Search box */}
        <div className="flex flex-3 items-center gap-3 border border-gray-300 rounded-2xl h-20 p-4 shadow-md flex-1">
          <CiSearch size={30} />
          <input
            type="text"
            name="search"
            placeholder="Search post"
            className="flex-1 outline-none text-2xl h-10"
            value={searchState}
            onChange={(e) => {
                const value = e.target.value;
                setSearchState(value)
                navigate(`?page=1&search=${value}`);
            }}
          />
        </div>

        <div className="flex flex-1 items-center gap-5 justify-center">
          <div className="flex items-center border h-20 border-gray-300 rounded-xl shadow-md px-3 py-2">
            <select
              className="outline-none text-2xl"
              value={type}
              onChange={(e) => {
                const value = (e.target.value).toString();
                setType(value);
                console.log(value)
                getReport({ page: 1, report_type: value })
              }}
            >
              <option value="">Type</option>
              <option value="post">post</option>
              <option value="comment">comment</option>
              <option value="user">user</option>
            </select>
          </div>
          <div className="flex items-center h-20 border border-gray-300 rounded-md shadow-md px-3 py-2">
            <select
              className="outline-none text-2xl"
              value={status}
              onChange={(e) => {
                const value = e.target.value;
                setStatus(value);
                getReport({ page: 1, status: value })
              }}
            >
              <option value="">status</option>
              <option value="0">pending</option>
              <option value="1">reviewed</option>
              <option value="2">resolved</option>
              <option value="3">rejected</option>
            </select>
          </div>
          <div className="flex items-center justify-center h-20 border px-2 border-gray-300 rounded-md shadow-md">
            <input className="outline-none text-xl"
              type="date" />
          </div>
          <div className="flex items-center justify-center h-20 border px-2 border-gray-300 rounded-md shadow-md">
            <input className="outline-none text-xl"
              type="date" />
          </div>
        </div>
      </div>


      <div className="flex border border-gray-100 p-6 rounded-2xl shadow-md mt-10 overflow-x-auto">
        <table className="min-w-full">
          <thead className="bg-gray-100">
            <tr>
              <th className="text-2xl py-2">Id</th>
              <th className="text-2xl py-2">Reporter</th>
              <th className="text-2xl py-2">Target type</th>
              <th className="text-2xl py-2">Reason</th>
              <th className="text-2xl py-2">Status</th>
              <th className="text-2xl py-2">Action</th>
            </tr>
          </thead>
          <tbody>
            {listReport.length === 0 && (
              <tr>
                <td colSpan="5" className="text-center py-5 text-gray-400">
                  No report data
                </td>
              </tr>
            )}

            {listReport.map((item) => {
              const statusText = statusMap[item.status] || "pending";

              return (
                <tr key={item.id} className="hover:bg-gray-50">
                  <td className="text-center text-2xl py-2 text-gray-400">{item.id}</td>
                  <td className="text-center text-2xl py-2 text-gray-400">
                    <div className="flex flex-row items-center">
                      <td className="h-15 px-4 py-2 flex items-center justify-center">
                        <div className="w-12 h-12 bg-amber-400 rounded-full overflow-hidden">
                          <img
                            className="w-12 h-12 object-cover rounded-full"
                            src={`http://localhost:8989/api/images/${item.User.avatar}`}
                          />
                        </div>
                      </td>
                      <span>
                        {item.User.full_name}
                      </span>
                    </div>
                  </td>
                  <td className="text-center text-2xl py-2 text-gray-400">
                    {item.target_type}
                  </td>
                  <td className="text-center text-2xl py-2 text-gray-400">
                    {item.reason}
                  </td>
                  <td className="text-center py-2">
                    <span
                      className={`px-3 py-1 rounded-md font-bold ${statusClasses[statusText]
                        }`}
                    >
                      {statusText}
                    </span>
                  </td>
                  <td className="text-center py-2">
                    <div className="flex flex-row justify-center items-center gap-2">

                      {/* Eye → xem */}
                      <button
                        onClick={() => handleView(item)}
                        className="hover:scale-110 transition"
                      >
                        <Eye size={30} color="#C0C0C0" />
                      </button>

                      {/* Report → mở modal */}
                      <button
                        onClick={() => openModel(item)}
                        className="hover:scale-110 transition"
                      >
                        <TbReport size={30} color="#C0C0C0" />
                      </button>

                    </div>
                  </td>

                </tr>
              );
            })}
          </tbody>
        </table>
        <Outlet />
      </div>

      <ReactPaginate
        pageCount={Math.ceil((total || 0) / 10)}
        onPageChange={handlePageClick}
        containerClassName="flex justify-center gap-2 mt-6"
        pageLinkClassName="px-3 py-1 border rounded hover:bg-gray-100"
        previousLinkClassName="px-3 py-1 border rounded"
        nextLinkClassName="px-3 py-1 border rounded"
        activeLinkClassName="bg-black text-white"
      />
      {open && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-[520px]">

            {!reportActionData ? (
              <div className="text-center py-10">
                Loading...
              </div>
            ) : (
              <>
                <h2 className="text-lg font-bold mb-4">
                  Report Action Detail
                </h2>

                <div className="mb-4">
                  <p className="font-semibold mb-1">Admin xử lý</p>
                  <div className="flex items-center gap-3">
                    <img
                      src={`http://localhost:8989/api/images/${reportActionData.User?.avatar}`}
                      className="w-10 h-10 rounded-full object-cover"
                    />
                    <span>{reportActionData.User?.full_name}</span>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3 text-sm mb-4">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold">Action:</span>
                    <span
                      className={`px-2 py-1 rounded text-white text-xs
                ${ACTION_MAP[reportActionData.action]?.color}`}
                    >
                      {ACTION_MAP[reportActionData.action]?.label}
                    </span>
                  </div>

                  <div>
                    <span className="font-semibold">Note:</span>
                    <span className="ml-2">
                      {reportActionData.note || "—"}
                    </span>
                  </div>

                  <div>
                    <span className="font-semibold">Created at:</span>
                    <span className="ml-2">
                      {new Date(reportActionData.created_at).toLocaleString()}
                    </span>
                  </div>

                  <div>
                    <span className="font-semibold">Updated at:</span>
                    <span className="ml-2">
                      {new Date(reportActionData.updated_at).toLocaleString()}
                    </span>
                  </div>
                </div>

                <div className="border-t pt-4">
                  <p className="font-semibold mb-2">Report detail</p>

                  <div className="space-y-2 text-sm">
                    <div>
                      <span className="font-semibold">Target:</span>
                      <span className="ml-2">
                        {reportActionData.Report?.target_type}
                        #{reportActionData.Report?.target_id}
                      </span>
                    </div>

                    <div>
                      <span className="font-semibold">Reason:</span>
                      <span className="ml-2">
                        {reportActionData.Report?.reason}
                      </span>
                    </div>

                    <div>
                      <span className="font-semibold">Description:</span>
                      <p className="ml-2 text-gray-600">
                        {reportActionData.Report?.description}
                      </p>
                    </div>
                  </div>
                </div>

                <div className="border-t pt-4 mt-4">
                  <p className="font-semibold mb-2">Reporter</p>
                  <div className="flex items-center gap-3">
                    <img
                      src={`http://localhost:8989/api/images/${reportActionData.Report?.User?.avatar}`}
                      className="w-9 h-9 rounded-full"
                    />
                    <span>{reportActionData.Report?.User?.full_name}</span>
                  </div>
                </div>

                <div className="flex justify-end mt-6">
                  <button
                    className="px-4 py-2 bg-red-500 text-white rounded"
                    onClick={closeModeal}
                  >
                    Close
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}



    </>
  );
};

export default Report;
