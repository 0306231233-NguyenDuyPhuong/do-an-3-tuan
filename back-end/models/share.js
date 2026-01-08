'use strict';
module.exports = (sequelize, DataTypes) => {
  const Share = sequelize.define('Share', {}, {
    tableName: 'shares',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  Share.associate = function(models) {
    Share.belongsTo(models.Post, { foreignKey: 'post_id' });
    Share.belongsTo(models.User, { foreignKey: 'user_id' });
  };

  return Share;
};
