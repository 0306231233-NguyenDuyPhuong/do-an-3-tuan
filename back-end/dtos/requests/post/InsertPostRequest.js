import Joi from "joi";
class InsertLocatioinRequest{
    constructor(data){
        this.user_id = data.user_id,
        this.content = data.content || "",
        this.privacy = data.privacy || 0,
        this.location_id = data.location_id,
        this.status = data.status || 1
    };

    static validate(data){
        const schema = Joi.object({
            user_id: Joi.number().integer().min(1).required(),
            content: Joi.string().allow("").optional(),
            privacy: Joi.number()
            .valid(0, 1, 2)
            .default(0)
            .integer(),
            location_id: Joi.number().integer().required(),
            status: Joi.number()
            .integer()
            .valid(0, 1)
            .default(1)
        });
        return schema.validate(data);
    }
}

export default InsertLocatioinRequest;