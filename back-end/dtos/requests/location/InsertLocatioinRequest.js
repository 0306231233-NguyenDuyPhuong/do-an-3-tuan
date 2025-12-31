import Joi from "joi";

class InsertLocationRequest {
    constructor(data){
        this.name = data.name,
        this.address = data.address
    };

    static validate(data){
        const schema = Joi.object({
            name: Joi.string().required(),
            address: Joi.string().required()
        });
        
        return schema.validate(data);
    };
}

export default InsertLocationRequest;