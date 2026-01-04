'use strict';
module.exports = (sequelize, DataTypes) => {
  const Follow = sequelize.define(
    'Follow',
    {
      follower_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
      following_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
      deleted_at: {
        type: DataTypes.DATE,
        allowNull: true,
      },
    },
    {
      tableName: 'follows',
      underscored: true,
      timestamps: true,
      createdAt: 'created_at',
      updatedAt: false,
      paranoid: true,        
      deletedAt: 'deleted_at',
    }
  );

  Follow.associate = function (models) {
    Follow.belongsTo(models.User, {
      foreignKey: 'follower_id',
      as: 'Follower',
    });

    Follow.belongsTo(models.User, {
      foreignKey: 'following_id',
      as: 'Following',
    });
  };

  return Follow;
};
