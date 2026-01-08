'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('report_actions', {
      id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
      },

      report_id: {
        type: Sequelize.INTEGER,
        allowNull: false
      },

      admin_id: {
        type: Sequelize.INTEGER,
        allowNull: false
      },

      action: {
        type: Sequelize.INTEGER,
        allowNull: false
      },

      note: {
        type: Sequelize.TEXT
      },

      created_at: {
        type: Sequelize.DATE,
        defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')
      },

      updated_at: {
        type: Sequelize.DATE,
        defaultValue: Sequelize.literal('CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP')
      }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('report_actions');
  }
};
