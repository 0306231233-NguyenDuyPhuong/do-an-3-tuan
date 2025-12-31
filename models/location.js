'use strict';
module.exports = (sequelize, DataTypes) => {
  const Location = sequelize.define('Location', {
    name: DataTypes.STRING,
    address: DataTypes.STRING
  }, {
    modelName: 'Location',
    tableName: 'locations',
    underscored: true,
    timestamps: false
  });

  Location.associate = function(models) {
    Location.hasMany(models.Post, { foreignKey: 'location_id' });
  };

  return Location;
};
