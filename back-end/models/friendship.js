'use strict';
const { Model } = require('sequelize');

module.exports = (sequelize, DataTypes) => {
  class Friendship extends Model {
    static associate(models) {

      // người gửi lời mời
      Friendship.belongsTo(models.User, {
        foreignKey: 'user_id',
        as: 'sender'
      });

      // người nhận lời mời
      Friendship.belongsTo(models.User, {
        foreignKey: 'friend_id',
        as: 'receiver'
      });
    }
  }

  Friendship.init({
    user_id: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    friend_id: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    status: {
      type: DataTypes.INTEGER, // ⚠️ nên là INTEGER
      allowNull: false
    }
  }, {
    sequelize,
    modelName: 'Friendship',
    tableName: 'friendships',
    underscored: true,
    timestamps: false
  });

  return Friendship;
};