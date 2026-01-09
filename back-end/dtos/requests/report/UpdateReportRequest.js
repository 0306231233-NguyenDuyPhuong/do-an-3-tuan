import Joi from "joi";

class UpdateReportRequest{
    constructor(data){
        this.status = data.status
    };

    static validate(data){
        const schema = Joi.object({
            status : Joi.number().integer()
            .valid(0, 1, 2, 3)
        });
        return schema.validate(data);
    }
}

export default UpdateReportRequest;