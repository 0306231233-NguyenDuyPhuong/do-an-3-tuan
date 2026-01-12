import { Routes, Route, Navigate } from "react-router-dom";
import Layout from "../components/Layout";
import Home from "../pages/Home";
import Login from "../pages/Login";
import User from "../pages/User";
import Post from "../pages/Post";
import Comment from "../pages/Comment";
import Report from "../pages/Report";
import Logout from "../pages/Logout";
import PostDetail from "../pages/PostDetail";
import ProtectedRoute from "./ProtectedRoute";
import UserDetail from "../pages/UserDetail";

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Home />} />
        <Route path="post" element = {<Post/>}/>
        <Route path="post/:postId" element={< PostDetail/>} />
        <Route path="user" element={<User />} />
        <Route path="user/:userId" element={<UserDetail />} />
        <Route path="comment" element={<Comment />} />
        <Route path="report" element={<Report />} />
        <Route path="logout" element={<Logout />} />
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
};

export default AppRoutes;
