'use strict';

module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define(
    'User',
    {
      id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },

      u_id: {
        type: DataTypes.STRING
      },

      email: {
        type: DataTypes.STRING
      },

      phone: {
        type: DataTypes.STRING
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
      }
    },
    {
      tableName: 'users',
      underscored: true,
      timestamps: false
    }
  );

  return User;
};
