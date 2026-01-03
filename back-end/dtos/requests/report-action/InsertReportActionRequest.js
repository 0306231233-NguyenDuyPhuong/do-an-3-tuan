import Joi, { not, valid } from "joi";

class InsertReportActionRequest{
    constructor(data){
        this.report_id = data.report_id,
        this.admin_id = data.admin_id,
        this.action = data.action,
        this.note = data.note
    };

    static validate(data){
        const schema = Joi.object({
            report_id : Joi.number().integer().required(),
            admin_id : Joi.number().integer().required(),
            action: Joi.string()
            .valid('hide_content', 'delete_content', 'warn_user', 'ban_user')
            .required(),
            note: Joi.string().allow("").required(),
        });
        return schema.validate(data);
    }
}

export default InsertReportActionRequest;