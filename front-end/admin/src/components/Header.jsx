import { Notification, SearchNormal } from "iconsax-react";
import avarta from "../images/avatar.png"
const Header = () =>{
    return (
        <>
            <div className="flex justify-between items-center">
                <div className="w-80 h-11 border rounded-xl border-gray-300
                p-2 flex
                ">
                    <SearchNormal size="20" color="#000" className="mr-2"/>
                    <div>
                        <input type="text" placeholder="Search" className="border-none outline-none" />
                    </div>
                </div>
                <div className="flex items-center">
                    <Notification size="30" color="#000" className="mr-2"/>
                    <div className="flex gap-2 items-center">
                        <div className="size-12 rounded-full p-1 border border-gray-200">
                            <img src={avarta}
                            className="w-full rounded-full"
                            />
                        </div>
                        <div className="flex flex-col">
                            <div>item1</div>
                            <div>item2</div>
                        </div>
                    </div>    
                </div>
            </div>
        </>
    )
};

export default Header;