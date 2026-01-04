'use strict';
const { Model } = require('sequelize');

module.exports = (sequelize, DataTypes) => {
  class Share extends Model {
    static associate(models) {
      Share.belongsTo(models.Post, {
        foreignKey: "post_id"
      });

      Share.belongsTo(models.User, {
        foreignKey: "user_id"
      });
    }
  }

  Share.init({
    post_id: DataTypes.INTEGER,
    user_id: DataTypes.INTEGER,
    createdAt: 'created_at',
  }, {
    sequelize,
    modelName: 'Share',
    tableName: 'shares',
    underscored: true
  });

  return Share;
};
