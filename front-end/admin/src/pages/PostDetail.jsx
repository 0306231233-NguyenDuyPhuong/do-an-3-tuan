import { useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { fetchPostById } from "../services/PostService";
import { ArrowCircleLeft, CommandSquare, DirectRight, Like, Like1, Message2 } from "iconsax-react";

const PostDetail = () => {
  const { postId } = useParams();
  const [postDetailData, setPostDetailData] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (postId) {
      // eslint-disable-next-line react-hooks/immutability
      getPostDetail(postId);
    }
  }, [postId]);

  const getPostDetail = async (id) => {
    try {
      const res = await fetchPostById(id);
      if (res?.data) {
        setPostDetailData(res.data);
        console.log("Post detail:", res.data);
      }
    } catch (error) {
      console.error(error);
    }
  };

  if (!postDetailData) {
    return <div>Loading...</div>;
  }

  return (
    <>
    <div className="flex flex-col gap-5">
       <ArrowCircleLeft size="40" color="#6F45E6" onClick={()=>navigate(-1)}/>
      <h2 className="font-bold text-2xl">
        {postDetailData.title}
      </h2>
      <div className="flex gap-5">
        <div className="flex-2 min-h-[100px] min-w-[200px] bg-white border border-gray-200 rounded-2xl shadow-md p-10">
          <div className="flex gap-5 items-center justify-between">
            <div className="flex items-center gap-5">
                <div className="size-20 border border-b-gray-100 rounded-full flex justify-center items-center">
                <img
                className="size-20 border rounded-full border-b-gray-100"
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
            <div className={postDetailData.status == "delete"? "flex justify-center items-center rounded-md h-10 font-bold w-30 bg-red-100":"flex justify-center items-center rounded-md h-10  w-30 font-bold bg-green-100"}>
                <span className={postDetailData.status == "delete"? "text-red-500":"text-green-500"} onClick={()=>console.log(">>>>>>>>>CLicek")}>{postDetailData.status}</span>
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

          <div className="flex justify-between mt-5 flex gap-5 mt-20">
            <div className="flex gap-2 items-center">
              <Like1 size="30" color="#000"/>
              <span>{postDetailData.LikeCount}</span>
            </div>
            <div className="flex gap-2 items-center">
               <Message2 size="30" color="#000"/>
              <span>{postDetailData.CommentCount}</span>

            </div>
            <DirectRight size="30" color="#000"/>
          </div>
        </div>

        <div className="flex-1 min-h-[100px] min-w-[200px] bg-white border border-gray-200 rounded-2xl shadow-md p-10">
          <h1>{postDetailData.status}</h1>
        </div>
      </div>
    </div>
    </>
  );
};

export default PostDetail;
