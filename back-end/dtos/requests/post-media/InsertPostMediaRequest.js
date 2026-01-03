import Joi from "joi";

class InsertPostMediaRequest{
    constructor(data){
        this.post_id = data.post_id,
        this.media_url = data.media_url,
        this.media_type = data.media_type,
        this.thumbnail_url = data.thumbnail_url
    }
    
    static validate(data){
        const schema = Joi.object({
            post_id : Joi.number().integer().min(1).required(),
            media_url : Joi.string().allow("").optional(),
            media_type : Joi.number().integer().required(),
            thumbnail_url : Joi.string().allow("").optional(),
        });
        return schema.validate(data)
    }
}

export default InsertPostMediaRequest;