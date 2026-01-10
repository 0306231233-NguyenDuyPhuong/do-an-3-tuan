import { EmojiNormal, Notification, SearchNormal, Share } from "iconsax-react";
import { IoShareSocialOutline } from "react-icons/io5";
import { LuMessageCircleMore, LuLayoutDashboard } from "react-icons/lu";

const Header = () => {
    return (
        <>
            <div className="flex flex-col h-20">
                <div className="flex justify-between items-center px-10">
                    <div className="flex items-center gap-2">
                        <LuLayoutDashboard size="25" color="#000" />
                        <div className="text-2xl font-bold">Darshboard</div>
                    </div>
                    <div className="flex items-center">
                        <div className="w-50 h-13 border rounded-xl border-gray-300
                p-3 flex shadow-md mr-5 border-r
                ">
                            <div className="flex">
                                <SearchNormal size="20" color="#6F45E6" />
                                <input type="text" placeholder="Search All Mode" className="border-none outline-none" />
                            </div>
                        </div>

                        <div className="w-12 h-12 border rounded-md p-3 border-gray-200 shadow-md mr-4">
                            <Notification size="25" color="#000" />
                        </div>
                        <div className="w-12 h-12 border rounded-md p-3 border-gray-200 shadow-md mr-4">
                            <LuMessageCircleMore size="25" color="#000" />
                        </div>
                        <div className="w-12 h-12 border rounded-md p-3 border-gray-200 shadow-md mr-4">
                            <IoShareSocialOutline size="25" color="#000" />
                        </div>
                    </div>
                </div>


                <div className="flex justify-between items-center mt-5 border-y border-gray-200 py-5 px-10">
                    <div className="text-md text-green-400 font-bold">Last updated now</div>

                    <div className="flex items-center justify-between gap-4">
                        <div className="flex flex-row w-50 h-13 border border-gray-200 shadow-md rounded-md justify-center items-center">
                            <Notification size="25" color="#000" />
                            <div>Customize Wiget</div>
                        </div>

                        <div className="flex items-center justify-between">
                            <div className="flex flex-row w-50 h-13 border border-gray-200 shadow-md rounded-md justify-center items-center">
                                <Notification size="25" color="#000" />
                                <div>Customize Wiget</div>
                            </div>
                        </div>

                        <div className="flex items-center justify-between">
                            <div className="flex flex-row w-50 h-13 bg-[rgb(111,69,230)] border border-gray-200 shadow-md rounded-md justify-center items-center">
                                <Notification size="25" color="#000" />
                                <div className="text-white">Customize Wiget</div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>

        </>
    )
};

export default Header;