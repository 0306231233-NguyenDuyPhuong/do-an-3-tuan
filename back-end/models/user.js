"use strict";
module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define(
    "User",
    {
      email: DataTypes.STRING,
      password: DataTypes.STRING,
    },
    {
      tableName: "users",
      timestamps: true,
    }
  );

  User.associate = function (models) {
    // define association here
  };

  return User;
};
