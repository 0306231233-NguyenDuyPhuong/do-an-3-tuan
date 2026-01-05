import express from "express";
const router = express.Router();
import ImageController from "./controllers/ImageController.js";
import ValidateImageExists from "./middledewares/ValidateImageExists.js";
import uploadImage from "./middledewares/ImageUpload.js";
import UserController from "./controllers/UserController.js";
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
import FollowController from "./controllers/FollowController.js";

const AppRoute = (app) => {
  router.get("/user", AuthController.getUser);
  //Auth
  router.get("/user/login", AuthController.login);
  router.post("/auth/login", AuthController.login);
  router.post("/auth/register", AuthController.register);
  router.post("/auth/refresh", AuthController.refresh);
  router.post("/auth/logout", AuthController.logout);
  router.post("/auth/forgot-password", AuthController.forgotPassword);
  router.post("/auth/reset-password", AuthController.resetPassword);

  //User
  router.get("/user/get-profile", verifyToken, UserController.getProfile);
  router.get("/users/:id", verifyToken, UserController.getUserById);
  router.get("/users", verifyToken, UserController.getUsers);

  //Friend
  router.post(
    "/friends/requests",
    verifyToken,
    FriendController.sendFriendRequest
  );
  router.get(
    "/friends/requests",
    verifyToken,
    FriendController.getFriendRequests
  );
  router.patch(
    "/friends/requests/accept",
    verifyToken,
    FriendController.acceptFriendRequest
  );
  router.patch(
    "/friends/requests/reject",
    verifyToken,
    FriendController.rejectFriendRequest
  );
  router.get("/friends", verifyToken, FriendController.getFriends);
  router.post(
    "/friends/requests/cancel",
    verifyToken,
    FriendController.cancelFriendRequest
  );
  router.post("/friends/unfriend", verifyToken, FriendController.unFriend);
  router.patch(
    "/friends/requests/block",
    verifyToken,
    FriendController.blockFriendRequest
  );

  router.get(
    "/friend/status/:friendId",
    verifyToken,
    FriendController.isFriend
  );

  //Follow
  router.post("/follow", verifyToken, FollowController.followUser);
  router.delete("/follow/unfollow", verifyToken, FollowController.unFollow);
  router.get("/follows", verifyToken, FollowController.getFollowers);
  router.get("/follow/following", verifyToken, FollowController.getFollowing);
  router.get(
    "/follow/status/:followingId",
    verifyToken,
    FollowController.isFollowing
  );

  //Location
  router.get("/locations", LocationController.getLocation);
  router.get("/locations/:id", LocationController.getLocationById);
  router.post(
    "/locations",
    validate(InsertLocationRequest),
    verifyToken,
    AsyncHandler(LocationController.postLocation)
  );
  router.put(
    "/locations/:id",
    validate(UpdateLocationRequest),
    AsyncHandler(LocationController.putLocation)
  );
  router.delete("/locations/:id", LocationController.deleteLocation);

  // Post
  router.get("/posts/users", verifyToken, PostController.getPostUser);
  router.get("/posts/admin", verifyToken, PostController.getPostAdmin);
  router.get("/posts/:id", verifyToken, PostController.getPostById);
  router.post(
    "/posts",
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
  router.get("/reports", ReportController.getReport);
  router.get("/reports/:id", ReportController.getReportById);
  router.post(
    "/reports",
    validate(InsertReportRequest),
    AsyncHandler(ReportController.postReport)
  );
  router.put(
    "/reports/:id",
    validate(UpdateReportRequest),
    AsyncHandler(ReportController.putReport)
  );
  router.delete("/reports/:id", ReportController.deleteReport);

  // Report Action
  router.get("/report-actions", ReportActionController.getReportAction);
  router.get("/report-actions/:id", ReportActionController.getReportActionById);
  router.post(
    "/report-actions",
    validate(InsertReportActionRequest),
    AsyncHandler(ReportActionController.postReportAction)
  );
  router.put(
    "/report-actions/:id",
    validate(UpdateReportActionRequest),
    AsyncHandler(ReportActionController.putReportAction)
  );

  // Upload image
  router.get("/images/:fileName", AsyncHandler(ImageController.viewImage));
  router.post(
    "/images/upload",
    uploadImage.array("images", 5),
    AsyncHandler(ImageController.uploadImages)
  );
  app.use("/api/", router);
};

export default AppRoute;
