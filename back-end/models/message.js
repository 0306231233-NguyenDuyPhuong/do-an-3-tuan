'use strict';
module.exports = (sequelize, DataTypes) => {
  const Message = sequelize.define('Message', {
    content: DataTypes.TEXT,
    message_type: DataTypes.ENUM('text', 'image', 'video', 'post'),
    deleted_at: DataTypes.DATE
  }, {
    tableName: 'messages',
    underscored: true,
    createdAt: 'created_at',
    updatedAt: false
  });

  Message.associate = function(models) {
    Message.belongsTo(models.Conversation, { foreignKey: 'conversation_id' });
    Message.belongsTo(models.User, { foreignKey: 'sender_id' });
  };

  return Message;
};
