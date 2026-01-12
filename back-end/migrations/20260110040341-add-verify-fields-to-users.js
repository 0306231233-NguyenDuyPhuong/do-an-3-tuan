module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.addColumn("users", "is_verified", {
      type: Sequelize.BOOLEAN,
      defaultValue: false,
    });

    await queryInterface.addColumn("users", "verify_token", {
      type: Sequelize.STRING(500),
      allowNull: true,
    });

    await queryInterface.addColumn("users", "verify_token_expire", {
      type: Sequelize.BIGINT,
      allowNull: true,
    });
  },

  down: async (queryInterface) => {
    await queryInterface.removeColumn("users", "is_verified");
    await queryInterface.removeColumn("users", "verify_token");
    await queryInterface.removeColumn("users", "verify_token_expire");
  },
};
