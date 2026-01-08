import { /*useLocation, useNavigate,*/ useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { ArrowCircleLeft, ArrowLeft, CommandSquare, DirectRight, Like, Like1, Message2 } from "iconsax-react";
import { ToastContainer } from 'react-toastify';
import { fetchUserById } from "../services/UserService";

const UserDetail = () => {
  const { userId } = useParams();
  const [userDetailData, setUserDetailData] = useState(null);
  const [listPostByUser, setPostByUser] = useState([]);
  const navigate = useNavigate();

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
  if (!userDetailData || !listPostByUser) {
    return <div className="flex justify-center items-center h-64">
      <div className="w-12 h-12 border-4 border-blue-500 border-dashed rounded-full animate-spin"></div>
    </div>
      ;
  }

  return (
    <>
      <ArrowLeft size="30" color="#000" onClick={() => navigate(-1)} />
      <div className="flex flex-col border-gray-200 shadow-md rounded-md gap-5 mx-30 p-10">
        <div className="flex-1 flex-col min-h-[200px]">
          <div className="flex ">
            <div className="flex justify-between w-full items-center">
              <div className="flex gap-5 items-center">
                <img
                  className="size-20 rounded-full border-gray-100"
                  src={`http://localhost:8989/api/images/${userDetailData.avatar}`}
                />
                <span className="text-2xl font-bold">{userDetailData.full_name}</span>
              </div>
              <div className="h-10 w-30 border border-green-500 bg-green-100 items-center flex justify-center rounded-md">
                <span className="text-green-500 text-xl font-bold">{userDetailData.status}</span>
              </div>
            </div>
          </div>
        </div>
        <div className="">
          {listPostByUser.map((item, index) => (
            <div key={`post-${index}`} className="border my-10 p-5 border-gray-100 rounded-md">
              <div>
                <div className="flex justify-between items-center">
                  <div className="flex gap-5 items-center">
                    <img
                      className="size-20 rounded-full border-gray-100"
                      src={`http://localhost:8989/api/images/${item.User.avatar}`}
                    />
                    <span className="text-2xl font-bold">{item.User.full_name}</span>
                  </div>
                  <div className="h-10 w-30 border border-green-500 bg-green-100 items-center flex justify-center rounded-md">
                    <span className="text-green-500 text-xl font-bold">{item.status}</span>
                  </div>
                </div>
              </div>

              <div className="h-5 w-15 my-5 border border-green-500 bg-green-100 items-center flex justify-center rounded-md">
                <span className="text-green-500 font-bold">{item.privacy}</span>
              </div>


              <div>
                <span className="text-gray-500 text-2xl font-bold">
                  {item.content}
                </span>
              </div>

              <div className="flex space-x-2 mt-2">
                {item.PostMedia?.length > 0 ? (
                  item.PostMedia.map((media) =>
                    media.media_url ? (
                      <img
                        key={media.id}
                        src={`http://localhost:8989/api/images/${media.media_url}`}
                        alt={`media-${media.id}`}
                        className="w-150 h-150 object-cover rounded flex-shrink-0"
                      />
                    ) : null
                  )
                ) : (
                  <span>Không có ảnh</span>
                )}
              </div>

              <div className="flex justify-between mt-5 flex gap-5 mt-20">
                <div className="flex gap-2 items-center">
                  <Like1 size="30" color="#000" />
                  <span>{item.LikeCount}</span>
                </div>
                <div className="flex gap-2 items-center">
                  <Message2 size="30" color="#000" />
                  <span>{item.CommentCount}</span>

                </div>
                <DirectRight size="30" color="#000" />
              </div>
            </div>
          ))}
        </div>
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
