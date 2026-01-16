'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class Post extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
    }
  }
  Post.init({
    user_id: DataTypes.INTEGER,
    content: DataTypes.TEXT,
    privacy: DataTypes.INTEGER,
    location_id: DataTypes.INTEGER,
    status: DataTypes.INTEGER,
    like_count: DataTypes.INTEGER,
    comment_count: DataTypes.INTEGER,
    share_count: DataTypes.INTEGER
  }, {
    sequelize,
    modelName: 'Post',
  });
  return Post;
};