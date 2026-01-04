import Joi, { valid } from "joi";

class UpdatePostAdminRequest{
    constructor(data){
        this.status = data.status
    }

    static validate(data){
        const schema = Joi.object({
            status  : Joi.string()
            .valid("delete", "approved")
            .required()
        })
        return schema.validate(data)
    }
}

export default UpdatePostAdminRequest;