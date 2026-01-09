'use strict';
module.exports = (sequelize, DataTypes) => {
  const SavedPost = sequelize.define('SavedPost', {}, {
    tableName: 'saved_posts',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  SavedPost.associate = function(models) {
    SavedPost.belongsTo(models.Post, { foreignKey: 'post_id' });
    SavedPost.belongsTo(models.User, { foreignKey: 'user_id' });
  };

  return SavedPost;
};
