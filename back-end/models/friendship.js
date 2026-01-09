'use strict';
module.exports = (sequelize, DataTypes) => {
  const Friendship = sequelize.define('Friendship', {
    only_follow: DataTypes.INTEGER,
    status: DataTypes.ENUM('pending', 'accepted', 'rejected', 'blocked')
  }, {
    tableName: 'friendships',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: 'updated_at'
  });

  Friendship.associate = function(models) {
    Friendship.belongsTo(models.User, { foreignKey: 'user_id' });
    Friendship.belongsTo(models.User, { foreignKey: 'friend_id' });
  };

  return Friendship;
};
