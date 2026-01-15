import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import AppRoute from "./AppRoute.js";
import http from "http";
import initSocket from "./sockets/socket.js";

dotenv.config();

const app = express();

app.use(
  cors({
    origin: "http://localhost:5173",
    credentials: true,
  })
);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const port = process.env.PORT || 8989;

const server = http.createServer(app);
initSocket(server);
AppRoute(app);

server.listen(port, "0.0.0.0", () => {
  console.log(`Server running at http://localhost:${port}`);
});
