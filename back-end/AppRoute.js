import express from "express";
const router = express.Router();
import ImageController from "./controllers/ImageController.js";
import ValidateImageExists from "./middledewares/ValidateImageExists.js";
import uploadImage from "./middledewares/ImageUpload.js";
import UserController from "./controllers/UserController.js";
import chartDashboard from "./controllers/DashboardController.js";
import AuthController from "./controllers/AuthController.js";
import LocationController from "./controllers/LocationController.js";
import PostController from "./controllers/PostController.js";
import PostMediaController from "./controllers/PostMediaController.js";
import ReportController from "./controllers/ReportController.js";
import ReportActionController from "./controllers/ReportActionController.js";
//<<<<<<< HEAD
import AsyncHandler from "./middledewares/AsyncHandler.js";
import InsertLocationRequest from "./dtos/requests/location/InsertLocatioinRequest.js";
import InsertPostRequest from "./dtos/requests/post/InsertPostRequest.js";
import InsertReportActionRequest from "./dtos/requests/report-action/InsertReportActionRequest.js";
import InsertReportRequest from "./dtos/requests/report/InsertReportRequest.js";
import InsertPostMediaRequest from "./dtos/requests/post-media/InsertPostMediaRequest.js";
import UpdateLocationRequest from "./dtos/requests/location/UpdateLocationRequest.js";
import UpdatePostRequest from "./dtos/requests/post/UpdatePostRequest.js";
import UpdateReportRequest from "./dtos/requests/report/UpdateReportRequest.js";
import UpdateReportActionRequest from "./dtos/requests/report-action/UpdateReportActionRequest.js";
import UpdatePostAdminRequest from "./dtos/requests/post/UpdatePostAdminRequest.js";
import validate from "./middledewares/Validate.js";
import verifyToken from "./middledewares/verifyToken.js";
import FriendController from "./controllers/FriendController.js";
import validateImageExists from "./middledewares/ValidateImageExists.js";
import DashboardController from "./controllers/DashboardController.js";
import checkCanLike from "./middledewares/CheckCanLike.js";
import InteractController from "./controllers/InteractController.js";
import checkCanShare from "./middledewares/CheckCanShare.js";
import checkCanComment from "./middledewares/CheckComment.js";
import CommentController from "./controllers/CommentController.js";
import checkBlocked from "./middledewares/checkBlocked.js";
import checkBlockedPost from "./middledewares/checkBlockedPost.js";

const AppRoute = (app) => {
  router.get("/user", AuthController.getUser);
  //Auth
  router.post("/auth/login", AuthController.login);
  router.post("/auth/register", AuthController.register);
  router.get("/auth/verify", AuthController.verifyEmail);
  router.post("/auth/resend", AuthController.resendEmail);

  router.post("/auth/refresh", AuthController.refresh);
  router.post("/auth/logout", AuthController.logout);
  router.post("/auth/forgot-password", AuthController.forgotPassword);
  router.get("/auth/reset-password", AuthController.resetPassword);

  //User
  router.get(
    "/user/get-profile",
    checkBlocked,
    verifyToken,
    UserController.getProfile
  );
  router.get("/users/:id", verifyToken, UserController.getUserById);
  router.get("/admin/users/:id", verifyToken, UserController.getAdminUserById);
  router.get("/users", verifyToken, UserController.getUsers);
  router.patch("/user/update", verifyToken, UserController.update);
  router.put("/admin/user/:id",
    verifyToken, 
    UserController.putUserAdmin
  )

  //INTERACT
  router.post(
    "/interact/like",
    verifyToken,
    checkBlockedPost,
    checkCanLike,
    InteractController.likePost
  );
  router.delete(
    "/interact/like",
    verifyToken,
    checkBlockedPost,
    InteractController.unlikePost
  );
  router.post(
    "/interact/share",
    verifyToken,
    checkBlockedPost,
    checkCanShare,
    InteractController.sharePost
  );
  router.delete(
    "/interact/share",
    verifyToken,
    checkBlockedPost,
    InteractController.unsharePost
  );
  //router.get("/interact/count/:postId", InteractController.getCount);

  //Comment
  router.get(
    "/comments/:postId",
    verifyToken,
    CommentController.getCommentsPost
  );
  router.get(
    "/comments/admin/:postId",
    verifyToken,
    CommentController.getCommentsAdmin
  );
  router.post(
    "/comment",
    verifyToken,
    checkBlocked,
    checkCanComment,
    CommentController.create
  );
  router.patch("/comment", verifyToken, CommentController.updateComment);
  router.put("/comment/admin/:id", 
    verifyToken,
    CommentController.putCommentAdmin
  )
  router.delete(
    "/comment/:commentId",
    verifyToken,
    CommentController.deleteComment
  );

  //Friend
  router.post(
    "/friends/requests",
    verifyToken,
    checkBlocked,
    FriendController.sendFriendRequest
  );
  router.get(
    "/friends/requests",
    verifyToken,
    FriendController.getFriendRequests
  );
  router.get("/friends", verifyToken, FriendController.getFriends);
  router.patch(
    "/friends/requests/accept",
    verifyToken,
    checkBlocked,
    FriendController.acceptFriendRequest
  );
  router.patch(
    "/friends/requests/reject",
    verifyToken,
    checkBlocked,
    FriendController.rejectFriendRequest
  );
  router.post(
    "/friends/requests/cancel",
    verifyToken,
    checkBlocked,
    FriendController.cancelFriendRequest
  );
  router.post(
    "/friends/unfriend",
    verifyToken,
    checkBlocked,
    FriendController.unFriend
  );

  router.patch("/friends/unblock", verifyToken, FriendController.unBlockUser);
  router.get(
    "/friend/status/:friendId",
    verifyToken,
    FriendController.isFriend
  );
  //FOLLOW
  router.get("/follow/follower", verifyToken, FriendController.getFollower);
  router.get("/follow/following", verifyToken, FriendController.getFollowing);
  router.post(
    "/follow/follow",
    verifyToken,
    checkBlocked,
    FriendController.follow
  );
  router.post(
    "/follow/unfollow",
    verifyToken,
    checkBlocked,
    FriendController.unFollow
  );

  //Location
  router.get("/locations", verifyToken, LocationController.getLocation);
  router.get("/locations/:id", LocationController.getLocationById);
  router.post(
    "/locations",
    validate(InsertLocationRequest),
    verifyToken,
    AsyncHandler(LocationController.postLocation)
  );
  router.put(
    "/locations/:id",
    verifyToken,
    validate(UpdateLocationRequest),
    AsyncHandler(LocationController.putLocation)
  );
  router.delete(
    "/locations/:id",
    verifyToken,
    LocationController.deleteLocation
  );

  // Chart dashboard
  router.get(
    "/admin/dashbord/statistics",
    verifyToken,
    DashboardController.getChartDashboard
  ),
    router.get("/admin/dashboard/count", DashboardController.getCountDashboard);
  // Post
  router.get("/posts/users", verifyToken, PostController.getPostUser);
  router.get("/posts/admin", verifyToken, PostController.getPostAdmin);
  router.get("/posts/:id", verifyToken, PostController.getPostById);

  router.post(
    "/posts",
    verifyToken,
    validate(InsertPostRequest),
    AsyncHandler(PostController.postPost)
  );
  router.put(
    "/posts/:id",
    verifyToken,
    validate(UpdatePostRequest),
    AsyncHandler(PostController.putPostUser)
  );
  router.put(
    "/posts/admin/:id",
    verifyToken,
    validate(UpdatePostAdminRequest),
    AsyncHandler(PostController.putPostAdmin)
  );
  router.delete("/posts/:id", PostController.deletePost);

  // Post Media
  router.get("/post-medias", PostMediaController.getPostMedia);
  router.get("/post-medias/:id", PostMediaController.getPostMediaById);
  router.post(
    "/post-medias",
    validate(InsertPostMediaRequest),
    validateImageExists,
    AsyncHandler(PostMediaController.postPostMedia)
  );
  router.delete("/post-medias/:id", PostMediaController.deletePostMedia);

  // Report
  router.get("/reports", verifyToken, ReportController.getReport);
  router.get("/reports/:id", verifyToken, ReportController.getReportById);
  router.post(
    "/reports",
    verifyToken,
    validate(InsertReportRequest),
    AsyncHandler(ReportController.postReport)
  );
  router.put(
    "/reports/:id",
    verifyToken,
    validate(UpdateReportRequest),
    AsyncHandler(ReportController.putReport)
  );
  router.delete("/reports/:id", verifyToken, ReportController.deleteReport);

  // Report Action
  router.get(
    "/report-actions",
    verifyToken,
    ReportActionController.getReportAction
  );
  router.get(
    "/report-actions/:id",
    verifyToken,
    ReportActionController.getReportActionById
  );
  router.post(
    "/report-actions",
    verifyToken,
    validate(InsertReportActionRequest),
    AsyncHandler(ReportActionController.postReportAction)
  );
  router.put(
    "/report-actions/:id",
    verifyToken,
    validate(UpdateReportActionRequest),
    AsyncHandler(ReportActionController.putReportAction)
  );

  // Upload image
  router.get("/images/:fileName", AsyncHandler(ImageController.viewImage));
  router.post(
    "/images/upload",
    verifyToken,
    uploadImage.array("images", 5),
    AsyncHandler(ImageController.uploadImages)
  );
  app.use("/api/", router);
};

export default AppRoute;
