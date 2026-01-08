const { stat } = require("@babel/core/lib/gensync-utils/fs")
const Joi = require("joi")
const { read } = require("xlsx")

class InsertReportRequest{
    constructor(data){
        this.reporter_id = data.reporter_id,
        this.target_type = data.target_type,
        this.target_id = data.target_id,
        this.reason = data.reason,
        this.description = data.description
    }

    static validate(data){
        const schema = Joi.object({
            reporter_id : Joi.number().integer().min(1).required(),
            target_type : Joi.string()
            .valid("post", "comment", "user"),
            target_id : Joi.number().integer().min(1).required(),
            reason : Joi.string().required(),
            description : Joi.string().required(),
        });
        return schema.validate(data);
    }
}

export default InsertReportRequest;