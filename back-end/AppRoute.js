import express from 'express';
const router = express.Router();

const AppRoute = (app) => {
  app.get("/", (req, res) => {
    res.send("Hello AppRoute");
  });
};

export default AppRoute;
