import { useEffect, useState } from "react";
import { fetchReport, updateStatusReport } from "../services/ReportService";
import { Eye, Warning2, Notification } from "iconsax-react";
import { NavLink, Outlet } from "react-router-dom";
import ReactPaginate from "react-paginate";
import { CiSearch } from "react-icons/ci";
import { fetchCommentData } from "../services/CommentService";
import { useNavigate } from "react-router-dom";

const Report = () => {
  const [listReport, setListReports] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [type, setType] = useState("")
  const [status, setStatus] = useState("")
  const [search, setSearch] = useState("")
  const navigate = useNavigate();
  //const [status, setStatus] = useState(0);
  // map status number -> text
  const statusMap = {
    1: "pending",
    2: "reviewed",
    3: "resolved",
    4: "rejected",
  };

  const statusClasses = {
    pending: "bg-yellow-100 text-yellow-500 border border-yellow-300",
    reviewed: "bg-blue-100 text-blue-500 border border-blue-300",
    resolved: "bg-green-100 text-green-500 border border-green-300",
    rejected: "bg-red-100 text-red-500 border border-red-300",
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/immutability
    getReport(1);
    // eslint-disable-next-line react-hooks/immutability
  }, []);

  const getReport = async (page = 1, status = 4, type = "", id = "") => {
    const res = await fetchReport(page, status, type, id);
    setCurrentPage(page);
    setTotal(res?.data?.count ?? 0);
    setListReports(res?.data?.rows ?? []);
  };

  const handleClick = async (item) => {

    if (item.target_type === "post") {
      navigate(`/post/${item.target_id}`, { state: { highlightCommentId: item.id } });
    } else if (item.target_type === "user") {
      navigate(`/user/${item.target_id}`);
    } else {
      const res = await fetchCommentData(item.target_id);
      const postId = res.data.rows[0].id
      navigate(`/post/${postId}`, { state: { highlightCommentId: item.target_id } });
    }
  }

  const updateStatus = async (id) => {
    try {
      await updateStatusReport(id, 0);
      getReport(currentPage);
    } catch (error) {
      alert("Update status error", error);
    }
  };

  const handlePageClick = (event) => {
    getReport(event.selected + 1);
  };

  const handleReportComment = async (comment) => {
    await fetchCommentData(comment.id);

    const postId = comment.post_id;
    console.log(">>>>>>>>>>", postId)
  }


  if (!Array.isArray(listReport)) {
    return <div>Loading...</div>;
  }



  return (
    <>
      <div className="flex gap-5">
        <div className="flex-1 bg-white rounded-xl border border-gray-100  p-10 shadow-md">
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
      </div>

      <div className="flex gap-5 items-center mt-10">
        {/* Search box */}
        <div className="flex items-center gap-3 border border-gray-300 rounded-2xl h-20 p-4 shadow-md flex-1">
          <CiSearch size={30} />
          <input
            type="text"
            name="search"
            placeholder="Search post"
            className="flex-1 outline-none text-2xl h-10"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                const reportId = Number(search)
                getReport(1, 5, "", reportId)
                console.log(">>>>>>", setListReports)
              }
            }}
          />
        </div>

        {/* Filter select */}
        <div className="flex flex-1 items-center justify-center">
          <div className="border border-gray-300 rounded-md shadow-md px-3 py-2">
            <select
              className="outline-none text-2xl font-bold"
              value={type}
              onChange={(e) => {
                const value = (e.target.value).toString();
                setType(value);
                getReport(1, 4, value)
              }}
            >
              <option value="post">post</option>
              <option value="comment">comment</option>
              <option value="user">user</option>
            </select>
          </div>
        </div>

        <div className="flex flex-1 items-center justify-center">
          <div className="border border-gray-300 rounded-md shadow-md px-3 py-2">
            <select
              className="outline-none text-2xl font-bold"
              value={status}
              onChange={(e) => {
                const value = Number(e.target.value);
                setStatus(value);
                getReport(value)
              }}
            >
              <option value="0">pending</option>
              <option value="1">reviewed</option>
              <option value="2">resolved</option>
              <option value="3">rejected</option>
            </select>
          </div>
        </div>
      </div>


      <div className="flex border border-gray-100 p-6 rounded-2xl shadow-md mt-10 overflow-x-auto">
        <table className="min-w-full">
          <thead className="bg-gray-100">
            <tr>
              <th className="text-2xl py-2">Id</th>
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
                    <NavLink
                      to={
                        item.target_type === "post"
                          ? `/post/${item.target_id}` :
                          (item.target_type === "user")
                            ? (`/user/${item.target_id}`) :
                            handleReportComment(item.target_id)
                      }
                      state={{ reportId: item.id }}

                    >
                      <Eye
                        size="30"
                        color="#C0C0C0"
                        onClick={() => {
                          handleClick(item)
                          updateStatus(item.id)
                        }}
                      />
                    </NavLink>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        <Outlet />
      </div>

      {/* ===== PAGINATION ===== */}
      <ReactPaginate
        pageCount={Math.ceil((total || 0) / 10)}
        onPageChange={handlePageClick}
        containerClassName="flex justify-center gap-2 mt-6"
        pageLinkClassName="px-3 py-1 border rounded hover:bg-gray-100"
        previousLinkClassName="px-3 py-1 border rounded"
        nextLinkClassName="px-3 py-1 border rounded"
        activeLinkClassName="bg-black text-white"
      />
    </>
  );
};

export default Report;
