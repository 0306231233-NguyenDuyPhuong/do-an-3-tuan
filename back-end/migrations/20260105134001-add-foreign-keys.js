'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {

    /* ================= USERS RELATED ================= */

    // friendships.user_id -> users.id
    await queryInterface.addConstraint('friendships', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_friendships_user',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // friendships.friend_id -> users.id
    await queryInterface.addConstraint('friendships', {
      fields: ['friend_id'],
      type: 'foreign key',
      name: 'fk_friendships_friend',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= POSTS ================= */

    // posts.user_id -> users.id
    await queryInterface.addConstraint('posts', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_posts_user',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // posts.location_id -> locations.id
    await queryInterface.addConstraint('posts', {
      fields: ['location_id'],
      type: 'foreign key',
      name: 'fk_posts_location',
      references: { table: 'locations', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'SET NULL'
    });

    /* ================= POST MEDIAS ================= */

    // post_medias.post_id -> posts.id
    await queryInterface.addConstraint('post_medias', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_post_medias_post',
      references: { table: 'posts', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= COMMENTS ================= */

    // comments.post_id -> posts.id
    await queryInterface.addConstraint('comments', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_comments_post',
      references: { table: 'posts', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // comments.user_id -> users.id
    await queryInterface.addConstraint('comments', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_comments_user',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= LIKES ================= */

    // likes.post_id -> posts.id
    await queryInterface.addConstraint('likes', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_likes_post',
      references: { table: 'posts', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // likes.user_id -> users.id
    await queryInterface.addConstraint('likes', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_likes_user',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= SAVED POSTS ================= */

    // saved_posts.post_id -> posts.id
    await queryInterface.addConstraint('saved_posts', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_saved_posts_post',
      references: { table: 'posts', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // saved_posts.user_id -> users.id
    await queryInterface.addConstraint('saved_posts', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_saved_posts_user',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= SHARES ================= */

    // shares.post_id -> posts.id
    await queryInterface.addConstraint('shares', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_shares_post',
      references: { table: 'posts', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // shares.user_id -> users.id
    await queryInterface.addConstraint('shares', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_shares_user',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= REPORTS ================= */

    // reports.reporter_id -> users.id
    await queryInterface.addConstraint('reports', {
      fields: ['reporter_id'],
      type: 'foreign key',
      name: 'fk_reports_reporter',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= REPORT ACTIONS ================= */

    // report_actions.report_id -> reports.id
    await queryInterface.addConstraint('report_actions', {
      fields: ['report_id'],
      type: 'foreign key',
      name: 'fk_report_actions_report',
      references: { table: 'reports', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // report_actions.admin_id -> users.id
    await queryInterface.addConstraint('report_actions', {
      fields: ['admin_id'],
      type: 'foreign key',
      name: 'fk_report_actions_admin',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= CONVERSATIONS ================= */

    // conversation_members.conversation_id -> conversations.id
    await queryInterface.addConstraint('conversation_members', {
      fields: ['conversation_id'],
      type: 'foreign key',
      name: 'fk_conversation_members_conversation',
      references: { table: 'conversations', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // conversation_members.user_id -> users.id
    await queryInterface.addConstraint('conversation_members', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_conversation_members_user',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    /* ================= MESSAGES ================= */

    // messages.conversation_id -> conversations.id
    await queryInterface.addConstraint('messages', {
      fields: ['conversation_id'],
      type: 'foreign key',
      name: 'fk_messages_conversation',
      references: { table: 'conversations', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

    // messages.sender_id -> users.id
    await queryInterface.addConstraint('messages', {
      fields: ['sender_id'],
      type: 'foreign key',
      name: 'fk_messages_sender',
      references: { table: 'users', field: 'id' },
      onUpdate: 'CASCADE',
      onDelete: 'CASCADE'
    });

  },

  async down(queryInterface) {
    await queryInterface.removeConstraint('friendships', 'fk_friendships_user');
    await queryInterface.removeConstraint('friendships', 'fk_friendships_friend');

    await queryInterface.removeConstraint('posts', 'fk_posts_user');
    await queryInterface.removeConstraint('posts', 'fk_posts_location');

    await queryInterface.removeConstraint('post_medias', 'fk_post_medias_post');

    await queryInterface.removeConstraint('comments', 'fk_comments_post');
    await queryInterface.removeConstraint('comments', 'fk_comments_user');

    await queryInterface.removeConstraint('likes', 'fk_likes_post');
    await queryInterface.removeConstraint('likes', 'fk_likes_user');

    await queryInterface.removeConstraint('saved_posts', 'fk_saved_posts_post');
    await queryInterface.removeConstraint('saved_posts', 'fk_saved_posts_user');

    await queryInterface.removeConstraint('shares', 'fk_shares_post');
    await queryInterface.removeConstraint('shares', 'fk_shares_user');

    await queryInterface.removeConstraint('reports', 'fk_reports_reporter');

    await queryInterface.removeConstraint('report_actions', 'fk_report_actions_report');
    await queryInterface.removeConstraint('report_actions', 'fk_report_actions_admin');

    await queryInterface.removeConstraint('conversation_members', 'fk_conversation_members_conversation');
    await queryInterface.removeConstraint('conversation_members', 'fk_conversation_members_user');

    await queryInterface.removeConstraint('messages', 'fk_messages_conversation');
    await queryInterface.removeConstraint('messages', 'fk_messages_sender');
  }
};
