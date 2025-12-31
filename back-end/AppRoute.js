import express from 'express';
const router = express.Router();
import LocationController from './controllers/LocationController.js';
import PostController from "./controllers/PostController.js";
import PostMediaController from "./controllers/PostMediaController.js";
import ReportController from "./controllers/ReportController.js";
import ReportActionController from "./controllers/ReportActionController.js";
import AsyncHandler from "./middledewares/AsyncHandler.js"
import InsertLocationRequest from './dtos/requests/location/InsertLocatioinRequest.js';
import InsertPostRequest from './dtos/requests/post/InsertPostRequest.js';
import InsertReportActionRequest from './dtos/requests/report-action/InsertReportActionRequest.js';
import InsertReportRequest from './dtos/requests/report/InsertReportRequest.js';
import UpdateLocationRequest from './dtos/requests/location/UpdateLocationRequest.js';
import UpdatePostRequest from './dtos/requests/post/UpdatePostRequest.js';
import UpdateReportRequest from './dtos/requests/report/UpdateReportRequest.js';
import UpdateReportActionRequest from './dtos/requests/report-action/UpdateReportActionRequest.js';
import validate  from './middledewares/Validate.js';

const AppRoute = (app) => {
  //Location
  router.get('/locations', LocationController.getLocation)
  router.get('/locations/:id', LocationController.getLocationById)
  router.post('/locations', 
    validate(InsertLocationRequest),
    AsyncHandler(LocationController.postLocation))
  router.put('/locations/:id', 
    validate(UpdateLocationRequest),
    AsyncHandler(LocationController.putLocation))
  router.delete('/locations/:id', LocationController.deleteLocation)

  // Post
  router.get('/posts', PostController.getPost);
  router.get('/posts/:id', PostController.getPostById);
  router.post('/posts', 
    validate(InsertPostRequest),
    AsyncHandler(PostController.postPost));
  router.put('/posts/:id', 
    validate(UpdatePostRequest),
    AsyncHandler(PostController.putPost));
  router.delete('/posts/:id', PostController.deletePost);

  // Post Media
  router.get('/post-medias', PostMediaController.getPostMedia);
  router.get('/post-medias/:id', PostMediaController.getPostMediaById);
  router.post('/post-medias', PostMediaController.postPostMedia);
  router.delete('/post-medias/:id', PostMediaController.deletePostMedia);

  // Report
  router.get('/reports', ReportController.getReport);
  router.get('/reports/:id', ReportController.getReportById);
  router.post('/reports', 
    validate(InsertReportRequest),
    AsyncHandler(ReportController.postReport));
  router.put('/reports/:id', 
    validate(UpdateReportRequest),
    AsyncHandler(ReportController.putReport));
  router.delete('/reports/:id', ReportController.deleteReport);

  // Report Action
  router.get('/report-actions', ReportActionController.getReportAction);
  router.get('/report-actions/:id', ReportActionController.getReportActionById);
  router.post('/report-actions',
    validate(InsertReportActionRequest),
    AsyncHandler(ReportActionController.postReportAction));
  router.put('/report-actions/:id', 
    validate(UpdateReportActionRequest),
    AsyncHandler(ReportActionController.putReportAction));

  app.use("/api/", router)
};

export default AppRoute;
