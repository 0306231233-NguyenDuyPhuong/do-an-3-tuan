import { Home } from "iconsax-react";
import { NavLink } from "react-router-dom";
import avatar from "../images/avatar.png"
const Sidebar = () =>{
    const user = JSON.parse(localStorage.getItem("user"))
    const full_name = user?.full_name;
    const email = user?.email
    return (
        <>
        <div className="flex flex-col pt-5 px-5">
            <div className="flex flex gap-1 items-center gap-5">
                <div className="h-12 w-12 bg-blue-200 rounded-full"></div>
                <div className="flex flex-col">
                <div className="text-black text-xl font-bold">{full_name}</div>
                <div className="text-gray-320 text-sm">Pivora</div>
                </div>
            </div>

            <div className="flex flex-row h-13 w-80 justify-between border bg-gray-200 border-none rounded-xl
            mt-10 px-3 py-1 items-center
            ">
                <div className="w-10 h-10 bg-gray-300 rounded-full items-center">
                    <img src={avatar} className="w-full rounded-full"/>
                </div>
                <div>{email}</div>
                <div>item3</div>
            </div>
            <div className="flex flex-col p-5 gap-2 border-gray-300 border-t mt-5">
                <div className="flex flex-col gap-4">
                    <NavLink to="/" className={({isActive})=>
                        `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-gray-200 text-black font-bold":"hover:bg-gray-50-100 text-2xl"}`
                    }>
                        <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
                        <span className="text-xl">Dashboard</span>
                        </div>
                    </NavLink>

                    <NavLink to="/post" className={({isActive})=>
                        `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-gray-200 text-black font-bold":"hover:bg-gray-50-100 text-2xl"}`
                    }>
                        <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
                        <span className="text-xl">Post</span>
                        </div>
                    </NavLink>
                    <NavLink to="/user" className={({isActive})=>
                        `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-gray-200 text-black font-bold":"hover:bg-gray-50-100 text-2xl"}`
                    }>
                        <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
                        <span className="text-xl">User</span>
                        </div>
                    </NavLink>

                    <NavLink to="/comment" className={({isActive})=>
                        `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-gray-200 text-black font-bold":"hover:bg-gray-50-100 text-2xl"}`
                    }>
                        <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
                        <span className="text-xl">Comment</span>
                        </div>
                    </NavLink>
                    <NavLink to="/report" className={({isActive})=>
                        `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-gray-200 text-black font-bold":"hover:bg-gray-50-100 text-2xl"}`
                    }>
                        <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
                        <span className="text-xl">Report</span>
                        </div>
                    </NavLink>
                    <NavLink to="/login" className={({isActive})=>
                        `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-gray-200 text-black font-bold":"hover:bg-gray-50-100 text-2xl"}`
                    }>
                        <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
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