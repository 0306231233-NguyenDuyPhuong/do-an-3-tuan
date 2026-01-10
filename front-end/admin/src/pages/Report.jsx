import { useEffect, useState } from "react";
import { fetchReport, updateStatusReport } from "../services/ReportService";
import { Eye, Warning2, Notification } from "iconsax-react";
import { NavLink, Outlet } from "react-router-dom";
import ReactPaginate from "react-paginate";

const Report = () => {
  const [listReport, setListReports] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);

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
  }, []);

  // ✅ FIX THEO RESPONSE API
  const getReport = async (page = 1) => {
    const res = await fetchReport(page);

    setCurrentPage(page);
    setTotal(res?.data?.count ?? 0);
    setListReports(res?.data?.rows ?? []);
  };

  const updateStatus = async (id) => {
    try {
      await updateStatusReport(id, "reviewed");
      getReport(currentPage); // ✅ reload đúng page
    } catch (error) {
      alert("Update status error", error);
    }
  };

  const handlePageClick = (event) => {
    getReport(event.selected + 1);
  };

  // loading / empty
  if (!Array.isArray(listReport)) {
    return <div>Loading...</div>;
  }

  return (
    <>
      {/* ===== SUMMARY ===== */}
      <div className="flex gap-5">
        <div className="flex-1 bg-white rounded-xl border p-10 shadow-md">
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

      {/* ===== TABLE ===== */}
      <div className="flex border p-6 rounded-2xl shadow-md mt-10 overflow-x-auto">
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
                  <td className="text-center py-2 text-gray-400">{item.id}</td>
                  <td className="text-center py-2 text-gray-400">
                    {item.target_type}
                  </td>
                  <td className="text-center py-2 text-gray-400">
                    {item.reason}
                  </td>
                  <td className="text-center py-2">
                    <span
                      className={`px-3 py-1 rounded-md font-bold ${
                        statusClasses[statusText]
                      }`}
                    >
                      {statusText}
                    </span>
                  </td>
                  <td className="text-center py-2">
                    <NavLink
                      to={
                        item.target_type === "post"
                          ? `/post/${item.target_id}`
                          : `/user/${item.target_id}`
                      }
                      state={{ reportId: item.id }}
                    >
                      <Eye
                        size="30"
                        color="#C0C0C0"
                        onClick={() => updateStatus(item.id)}
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
