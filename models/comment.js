'use strict';
module.exports = (sequelize, DataTypes) => {
  const Comment = sequelize.define('Comment', {
    content: DataTypes.TEXT,
    status: DataTypes.ENUM('active', 'hidden', 'deleted'),
    deleted_at: DataTypes.DATE
  }, {
    modelName: 'Comment',
    tableName: 'comments',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  Comment.associate = function(models) {
    Comment.belongsTo(models.Post, { foreignKey: 'post_id' });
    Comment.belongsTo(models.User, { foreignKey: 'user_id' });
  };

  return Comment;
};
