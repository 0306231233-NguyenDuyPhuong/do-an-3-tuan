'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('post_media', {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
      },

      post_id: {
        type: Sequelize.INTEGER,
        allowNull: false
      },

      media_url: {
        type: Sequelize.STRING,
        allowNull: false
      },

      media_type: {
        type: Sequelize.INTEGER,
        allowNull: false
        // 0=image, 1=video
      },

      thumbnail_url: {
        type: Sequelize.STRING,
        allowNull: true
      }
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('post_media');
  }
};
