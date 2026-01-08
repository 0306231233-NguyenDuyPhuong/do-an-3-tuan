'use strict';
module.exports = (sequelize, DataTypes) => {
  const Location = sequelize.define('Location', {
    name: DataTypes.STRING,
    address: DataTypes.STRING,
    deleted_at: DataTypes.DATE
  }, {
    tableName: 'locations',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: 'updated_at'
  });

  Location.associate = function(models) {
    Location.hasMany(models.Post, { foreignKey: 'location_id' });
  };

  return Location;
};
