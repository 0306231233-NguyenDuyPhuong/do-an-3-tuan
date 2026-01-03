import { useEffect, useState } from "react";
import { fetchPostAdmin } from "../services/PostService";
import { Eye } from "iconsax-react";
import { NavLink, Outlet } from "react-router-dom";

const Post = () =>{
    const [listPosts, setListPosts] = useState([]);

    useEffect(()=>{
        // eslint-disable-next-line react-hooks/immutability
        getPostAdmin(1)
    }, []);

    const getPostAdmin = async(page)=>{
        let res = await fetchPostAdmin(page);
        if(res && res.data){
            setListPosts(res.data)
        }
    }
    return (
        <>
        <div>
<table className="min-w-full border border-gray-200 shadow-md">
            <thead className="">
                <th className="text-2xl h-15 border border-gray-200">Id</th>
                <th className="text-2xl h-15 border border-gray-200">Avatar</th>
                <th className="text-2xl h-15 border border-gray-200">Full name</th>
                <th className="text-2xl h-15 border border-gray-200">Status</th>
                <th className="text-2xl h-15 border border-gray-200">Action</th>
            </thead>
            <tbody className="items-center">
                {listPosts.map((item, index)=>{
                    return(
                        <tr key={`data-${index}`}>
                            <td className="h-15 border border-gray-200 text-center text-2xl">{item.id}</td>
                            <td className="flex items-center justify-center h-15 border border-gray-200 text-2xl">
                                <div className="size-12 bg-amber-400 rounded-full">
                                    <img 
                                    className="size-12 rounded-full"
                                    src={`http://localhost:8989/api/images/${item.User.avatar}`} alt="" />
                                </div>
                                </td>
                            <td className="h-15 border border-gray-200 text-center text-2xl">{item.User.full_name}</td>
                            <td className= "h-15 p-2 border border-gray-200 text-center">
                                <div className="text-center h-10 w-30">
                                <div className={item.status === "delete"?"border rounded-2xl bg-red-500 text-center font-bold text-white":"border rounded-2xl bg-green-600 text-center font-bold text-white"}>
                                {item.status}
                                </div>
                                </div>

                            </td>
                            <td className="border border-gray-200">
                                <div
                                className="flex items-center justify-center">
                                    <NavLink to = {`/post/${item.id}`}>
                                    <Eye size="30" color="#000"/>
                                    </NavLink>
                                </div>
                            </td>
                        </tr>
                    )
                })}
            </tbody>
        </table>
        <Outlet/>
        </div>
        
        </>
    )
}

export default Post;