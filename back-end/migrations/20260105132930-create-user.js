'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('users', {
      id: { allowNull: false, autoIncrement: true, primaryKey: true, type: Sequelize.INTEGER },

      email: { type: Sequelize.STRING, allowNull: false },
      password: { type: Sequelize.STRING, allowNull: false },
      name: { type: Sequelize.STRING, allowNull: false },

      role: { type: Sequelize.INTEGER, allowNull: false, defaultValue: 0 }, // 0=user, 1=admin
      avatar: { type: Sequelize.STRING },
      phone: { type: Sequelize.STRING },

      status: { type: Sequelize.INTEGER, allowNull: false, defaultValue: 1 }, // 0=inactive,1=active,2=banned
      gender: { type: Sequelize.BOOLEAN },
      birth: { type: Sequelize.STRING },
      full_name: { type: Sequelize.TEXT },

      created_at: { allowNull: false, type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP') },
      updated_at: { allowNull: false, type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP') }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('users');
  }
};
