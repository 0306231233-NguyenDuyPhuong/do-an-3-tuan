import { Outlet } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";

const Layout = () => {
  return (
    <div className="flex bg-gray-200 h-screen w-screen">
      <div className="w-100 h-screen bg-gray-100 border border-gray-200">
        <Sidebar />
      </div>

      <div className="flex flex-col flex-1">
        <div className="h-50 bg-white py-5 px-20">
          <Header />
        </div>

        <div className="flex-1 overflow-y-auto p-5 bg-white">
          <Outlet /> 
        </div>
      </div>
    </div>
  );
};

export default Layout;
