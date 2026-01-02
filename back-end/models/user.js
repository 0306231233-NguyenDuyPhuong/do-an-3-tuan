'use strict';

module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define(
    'User',
    {
<<<<<<< HEAD
      email: DataTypes.STRING,
      password: DataTypes.STRING,
      full_name: DataTypes.STRING,
      role: DataTypes.STRING,
=======
      id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },

      u_id: {
        type: DataTypes.STRING
      },

      email: {
        type: DataTypes.STRING,
        allowNull: true,
      },

      phone: {
        type: DataTypes.STRING,
        allowNull: true,
      },

      password: {
        type: DataTypes.STRING
      },

      full_name: {
        type: DataTypes.STRING
      },

      birth_date: {
        type: DataTypes.DATEONLY
      },

      gender: {
        type: DataTypes.STRING
      },

      avatar: {
        type: DataTypes.STRING
      },

      status: {
        type: DataTypes.STRING
      },

      role: {
        type: DataTypes.STRING
      },

      refresh_token: {
        type: DataTypes.STRING(500),
        allowNull: true,
      },

      reset_token: {
        type: DataTypes.STRING(500),
        allowNull: true,
      },

      reset_token_expire: {
        type: DataTypes.BIGINT,
        allowNull: true,
      },
>>>>>>> origin/nguyencongman
    },
    {
      tableName: 'users',
      underscored: true,
      timestamps: false
    }
  );

  // ================= ASSOCIATIONS =================
  User.associate = (models) => {

    // User gửi lời mời kết bạn
    User.hasMany(models.Friendship, {
      foreignKey: 'user_id',
      as: 'sentFriendships'
    });

    // User nhận lời mời kết bạn
    User.hasMany(models.Friendship, {
      foreignKey: 'friend_id',
      as: 'receivedFriendships'
    });
  };
  // =================================================

  return User;
};
