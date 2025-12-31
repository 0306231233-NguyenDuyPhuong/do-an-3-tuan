'use strict';
module.exports = (sequelize, DataTypes) => {
  const PostMedia = sequelize.define('PostMedia', {
    media_url: DataTypes.STRING,
    media_type: DataTypes.ENUM('image', 'video'),
    thumbnail_url: DataTypes.STRING
  }, {
    modelName: 'PostMedia',
    tableName: 'post_media',
    underscored: true,
    timestamps: false
  });

  PostMedia.associate = function(models) {
    PostMedia.belongsTo(models.Post, { foreignKey: 'post_id' });
  };

  return PostMedia;
};
