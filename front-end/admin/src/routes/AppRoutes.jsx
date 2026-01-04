import { Routes, Route } from "react-router-dom";
import Layout from "../components/Layout";
import Home from "../pages/Home";
import User from "../pages/User";
import Post from "../pages/Post"
const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="post" element={<Post />} />
        <Route path="user" element={<User />} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
