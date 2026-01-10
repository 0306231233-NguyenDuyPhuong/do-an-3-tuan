import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { fetchPostById, putStatusPost } from "../services/PostService";
import { updateStatusReport } from "../services/ReportService";
import { ArrowCircleLeft, ArrowLeft, CommandSquare, DirectRight, Like, Like1, Message2 } from "iconsax-react";
import { ToastContainer, toast } from 'react-toastify';
import { fetchCommentData } from "../services/CommentService";
import dayjs from "dayjs";

const PostDetail = () => {
  const { postId } = useParams();
  const [postDetailData, setPostDetailData] = useState(null);
  const [commentsList, setCommentsList] = useState([]);
  const [totalComment, setTotalComment] = useState(0);
  const {state} = useLocation();
  const reportId = state?.reportId;
  const statusPost = {
    0: "delete",
    1: "approved"
  }
  const navigate = useNavigate();

  useEffect(() => {
    if (postId) {
      // eslint-disable-next-line react-hooks/immutability
      getPostDetail(postId);
      // eslint-disable-next-line react-hooks/immutability
      getComment(postId)
    }
  }, [postId]);

  const getPostDetail = async (id) => {
    try {
      const res = await fetchPostById(id);
      if (res?.data) {
        setPostDetailData(res.data);
      }
    } catch (error) {
      console.error(error);
    }
  };

  const getComment = async(postId) =>{
    try {
      const res = await fetchCommentData(postId);
      if(res && res.data){
        setCommentsList(res.data.rows)
        setTotalComment(res.data.count)
      }
    } catch (error) {
      console.log("Error: ", error)
    }
  }

  const updateStatusPost = async(postId, status) =>{
    try {
      await putStatusPost(postId, status);
      getPostDetail(postId)
      await updateStatusReport(reportId, 1)
      toast.success("Update status success!")
    } catch (error) {
      alert("Update status post faild, error: ", error)
    }
  }

  if (!postDetailData) {
    return <div>Loading...</div>;
  }
  if(!commentsList){
    return <div>Loading...</div>; 
  }

  return (
    <>
    <div className="flex flex-col gap-5">
       <ArrowLeft size="30" color="black" onClick={()=>navigate(-1)}/>
      <h2 className="font-bold text-2xl">
        {postDetailData.title}
      </h2>
      <div className="flex gap-5">
        <div className="flex-2 min-h-[100px] min-w-[200px] bg-white border border-gray-200 rounded-2xl shadow-md p-10">
          <div className="flex gap-5 items-center justify-between">
            <div className="flex items-center gap-5">
                <div className="size-20 rounded-full flex justify-center items-center">
                <img
                className="size-20 rounded-full border-gray-100"
                src={`http://localhost:8989/api/images/${postDetailData.User.avatar}`}
                />
            </div>
            <div>
                <span className="text-2xl font-bold">
                    {postDetailData.User?.full_name}
                </span>
            </div>
            </div>
            <div className="mt-5 flex justify-between items-center">
            <div className={postDetailData.status == 0? "flex justify-center items-center rounded-md h-10 font-bold w-30 bg-red-100":"flex justify-center items-center rounded-md h-10  w-30 font-bold bg-green-100"}>
                <span className={postDetailData.status == 0? "text-red-500":"text-green-500"} 
                onClick={()=> updateStatusPost(postDetailData.id, postDetailData.status===0? 1:0)}>{statusPost[postDetailData.status]}</span>
            </div>
          </div>
          </div>

        <div className="my-5">
            <span className="text-xl font-bold text-gray-500">{postDetailData.content}</span>
        </div>
          <div className="min-h-[100px] grid grid-cols-2 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {postDetailData.PostMedia.map((img)=>(
                <img 
                className="flex flex-1"
            src={`http://localhost:8989/api/images/${img.media_url}`}/>
            ))}
            
          </div>

          <div className="flex  mt-5 flex gap-10 mt-20">
            <div className="flex gap-2 items-center">
              <Like1 size="30" color="#000"/>
              <span>{postDetailData.like_count}</span>
            </div>
            <div className="flex gap-2 items-center">
               <Message2 size="30" color="#000"/>
              <span>{postDetailData.comment_count}</span>
            </div>
            <div className="flex gap-2 items-center">
               <DirectRight size="30" color="#000"/>
              <span>{postDetailData.share_count}</span>
            </div>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto min-h-[100px] min-w-[200px] bg-white border border-gray-200 rounded-2xl shadow-md p-10">
          <span className="flex items-center justify-center text-2xl font-bold">{totalComment} Comments</span>
          {commentsList.map((item, index)=>(
            <div key={`data-$${index}`}>
              <div className="flex gap-2 mt-10">
                <div className="size-10 rounded-full flex justify-center items-center">
                <img
                className="size-10 rounded-full border-gray-100"
                src={`http://localhost:8989/api/images/${item.User.avatar}`}
                />
            </div>
                <div className="flex flex-col">
                  <span className="text-md text-gray-400 font-bold">{item.User.full_name}</span>
                  <span className="text-black">{item.content}</span>
                  <span className="text-gray-300">{dayjs(item.created_at).format("DD/MM/YYYY")}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
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

export default PostDetail;
