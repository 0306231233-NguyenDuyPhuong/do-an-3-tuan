import { /*useLocation, useNavigate,*/ useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { ArrowCircleLeft, ArrowLeft, CommandSquare, DirectRight, Like, Like1, Message2 } from "iconsax-react";
import { ToastContainer, toast } from 'react-toastify';
import { fetchUserById, putStatusUser } from "../services/UserService";

const UserDetail = () => {
  const { userId } = useParams();
  const [userDetailData, setUserDetailData] = useState(null);
  const [listPostByUser, setPostByUser] = useState([]);
  const navigate = useNavigate();
  const privacyMap = {
    0: "public",
    1: "friends",
    2: "private"
  }

  const statusMap = {
    1: "active",
    2: "banned"
  }

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
      console.log(">>>>>>>>>>>>>>>>",res)
      if (res) {
        setUserDetailData(res.data.user);
        setPostByUser(res.data.post)
      }
    } catch (error) {
      console.error(error);
    }
  };

  const updateStautusUser = async(id, status) =>{
    console.log(">>>>>>>>", id, status)
    await putStatusUser(id, status)
    await getUserDetail(userId)
    toast.success("Update status success!")
  }
  if (!userDetailData || !listPostByUser) {
    return <div className="flex justify-center items-center h-64">
      <div className="w-12 h-12 border-4 border-blue-500 border-dashed rounded-full animate-spin"></div>
    </div>
      ;
  }

  return (
  <>

    <div className="max-w-4xl mx-auto p-6 flex flex-col gap-6 shadow-md rounded-md">
      <ArrowLeft size="30" color="black" onClick={()=>navigate(-1)}/>
      {/* USER INFO */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <img
            className="w-16 h-16 rounded-full object-cover"
            src={`http://localhost:8989/api/images/${userDetailData.avatar}`}
          />
          <span className="text-xl font-semibold">
            {userDetailData.full_name}
          </span>
        </div>

        <span className= {userDetailData.status === 1?"px-4 py-1 rounded-md bg-green-100 text-green-600 font-semibold":"px-4 py-1 rounded-md bg-yellow-100 text-yellow-600 font-semibold"}
        onClick={()=>{
          const status = userDetailData.status==1?2:1;
          updateStautusUser(userId, status)
        }}
        >
          {statusMap[userDetailData.status]}
        </span>
      </div>

      {/* POSTS */}
      <div className="flex flex-col gap-6">
        {listPostByUser.map((item, index) => (
          <div
            key={`post-${index}`}
            className="border border-gray-200 rounded-lg p-5"
          >
            {/* POST HEADER */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <img
                  className="w-12 h-12 rounded-full object-cover"
                  src={`http://localhost:8989/api/images/${item.User.avatar}`}
                />
                <span className="font-semibold">
                  {item.User.full_name}
                </span>
              </div>

              <span className="px-3 py-1 rounded bg-green-100 text-green-600 text-sm font-medium">
                {statusMap[item.status]}
              </span>
            </div>

            {/* PRIVACY */}
            <div className="inline-block mt-3 px-3 py-1 rounded bg-green-100 text-green-600 text-sm font-medium">
              {privacyMap[item.privacy]}
            </div>

            {/* CONTENT */}
            <p className="mt-3 text-gray-700 font-bold">
              {item.content}
            </p>

            {/* MEDIA */}
            {item.PostMedia?.length > 0 ? (
              <div className="grid grid-cols-2 gap-3 mt-4">
                {item.PostMedia.map(
                  media =>
                    media.media_url && (
                      <img
                        key={media.id}
                        src={`http://localhost:8989/api/images/${media.media_url}`}
                        className="w-full h-48 object-cover rounded"
                      />
                    )
                )}
              </div>
            ) : (
              <p className="mt-3 text-sm text-gray-400">Không có ảnh</p>
            )}

            {/* ACTIONS */}
            <div className="flex justify-around items-center mt-5 pt-4 border-t">
              <div className="flex items-center gap-2">
                <Like1 size={24} />
                <span>{item.LikeCount}</span>
              </div>

              <div className="flex items-center gap-2">
                <Message2 size={24} />
                <span>{item.CommentCount}</span>
              </div>

              <DirectRight size={24} />
            </div>
          </div>
        ))}
      </div>
    </div>

    <ToastContainer
      position="top-left"
      autoClose={5000}
      pauseOnHover
      draggable
    />
  </>
);

};

export default UserDetail;
