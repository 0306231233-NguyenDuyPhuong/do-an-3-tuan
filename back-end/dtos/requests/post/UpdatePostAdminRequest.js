import Joi, { valid } from "joi";

class UpdatePostAdminRequest{
    constructor(data){
        this.status = data.status
    }

    static validate(data){
        const schema = Joi.object({
            status  : Joi.number().integer()
            .valid(0, 1)
            .required()
        })
        return schema.validate(data)
    }
}

export default UpdatePostAdminRequest;