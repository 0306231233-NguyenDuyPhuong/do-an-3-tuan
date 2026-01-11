import { useEffect } from "react";
import AppRoutes from "./routes/AppRoutes";
import { refreshAccessToken } from "./services/UserService";

function App() {
  useEffect(()=>{
    refreshAccessToken();
    const interval = setInterval(()=>{
      refreshAccessToken();
    }, 15*60*60)

    return ()=> clearInterval(interval)
  })

  
  return <AppRoutes/>
}

export default App;
