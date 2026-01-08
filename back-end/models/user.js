'use strict';
module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    email: DataTypes.STRING,
    phone: DataTypes.STRING,
    password: DataTypes.STRING,
    full_name: DataTypes.STRING,
    birth_date: DataTypes.DATE,
    gender: DataTypes.STRING,
    avatar: DataTypes.STRING,
    // Thêm giá trị mặc định để khớp với Nghiệp vụ API bạn đã nêu
    status: {
      type: DataTypes.ENUM('active', 'inactive', 'banned'),
      defaultValue: 'active'
    },
    role: {
      type: DataTypes.ENUM('user', 'admin', 'moderator'),
      defaultValue: 'user' // Role mặc định là user như tài liệu API
    }, // <-- PHẢI CÓ DẤU PHẨY Ở ĐÂY
    reset_token: DataTypes.STRING,
    reset_token_expire: DataTypes.DATE
  }, {
    tableName: 'users',
    underscored: true,
    createdAt: 'created_at',
    updated_at: 'updated_at'
  });

  User.associate = function(models) {
    User.hasMany(models.Post, { foreignKey: 'user_id' });
    User.hasMany(models.Comment, { foreignKey: 'user_id' });
    User.hasMany(models.Like, { foreignKey: 'user_id' });
  };

  return User;
};