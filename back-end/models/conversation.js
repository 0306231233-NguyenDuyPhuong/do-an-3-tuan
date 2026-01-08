'use strict';
module.exports = (sequelize, DataTypes) => {
  const Conversation = sequelize.define('Conversation', {
    type: DataTypes.ENUM('private', 'group'),
    name: DataTypes.STRING
  }, {
    tableName: 'conversations',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  Conversation.associate = function(models) {
    Conversation.hasMany(models.ConversationMember, { foreignKey: 'conversation_id' });
  };

  return Conversation;
};
