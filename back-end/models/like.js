'use strict';
module.exports = (sequelize, DataTypes) => {
  const Like = sequelize.define('Like', {}, {
    modelName: 'Like',
    tableName: 'likes',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  Like.associate = function(models) {
    Like.belongsTo(models.Post, { foreignKey: 'post_id' });
    Like.belongsTo(models.User, { foreignKey: 'user_id' });
  };

  return Like;
};
