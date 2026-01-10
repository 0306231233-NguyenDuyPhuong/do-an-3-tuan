"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    // 1. Cho phép email, phone = NULL
    await queryInterface.changeColumn("users", "email", {
      type: Sequelize.STRING(255),
      allowNull: true,
    });

    await queryInterface.changeColumn("users", "phone", {
      type: Sequelize.STRING(20),
      allowNull: true,
    });

    // 2. Thêm các cột auth nếu chưa có
    const table = await queryInterface.describeTable("users");

    if (!table.refresh_token) {
      await queryInterface.addColumn("users", "refresh_token", {
        type: Sequelize.STRING(500),
        allowNull: true,
      });
    }

    if (!table.reset_token) {
      await queryInterface.addColumn("users", "reset_token", {
        type: Sequelize.STRING(500),
        allowNull: true,
      });
    }

    if (!table.reset_token_expire) {
      await queryInterface.addColumn("users", "reset_token_expire", {
        type: Sequelize.BIGINT,
        allowNull: true,
      });
    }

    if (!table.birth) {
      await queryInterface.addColumn("users", "birth", {
        type: Sequelize.DATEONLY,
        allowNull: true,
      });
    }
  },

  async down(queryInterface, Sequelize) {
    // rollback an toàn
    await queryInterface.changeColumn("users", "email", {
      type: Sequelize.STRING(255),
      allowNull: false,
    });

    await queryInterface.changeColumn("users", "phone", {
      type: Sequelize.STRING(20),
      allowNull: true,
    });

    await queryInterface.removeColumn("users", "refresh_token");
    await queryInterface.removeColumn("users", "reset_token");
    await queryInterface.removeColumn("users", "reset_token_expire");
    await queryInterface.removeColumn("users", "birth");
  },
};
