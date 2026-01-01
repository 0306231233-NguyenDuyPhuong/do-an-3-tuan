'use strict';
const { Model } = require('sequelize');

module.exports = (sequelize, DataTypes) => {
  class Friendship extends Model {
    /**
     * Helper method for defining associations.
     */
    static associate(models) {
      // Một Friendship liên kết với user_id (người gửi) và friend_id (người nhận)
      Friendship.belongsTo(models.User, { foreignKey: 'user_id', as: 'user' });
      Friendship.belongsTo(models.User, { foreignKey: 'friend_id', as: 'friend' });
    }
  }

  Friendship.init({
    user_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'user_id'
    },
    friend_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'friend_id'
    },
    status: {
      type: DataTypes.STRING,
      allowNull: false,
      defaultValue: 'pending', // ví dụ: pending, accepted, blocked
      field: 'status'
    }
  }, {
    sequelize,
    modelName: 'Friendship',
    tableName: 'friendships', // đảm bảo đúng tên bảng trong database
    timestamps: true,         // có createdAt / updatedAt
    createdAt: 'created_at',
    updatedAt: false
  });

  return Friendship;
};
