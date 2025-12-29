import { Home } from "iconsax-react";
import { NavLink } from "react-router-dom";

const Sidebar = () =>{
    return (
        <>
        <div className="flex flex-col pt-5 px-5">
            <div className="flex flex gap-1 items-center">
                <div className="h-10 w-10 bg-blue-200 rounded-full"></div>
                <div className="text-blue-500 text-lg font-bold">Name</div>
            </div>
            <div className="flex flex-col p-5 gap-2 border-t border-gray-300 mt-15">
                <span className="my-4">MENU</span>
                <div className="flex flex-col gap-4 ml-2">
                    <NavLink to="/" className={({isActive})=>
                        `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-blue-100 text-blue-600":"hover:bg-gray-50-100"}`
                    }>
                        <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
                        <span className="text-sm">Home</span>
                        </div>
                    </NavLink>
                    <NavLink to="/product" className={({isActive})=>
                     `block rounded-lg px-4 py-2 transition 
                        ${isActive ? "bg-blue-100 text-blue-600":"hover:bg-gray-50-100"}`}>
                    <div className="flex items-center gap-3">
                        <Home size="20" color="#000" />
                        <span className="text-sm">Product</span></div>
                    </NavLink>
                    <div className="flex items-center gap-3"><span>icon</span><span className="text-sm">title</span></div>
                    <div className="flex items-center gap-3"><span>icon</span><span className="text-sm">title</span></div>
                </div>
                <span className="my-4">ORDERS</span>
                <div className="flex flex-col gap-4 ml-2">
                    <div className="flex items-center gap-3"><span>icon</span><span className="text-sm">title</span></div>
                    <div className="flex items-center gap-3"><span>icon</span><span className="text-sm">title</span></div>
                    <div className="flex items-center gap-3"><span>icon</span><span className="text-sm">title</span></div>
                    <div className="flex items-center gap-3"><span>icon</span><span className="text-sm">title</span></div>
                </div>
            </div>
        </div>
        </>
    )
};

export default Sidebar;