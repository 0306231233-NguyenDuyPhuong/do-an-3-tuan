import Joi from "joi";

class UpdatePostRequest{
    constructor(data){
        this.user_id = data.user_id,
        this.content = data.content || "",
        this.privacy = data.privacy || "public",
        this.location_id = data.location_id,
        this.status = data.status || "approved"
    };

     static validate(data){
            const schema = Joi.object({
                user_id: Joi.number().integer().min(1).required(),
                content: Joi.string().allow("").optional(),
                privacy: Joi.string()
                .valid("public", "friends", "private")
                .default("public"),
                location_id: Joi.number().integer().required(),
                status: Joi.string()
                .valid("approved", "delete")
                .default("approved")
            });
            return schema.validate(data);
        }
}

export default UpdatePostRequest;