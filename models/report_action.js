'use strict';
module.exports = (sequelize, DataTypes) => {
  const ReportAction = sequelize.define('ReportAction', {
    action: DataTypes.ENUM('hide_content', 'delete_content', 'warn_user', 'ban_user'),
    note: DataTypes.TEXT
  }, {
    modelName: 'ReportAction',
    tableName: 'report_actions',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  ReportAction.associate = function(models) {
    ReportAction.belongsTo(models.Report, { foreignKey: 'report_id' });
    ReportAction.belongsTo(models.User, { foreignKey: 'admin_id' });
  };

  return ReportAction;
};
