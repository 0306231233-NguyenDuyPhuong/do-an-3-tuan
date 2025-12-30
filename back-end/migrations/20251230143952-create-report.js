'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('reports', {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
      },

      reporter_id: {
        type: Sequelize.INTEGER,
        allowNull: false
      },

      target_type: {
        type: Sequelize.STRING,
        allowNull: false
        // post | comment | user
      },

      target_id: {
        type: Sequelize.INTEGER,
        allowNull: false
      },

      reason: {
        type: Sequelize.STRING,
        allowNull: false
      },

      description: {
        type: Sequelize.TEXT,
        allowNull: true
      },

      status: {
        type: Sequelize.INTEGER,
        allowNull: false,
        defaultValue: 0
        // 0=pending, 1=reviewed, 2=resolved, 3=rejected
      },

      created_at: {
        allowNull: false,
        type: Sequelize.DATE,
        defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')
      },

      reviewed_at: {
        type: Sequelize.DATE,
        allowNull: true
      }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('reports');
  }
};
