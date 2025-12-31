import express from 'express';
const router = express.Router();
import UserController from './controllers/UserController.js';
const AppRoute = (app) => {
  router.get('/user/login', UserController.login)

  app.use("/api/", router)
};

export default AppRoute;
