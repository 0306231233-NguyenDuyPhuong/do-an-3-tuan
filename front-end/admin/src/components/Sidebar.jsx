import { Home, } from "iconsax-react";
import { FaChartBar, FaRegUser } from "react-icons/fa";
import { LuNewspaper } from "react-icons/lu";
import { BsBookmark } from "react-icons/bs";
import { FiLogOut } from "react-icons/fi";
import { NavLink } from "react-router-dom";
import logo from "../images/logo.png"
import { LuUserPen } from "react-icons/lu";

const Sidebar = () => {
    const user = JSON.parse(localStorage.getItem("user"))
    const full_name = user?.full_name;
    const email = user?.email
    const avatar = user?.avatar
    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
    }
    return (
        <>
            <div className="flex flex-col pt-5 px-5">
                <div className="flex flex gap-1 items-center gap-5">
                    <div className="size-15 p-3">
                        <img src={logo} className="w-full rounded-full" />
                    </div>
                    <div className="flex flex-col">
                        <div className="text-black text-xl font-bold">{full_name}</div>
                        <div className="text-gray-320 text-sm">Pivora</div>
                    </div>
                </div>

                <div className="flex flex-row h-13 w-80 justify-between border bg-gray-200 border-none rounded-xl
            mt-10 px-3 py-1 items-center
            ">
                    <div className="w-10 h-10 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                        <img
                            src={`http://localhost:8989/api/images/${avatar}`}
                            alt="avatar"
                            className="w-full h-full object-cover"
                            onError={(e) => {
                                e.target.src = "/default-avatar.png"
                            }}
                        />
                    </div>

                    <div>{email}</div>
                    <div>item3</div>
                </div>
                <div className="flex flex-col p-5 gap-2 border-gray-300 border-t mt-5">
                    <div className="flex flex-col gap-4">
                        <NavLink to="/" className={({ isActive }) =>
                            `block rounded-lg px-4 py-2 transition 
                        ${isActive
                                ? "bg-[rgb(132,93,240)] text-white font-bold"
                                : "hover:bg-gray-100 text-gray-500"}`
                        }>
                            <div className="flex items-center gap-3">
                                <FaChartBar size={30} color="grey" />
                                <span className="text-xl">Dashboard</span>
                            </div>
                        </NavLink>

                        <NavLink
                            to="/post"
                            className={({ isActive }) =>
                                `block rounded-lg px-4 py-2 transition
                        ${isActive
                                    ? "bg-[rgb(132,93,240)] text-white font-bold"
                                    : "hover:bg-gray-100 text-gray-500"}`
                            }
                        >
                            <div className="flex items-center gap-3">
                                <LuNewspaper size={30} className="text-current" />
                                <span className="text-xl">Post</span>
                            </div>
                        </NavLink>

                        <NavLink to="/user" className={({ isActive }) =>
                            `block rounded-lg px-4 py-2 transition 
                        ${isActive
                                ? "bg-[rgb(132,93,240)] text-white font-bold"
                                : "hover:bg-gray-100 text-gray-500"}`
                        }>
                            <div className="flex items-center gap-3">
                                <FaRegUser size="30" color="grey" />
                                <span className="text-xl">User</span>
                            </div>
                        </NavLink>


                        <NavLink to="/report" className={({ isActive }) =>
                            `block rounded-lg px-4 py-2 transition 
                        ${isActive
                                ? "bg-[rgb(132,93,240)] text-white font-bold"
                                : "hover:bg-gray-100 text-gray-500"}`
                        }>
                            <div className="flex items-center gap-3">
                                <BsBookmark size="30" color="grey" />
                                <span className="text-xl">Report</span>
                            </div>
                        </NavLink>

                        <NavLink to="/user-profile" className={({ isActive }) =>
                            `block rounded-lg px-4 py-2 transition 
                        ${isActive
                                ? "bg-[rgb(132,93,240)] text-white font-bold"
                                : "hover:bg-gray-100 text-gray-500"}`
                        }>
                            <div className="flex items-center gap-3">
                                <LuUserPen size="30" color="grey" />
                                <span className="text-xl">User profile</span>
                            </div>
                        </NavLink>
                        <NavLink to="/login"
                            onClick={() => {
                                handleLogout()
                            }}
                            className={({ isActive }) =>
                                `block rounded-lg px-4 py-2 transition 
                        ${isActive
                                    ? "bg-[rgb(132,93,240)] text-white font-bold"
                                    : "hover:bg-gray-100 text-gray-500"}`
                            }>
                            <div className="flex items-center gap-3">
                                <FiLogOut size="30" color="grey" />
                                <span className="text-xl">Logout</span>
                            </div>
                        </NavLink>
                    </div>
                </div>
            </div>
        </>
    )
};

export default Sidebar;