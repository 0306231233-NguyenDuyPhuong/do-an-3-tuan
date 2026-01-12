import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { postLoginUser } from "../services/UserService";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

const handleLogin = async () => {
  try {
    const res = await postLoginUser({
      username: username,
      password: password,
    });

    localStorage.setItem("accessToken", res.data.accessToken);
    localStorage.setItem("refreshToken", res.data.refreshToken);

    localStorage.setItem("user",
      JSON.stringify(res.data.user))
    if(res.data.user.role == 1){
      navigate("/");
    } else{
      alert("Tài khoản không có quyền truy cập");
      navigate('/login')
    }
  } catch (error) {
    console.error("LOGIN ERROR:", error.response?.data);
    alert("Sai tài khoản hoặc mật khẩu");
  }
};

  return (
    <div className="flex justify-center items-center min-h-screen">
      <div className="flex flex-col h-[500px] w-[400px] rounded-2xl bg-blue-100 p-10 items-center">
        <h1 className="text-4xl text-blue-500 font-bold mb-6">Login</h1>

        <div className="flex flex-col gap-4 w-full">
          <div>
            <span className="text-2xl text-blue-500 font-bold">User name:</span>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="h-10 w-full rounded-2xl bg-white p-2"
            />
          </div>

          <div>
            <span className="text-2xl text-blue-500 font-bold">Password:</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="h-10 w-full rounded-2xl bg-white p-2"
            />
          </div>

          <button
            onClick={handleLogin}
            className="h-10 w-full rounded-2xl bg-blue-700 text-white font-bold mt-4"
          >
            Login
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;
