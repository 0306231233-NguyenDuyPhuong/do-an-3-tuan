"use strict";
module.exports = (sequelize, DataTypes) => {
  const Conversation = sequelize.define(
    "Conversation",
    {
      type: {
        type: DataTypes.INTEGER,
        allowNull: false,
        comment: "0: private, 1: group",
      },
    },
    {
      tableName: "conversations",
      underscored: true,
      createdAt: "created_at",
      updatedAt: false,
    }
  );

  Conversation.associate = function (models) {
    Conversation.hasMany(models.ConversationMember, {
      foreignKey: "conversation_id",
      as: "members",
    });
    Conversation.hasMany(models.Message, {
      foreignKey: "conversation_id",
      as: "messages",
    });
  };

  return Conversation;
};
