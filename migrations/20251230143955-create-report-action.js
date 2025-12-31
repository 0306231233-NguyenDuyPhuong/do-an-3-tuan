'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('report_actions', {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
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
        // 0=hide, 1=delete, 2=warn, 3=ban
      },

      note: {
        type: Sequelize.TEXT,
        allowNull: true
      },

      created_at: {
        allowNull: false,
        type: Sequelize.DATE,
        defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')
      }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('report_actions');
  }
};
