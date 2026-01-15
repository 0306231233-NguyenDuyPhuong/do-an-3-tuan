"use strict";
module.exports = (sequelize, DataTypes) => {
  const Post = sequelize.define(
    "Post",
    {
      content: DataTypes.TEXT,
      privacy: DataTypes.ENUM("public", "friends", "private"),
      status: DataTypes.ENUM("approved", "deleted"),
      like_count: DataTypes.INTEGER,
      comment_count: DataTypes.INTEGER,
      share_count: DataTypes.INTEGER,
      is_like: DataTypes.BOOLEAN,
    },
    {
      tableName: "posts",
      underscored: true,
      createdAt: "created_at",
      updatedAt: "updated_at",
    }
  );

  Post.associate = function (models) {
    Post.belongsTo(models.User, { foreignKey: "user_id" });
    Post.belongsTo(models.Location, { foreignKey: "location_id" });
    Post.hasMany(models.Comment, { foreignKey: "post_id" });
    Post.hasMany(models.PostMedia, { foreignKey: "post_id" });
    Post.hasMany(models.Like, { foreignKey: "post_id" });
  };

  return Post;
};
