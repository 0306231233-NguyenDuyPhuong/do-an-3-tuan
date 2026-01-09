'use strict';
module.exports = (sequelize, DataTypes) => {
  const ConversationMember = sequelize.define('ConversationMember', {}, {
    tableName: 'conversation_members',
    underscored: true,
    createdAt: 'joined_at',
    updatedAt: false
  });

  ConversationMember.associate = function(models) {
    ConversationMember.belongsTo(models.Conversation, { foreignKey: 'conversation_id' });
    ConversationMember.belongsTo(models.User, { foreignKey: 'user_id' });
  };

  return ConversationMember;
};
