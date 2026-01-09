'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('messages', {
      id: { allowNull: false, autoIncrement: true, primaryKey: true, type: Sequelize.INTEGER },

      conversation_id: { type: Sequelize.INTEGER, allowNull: false },
      sender_id: { type: Sequelize.INTEGER, allowNull: false },
      content: { type: Sequelize.TEXT },

      message_type: { type: Sequelize.INTEGER, defaultValue: 0 }, // 0=text,1=image,2=video,3=post

      created_at: { type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP') },
      deleted_at: { type: Sequelize.DATE }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('messages');
  }
};
