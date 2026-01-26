import { useEffect, useState } from "react";
import { fetchPostAdmin } from "../services/PostService";
import { Eye, Warning2, Notification } from "iconsax-react";
import { NavLink, Outlet } from "react-router-dom";
import ReactPaginate from 'react-paginate';
import { CiSearch } from "react-icons/ci";
import Calenda from "../components/Calenda"
const Post = () => {
  const [listPosts, setListPosts] = useState([]);
  let [total, setTotatl] = useState(null);
  const [date, setDate] = useState("")
  const [dateEnd, setDateEnd] = useState("")
  const statusPost = {
    0: "delete",
    1: "approved"
  }
  const [sort, setSort] = useState("");
  const [search, setSearch] = useState("");
  useEffect(() => {
    // eslint-disable-next-line react-hooks/immutability
    getPostAdmin({page: 1})
  }, []);

  const getPostAdmin = async ({
    page = 1,
    search = "",
    sort = "",
    date, 
    status,
    dateStart, 
    dateEnd
  } = {}) => {
    try {
      const res = await fetchPostAdmin({
      page,
      search, 
      sort,
      date, 
      status,
      dateStart,
      dateEnd
    });
    setTotatl(res.total);
    if (res && res.data) {
      setListPosts(res.data)
    }
    } catch (error) {
      console.log("Fetch admin post error: ",error)
    }
  }

  const handlePageClick = (event) => {
    const selectedPage = event.selected +1;
    getPostAdmin({
      page: selectedPage,
      search,
      sort, 
      date, 
      dateStart: date, 
      dateEnd
    })
    
  }

  return (
    <>
      <div className="flex gap-5">
        <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
          <div className="flex justify-between items-center">
            <div className="flex gap-4">
              <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                <Notification size="25" color="#000" />
              </div>
              <div className="text-xl font-bold"> Quantity Post</div>
            </div>
            <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
              <Warning2 size="20" color="#000" />
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
                <Notification size="25" color="#000" />
              </div>
              <div className="text-xl font-bold"> Quantity user</div>
            </div>
            <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
              <Warning2 size="20" color="#000" />
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
                <Notification size="25" color="#000" />
              </div>
              <div className="text-xl font-bold"> Quantity user</div>
            </div>
            <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
              <Warning2 size="20" color="#000" />
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
            <CiSearch size={30} />
          </div>
          <div className="">
            <input className="flex h-10 w-200 flex-1 outline-none text-2xl"
              placeholder="Search post" type="text" name="search"
              value={search}
              onChange={(e) => {
                const value = e.target.value;
                setSearch(value)
                getPostAdmin({page: 1, search: search})
              }}
            />
          </div>
        </div>

        <div className="flex gap-5">
          <div className="flex
            rounded-md border border-gray-300 shadow-md">
            <select className="outline-none 
              my-5
              text-2xl"
              value={sort}
              onChange={(e) => {
                const value = e.target.value;
                setSort(value);
                if(value == "trending"){
                  getPostAdmin({page: 1, sort: value})}
                else{
                  getPostAdmin({page: 1, status: Number(value)})
                }
              }}
            >
              <option value="">date</option>
              <option value="trending">Trending</option>
              <option value="1">approved</option>
              <option value="0">delete</option>
            </select>
          </div>
              <div className="flex flex-col items-center justify-center h-18 border px-2 border-gray-300 rounded-md shadow-md">
                <input className="outline-none text-xl" 
                value={date}
                onChange={(e)=>{
                  const value = e.target.value;
                  setDate(value)         ;         
                  getPostAdmin({page:1, date: date})
                }}
                type="date"
                />
              </div>
              <div className="flex items-center justify-center h-18 border px-2 border-gray-300 rounded-md shadow-md">
                <input className="outline-none text-xl"
                value={dateEnd}
                onChange={(e)=>{
                  const value = e.target.value;
                  setDateEnd(value)                  
                  getPostAdmin({page: 1, dateStart: date, dateEnd: dateEnd})
                }}
                type="date"/>
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
              <th className="text-2xl h-15 px-4 py-2 text-center">Status</th>
              <th className="text-2xl h-15 px-4 py-2 text-center">Action</th>
            </tr>
          </thead>
          <tbody>
            {listPosts.map((item, index) => (
              <tr key={`data-${index}`} className="hover:bg-gray-50">
                <td className="h-15 px-4 py-2 text-center text-2xl text-gray-400">{item.id}</td>
                <td className="h-15 px-4 py-2 flex items-center justify-center">
                  <div className="w-12 h-12 bg-amber-400 rounded-full overflow-hidden">
                    <img
                      className="w-12 h-12 object-cover rounded-full"
                      src={`http://localhost:8989/api/images/${item.User.avatar}`}
                      alt={item.User.full_name}
                    />
                  </div>
                </td>
                <td className="h-15 px-4 py-2 text-center text-2xl text-gray-400">{item.User.full_name}</td>
                <td className="h-15 px-4 py-2 text-center">
                  <div
                    className={`inline-block px-3 py-1 rounded-md font-bold ${item.status === 0
                        ? "bg-red-100 text-red-500 border border-red-300"
                        : "bg-green-100 text-green-500 border border-green-300"
                      }`}
                  >
                    {statusPost[item.status]}
                  </div>
                </td>
                <td className="h-15 px-4 py-2 text-center">
                  <NavLink to={`/post/${item.id}`} className="hover:text-blue-500">
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

export default Post;