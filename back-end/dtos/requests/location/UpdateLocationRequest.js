const Joi = require("joi")

class UpdateLocationRequest {
    constructor(data){
        this.name = data.name,
        this.address = data.address
    };
    
    static validate(data){
        const schema = Joi.object({
            name: Joi.string().optional(),
            address: Joi.string().optional()
        });
        return schema.validate(data);
    };
}

export default UpdateLocationRequest;