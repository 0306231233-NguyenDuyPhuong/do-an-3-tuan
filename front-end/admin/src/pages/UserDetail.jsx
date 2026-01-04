import { /*useLocation, useNavigate,*/ useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { ArrowCircleLeft, CommandSquare, DirectRight, Like, Like1, Message2 } from "iconsax-react";
import { ToastContainer } from 'react-toastify';
import { fetchUserById } from "../services/UserService";

const UserDetail = () => {
  const { userId } = useParams();
  const [userDetailData, setUserDetailData] = useState(null);
  const [listPostByUser, setPostByUser] = useState([]);
  //const {state} = useLocation();
  //const reportId = state?.reportId;
 // const navigate = useNavigate();

  useEffect(() => {
    if (userId) {
      // eslint-disable-next-line react-hooks/immutability
      getUserDetail(userId);
    }
  }, [userId]);

  const getUserDetail = async (userId) => {
    try {
      const res = await fetchUserById(userId);
      console.log(res)
      if (res) {
        setUserDetailData(res.user);
        setPostByUser(res.post)
        console.log(">>>>>USER Data: ", userDetailData)
        console.log(">>>>>USER Data: ", listPostByUser)
      }
    } catch (error) {
      console.error(error);
    }
  };

  /*const updateStatusPost = async(postId, status) =>{
    try {
      await putStatusPost(postId, status);
      getPostDetail(postId)
      await updateStatusReport(reportId, "resolved")
      toast.success("Update status success!")
    } catch (error) {
      alert("Update status post faild, error: ", error)
    }
  }*/
/*
if (!userDetailData) {
  return (
    <div className="flex justify-center items-center h-64">
      <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
    </div>
  );
}
if (!listPostByUser) {
  return (
    <div className="flex justify-center items-center h-64">
      <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
    </div>
  );
}*/

  return (
    <>
    <div className="flex justify-between border-gray-200 shadow-md rounded-md gap-5">
        <div className="flex flex-1 min-h-[200px] border p-10">
            <div className="flex flex-row justify-between">
                <div className="flex">
                    <div className="flex items-center gap-5">
                <img
                className="size-20 rounded-full border-gray-100"
                src={`http://localhost:8989/api/images/${userDetailData.avatar}`}
                />
                    <span>{userDetailData.full_name}</span>
                </div>
                </div>
                    
                <div>fg</div>
            </div>
        </div>
        <div className="flex flex-1 min-h-[200px] border p-10">item1</div>
    </div>

    
    <ToastContainer
      position="top-left"
      autoClose={5000}
      hideProgressBar={false}
      newestOnTop={false}
      closeOnClick={false}
      rtl={false}
      pauseOnFocusLoss
      draggable
      pauseOnHover
    />
    </>
  );
};

export default UserDetail;
