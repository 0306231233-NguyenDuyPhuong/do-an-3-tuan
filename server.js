// server.js
import express from 'express'
import dotenv from 'dotenv'
import AppRoute from './AppRoute.js'

const app = express()

dotenv.config(); 
app.use(express.json())
express.urlencoded({extended:true})


const port = process.env.PORT || 3000; 

AppRoute(app)

app.listen(8989, "0.0.0.0", () => {
  console.log(`Server run at ${port}`);
});