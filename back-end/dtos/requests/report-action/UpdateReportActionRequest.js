import Joi from "joi";

class UpdateReportActionRequest{
    constructor(data){
        this.admin_id = data.admin_id,
        this.action = data.action
    };

    static validate(data){
        const schema = Joi.object({
            admin_id: Joi.number().integer().required(),
            action: Joi.string()
            .valid('hide_content', 'delete_content', 'warn_user', 'ban_user')
            .required(),
        });
        return schema.validate(data);
    }
}

export default UpdateReportActionRequest;