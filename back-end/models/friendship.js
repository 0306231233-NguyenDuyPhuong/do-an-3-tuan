"use strict";

module.exports = (sequelize, DataTypes) => {
  const Friendship = sequelize.define(
    "Friendship",
    {
      user_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },

      friend_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },

      only_follow: {
        type: DataTypes.BOOLEAN,
        allowNull: false,
        defaultValue: false,
      },

      status: {
        type: DataTypes.ENUM("pending", "accepted", "rejected", "blocked"),
        allowNull: false,
      },
    },
    {
      tableName: "friendships",
      underscored: true,
      createdAt: "created_at",
      updatedAt: "updated_at",
    }
  );

  Friendship.associate = function (models) {
    Friendship.belongsTo(models.User, {
      foreignKey: "user_id",
      as: "sender",
    });

    Friendship.belongsTo(models.User, {
      foreignKey: "friend_id",
      as: "receiver",
    });
  };

  return Friendship;
};
