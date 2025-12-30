'use strict';
module.exports = (sequelize, DataTypes) => {
  const Report = sequelize.define('Report', {
    target_type: DataTypes.ENUM('post', 'comment', 'user'),
    target_id: DataTypes.INTEGER,
    reason: DataTypes.STRING,
    description: DataTypes.TEXT,
    status: DataTypes.ENUM('pending', 'reviewed', 'resolved', 'rejected'),
    reviewed_at: DataTypes.DATE
  }, {
    modelName: 'Report',
    tableName: 'reports',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  Report.associate = function(models) {
    Report.belongsTo(models.User, { foreignKey: 'reporter_id' });
    Report.hasMany(models.ReportAction, { foreignKey: 'report_id' });
  };

  return Report;
};
