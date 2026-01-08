'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('friendships', {
      id: { allowNull: false, autoIncrement: true, primaryKey: true, type: Sequelize.INTEGER },

      user_id: { type: Sequelize.INTEGER, allowNull: false },
      friend_id: { type: Sequelize.INTEGER, allowNull: false },
      onlyflow: { type: Sequelize.INTEGER, defaultValue: 0 },

      status: { type: Sequelize.INTEGER, defaultValue: 0 }, // 0=pending,1=accepted,2=rejected,3=blocked

      created_at: { type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP') },
      updated_at: { type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP') }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('friendships');
  }
};
