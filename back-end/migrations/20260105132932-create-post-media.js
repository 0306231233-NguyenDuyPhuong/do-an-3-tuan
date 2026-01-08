'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('post_medias', {
      id: { allowNull: false, autoIncrement: true, primaryKey: true, type: Sequelize.INTEGER },

      post_id: { type: Sequelize.INTEGER, allowNull: false },
      media_url: { type: Sequelize.STRING, allowNull: false },
      media_type: { type: Sequelize.INTEGER }, // 0=image,1=video
      thumbnail_url: { type: Sequelize.STRING },

      created_at: { type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP') },
      updated_at: { type: Sequelize.DATE, defaultValue: Sequelize.literal('CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP') },
      deleted_at: { type: Sequelize.DATE }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('post_medias');
  }
};
