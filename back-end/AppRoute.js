import express from 'express';
const router = express.Router();
import UserController from './controllers/UserController.js';
import AuthController from './controllers/AuthController.js';
import verifyToken from "./middledewares/verifyToken.js"
const AppRoute = (app) => {
  //Auth
  router.get('/user/login', AuthController.login)
  router.post('/auth/login', AuthController.login)
  router.post("/auth/register",AuthController.register)
  router.post("/auth/refresh",AuthController.refresh)
  router.post("/auth/logout",AuthController.logout)
  router.post("/auth/forgot-password",AuthController.forgotPassword)
    router.post("/auth/reset-password",AuthController.resetPassword)


  //User
  router.get("/user/get-profile",verifyToken,UserController.getProfile)
  app.use("/api/", router)
};

export default AppRoute;
