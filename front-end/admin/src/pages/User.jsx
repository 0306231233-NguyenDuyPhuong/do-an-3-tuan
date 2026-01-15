import { useEffect, useState } from "react";
import { Eye, Warning2 , Notification} from "iconsax-react";
import { NavLink, Outlet } from "react-router-dom";
import ReactPaginate from 'react-paginate';
import { CiSearch } from "react-icons/ci";
import { fetchUser } from "../services/UserService";
const User = () =>{
    const [listUsers, setListUsers] = useState([]);
    let [total, setTotatl] = useState(null);
    const statusPost = {
      2: "banned", 
      1: "approved"
    }
    const roleUser = {
      0: "user", 
      1: "admin"
    }
    //const [sort, setSort] = useState("");
    const [role, setRole] = useState("");
    const [search, setSearch] = useState("");
    useEffect(() => {
        // eslint-disable-next-line react-hooks/immutability
        getUser({page:1})
    }, []);

    const getUser = async ({
      page,
      search, 
      role,
      status
    }={}) => {
        try {
          let res = await fetchUser({
            page, 
            search, 
            role,
            status
          });
          if (res && res.data) {
              setListUsers(res.data.data);
              setTotatl(res.total);
          }
        } catch (error) {
          console.log("Get user error: ", error)
        }
    }

    const handlePageClick = (event) =>{
      console.log(">>> seleted: ",event)
      getUser(+event.selected+1);
    }

    if(!listUsers){
        return <>
        <div>Loading...</div>
        </>
    }
    return (
        <>
                <div className="flex gap-5">
                    <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
                    <div className="flex justify-between items-center">
                      <div className="flex gap-4">
                        <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                          <Notification size="25" color="#000"/>
                        </div>
                        <div className="text-xl font-bold"> Quantity Post</div>
                      </div>
                      <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                        <Warning2 size="20" color="#000"/>
                      </div>
                    </div>
                    <div className="flex gap-2 items-center justify-between mt-5">
                      <div className="flex items-center">
                        <span className="text-5xl font-bold mr-2 text-green-400">{total}</span>
                        <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 flex justify-center items-center">tang</span>
                      </div>
                      <div className="text-xl text-gray-400">
                        vs last week
                      </div>
                    </div>
                  </div>
                <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
                    <div className="flex justify-between items-center">
                      <div className="flex gap-4">
                        <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                          <Notification size="25" color="#000"/>
                        </div>
                        <div className="text-xl font-bold"> Quantity user</div>
                      </div>
                      <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                        <Warning2 size="20" color="#000"/>
                      </div>
                    </div>
                    <div className="flex gap-2 items-center justify-between mt-5">
                      <div className="flex items-center">
                        <span className="text-5xl font-bold mr-2 text-green-500">129</span>
                        <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-400 flex items-center justify-center">tang</span>
                      </div>
                      <div className="text-xl text-gray-400">
                        vs last week
                      </div>
                    </div>
                  </div>
                <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
                    <div className="flex justify-between items-center">
                      <div className="flex gap-4">
                        <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                          <Notification size="25" color="#000"/>
                        </div>
                        <div className="text-xl font-bold"> Quantity user</div>
                      </div>
                      <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                        <Warning2 size="20" color="#000"/>
                      </div>
                    </div>
                    <div className="flex gap-2 items-center justify-between mt-5">
                      <div className="flex items-center">
                        <span className="text-5xl font-bold mr-2 text-green-400">129</span>
                        <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 p-1 flex justify-center items-center">tang</span>
                      </div>
                      <div className="text-xl text-gray-400">
                        vs last week
                      </div>
                    </div>
                  </div>
                </div>


        <div className="flex my-10 border border-gray-100 rounded-md 
        p-5 gap-10 justify-center items-center">
          <div className="flex flex-3 items-center justify-start
          border-gray-300 rounded-2xl h-20 border gap-5 p-5 shadow-md">
            <div>
              <CiSearch size={30}/>
            </div>
            <div className="">
              <input className="flex h-10 w-200 flex-1 outline-none text-2xl" 
              placeholder="Search post" type="text" name="search"
              value={search}
              onChange={(e)=>{
                setSearch(e.target.value)
                getUser({page:1, search:search})
              }}
              />
            </div>
          </div>
          <div className="flex flex-1 items-center justify-center">
            <div className="flex h-20
            rounded-md border border-gray-300 shadow-md">
              <select className="outline-none 
              my-5
              text-2xl" 
              value={role}
              onChange={(e)=>{
                const value = Number(e.target.value);
                setRole(value);
                if(value <2 && value>-1){
                  getUser({page:1, role: value})
                }
                else{
                  if(value == 3)
                  getUser({page:1, status: 1})
                  else getUser({page:1, status: 2})
                    
                }
              }}
              >
              <option value="">status</option>
              <option value="1">admin</option>
              <option value="0">user</option>
              <option value="3">active</option>
              <option value="4">banned</option>
            </select>
          </div>

            </div>
        </div>

        <div className="flex border p-6 border-gray-200 rounded-2xl shadow-md overflow-x-auto mt-10">
        <table className="min-w-full border-collapse">
            <thead className="bg-gray-100">
            <tr>
                <th className="text-2xl h-15 px-4 py-2 text-center">Id</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Avatar</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Full name</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Role</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Status</th>
                <th className="text-2xl h-15 px-4 py-2 text-center">Action</th>
            </tr>
            </thead>
            <tbody>
            {listUsers.map((item, index) => (
                <tr key={`data-${index}`} className="hover:bg-gray-50">
                <td className="h-15 px-4 py-2 text-center text-2xl text-gray-400">{item.id}</td>
                <td className="h-15 px-4 py-2 flex items-center justify-center">
                    <div className="w-12 h-12 bg-amber-400 rounded-full overflow-hidden">
                    <img
                        className="w-12 h-12 object-cover rounded-full"
                        src={`http://localhost:8989/api/images/${item.avatar}`}
                        alt={item.full_name}
                    />
                    </div>
                </td>
                <td className="h-15 px-4 py-2 text-center text-2xl text-gray-400">{item.full_name}</td>
                <td className="h-15 px-4 py-2 text-center">
                    <div
                    className={`inline-block px-3 py-1 rounded-md font-bold ${
                        item.status === 0
                        ? "bg-red-100 text-red-500 border border-red-300"
                        : "bg-green-100 text-green-500 border border-green-300"
                    }`}
                    >
                    {roleUser[item.role]}
                    </div>
                </td>
                <td className="h-15 px-4 py-2 text-center">
                    <div
                    className={`inline-block px-3 py-1 rounded-md font-bold ${
                        item.status === 2
                        ? "bg-yellow-100 text-yellow-500 border border-yellow-300"
                        : "bg-green-100 text-green-500 border border-green-300"
                    }`}
                    >
                    {statusPost[item.status]}
                    </div>
                </td>
                <td className="h-15 px-4 py-2 text-center">
                    <NavLink to={`/user/${item.id}`} className="hover:text-blue-500">
                    <Eye size="30" color="#C0C0C0" />
                    </NavLink>
                </td>
                </tr>
            ))}
            </tbody>
        </table>
        <Outlet />
        </div>
      <ReactPaginate
        pageCount={Math.ceil(total / 10)}
        onPageChange={handlePageClick}
        containerClassName="flex justify-center gap-2 mt-4"
        pageLinkClassName="px-3 py-1 border rounded hover:bg-gray-100"
        previousLinkClassName="px-3 py-1 border rounded"
        nextLinkClassName="px-3 py-1 border rounded"
        activeLinkClassName="bg-black text-white"
      />


        </>
    )
}

export default User;