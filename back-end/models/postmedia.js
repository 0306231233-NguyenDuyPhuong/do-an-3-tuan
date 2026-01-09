'use strict';
module.exports = (sequelize, DataTypes) => {
  const PostMedia = sequelize.define('PostMedia', {
    media_url: DataTypes.STRING,
    media_type: DataTypes.ENUM('image', 'video'),
    thumbnail_url: DataTypes.STRING,
    deleted_at: DataTypes.DATE
  }, {
    tableName: 'post_medias',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: 'updated_at'
  });

  PostMedia.associate = function(models) {
    PostMedia.belongsTo(models.Post, { foreignKey: 'post_id' });
  };

  return PostMedia;
};
