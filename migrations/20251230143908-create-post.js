'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('posts', {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
      },

      user_id: {
        type: Sequelize.INTEGER,
        allowNull: false
      },

      content: {
        type: Sequelize.TEXT,
        allowNull: true
      },

      privacy: {
        type: Sequelize.INTEGER,
        allowNull: false,
        defaultValue: 0 // 0=public, 1=friends, 2=private
      },

      location_id: {
        type: Sequelize.INTEGER,
        allowNull: true
      },

      status: {
        type: Sequelize.INTEGER,
        allowNull: false,
        defaultValue: 1 // 0=pending, 1=approved, 2=rejected, 3=deleted
      },

      created_at: {
        allowNull: false,
        type: Sequelize.DATE,
        defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')
      },

      updated_at: {
        allowNull: false,
        type: Sequelize.DATE,
        defaultValue: Sequelize.literal(
          'CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP'
        )
      }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('posts');
  }
};
