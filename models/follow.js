'use strict';
module.exports = (sequelize, DataTypes) => {
  const Follow = sequelize.define('Follow', {
    deleted_at: DataTypes.DATE
  }, {
    modelName: 'Follow',
    tableName: 'follows',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  Follow.associate = function(models) {
    Follow.belongsTo(models.User, { foreignKey: 'follower_id', as: 'Follower' });
    Follow.belongsTo(models.User, { foreignKey: 'following_id', as: 'Following' });
  };

  return Follow;
};
