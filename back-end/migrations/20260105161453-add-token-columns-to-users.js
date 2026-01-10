module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn("users", "refresh_token", {
      type: Sequelize.STRING(500),
      allowNull: true,
    });
    await queryInterface.addColumn("users", "reset_token", {
      type: Sequelize.STRING(500),
      allowNull: true,
    });
    await queryInterface.addColumn("users", "reset_token_expire", {
      type: Sequelize.BIGINT,
      allowNull: true,
    });
  },

  async down(queryInterface) {
    await queryInterface.removeColumn("users", "refresh_token");
    await queryInterface.removeColumn("users", "reset_token");
    await queryInterface.removeColumn("users", "reset_token_expire");
  },
};
