import Joi from "joi";

class UpdateReportRequest{
    constructor(data){
        this.status = data.status
    };

    static validate(data){
        const schema = Joi.object({
            status : Joi.string()
            .valid("pending", "reviewed", "resolved", "rejected")
        });
        return schema.validate(data);
    }
}

export default UpdateReportRequest;