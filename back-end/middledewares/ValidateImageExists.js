import path from 'path'
import fs from 'fs'

const validateImageExists = (req, res, next) => {
    const imageName = req.body.image

    if (imageName && !imageName.startsWith('http://') &&
        !imageName.startsWith('https://')) {
        const imagePath = path.join(__dirname, '../uploads', imageName)

        if (!fs.existsSync(imagePath)) {
            return res.status(404).json({
                message: 'File not found'
            })
        }
    }
    next()
}

export default validateImageExists
