'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('posts', {
      id: { allowNull: false, autoIncrement: true, primaryKey: true, type: Sequelize.INTEGER },

      user_id: { type: Sequelize.INTEGER, allowNull: false },
      content: { type: Sequelize.TEXT },

      privacy: { type: Sequelize.INTEGER, defaultValue: 0 }, // 0=public,1=friends,2=private
      location_id: { type: Sequelize.INTEGER },

      status: { type: Sequelize.INTEGER, defaultValue: 1 }, // 1=approved,0=deleted
      like_count: { type: Sequelize.INTEGER, defaultValue: 0 },
      comment_count: { type: Sequelize.INTEGER, defaultValue: 0 },
      share_count: { type: Sequelize.INTEGER, defaultValue: 0 },

      created_at: { allowNull: false, type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP') },
      updated_at: { allowNull: false, type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP') }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('posts');
  }
};
