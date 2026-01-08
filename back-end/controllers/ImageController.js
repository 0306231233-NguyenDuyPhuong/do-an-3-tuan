import path from 'path'
import fs from 'fs'

const uploadImages=async(req,res)=>{
    //Kiểm tra nếu không có file nào được tải lên
    if(req.files.length===0){
        throw new Error('Không có file nào được tải lên')
    }
    //Trả về đường dẫn của file ảnh được tải lên
    const uploadImagesPaths = req.files.map(file=>path.basename(file.path))
    
    res.status(200).json({
        messaga:'Upload images success',
        files:uploadImagesPaths
    })
}

const viewImage=(req,res)=>{
    const {fileName} = req.params 
    const imagePath = path.join(path.join(__dirname,'../uploads/'),fileName)
    fs.access(imagePath,fs.constants.F_OK,(error)=>{
        if(error){
            return res.status(404).send('Image not found')
        }
        res.sendFile(imagePath)
    })
}
export default {
    uploadImages:uploadImages,
    viewImage:viewImage
}