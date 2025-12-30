'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {

    // posts.user_id -> users.id
    await queryInterface.addConstraint('posts', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_posts_user',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE',
      onUpdate: 'CASCADE'
    });

    // posts.location_id -> locations.id
    await queryInterface.addConstraint('posts', {
      fields: ['location_id'],
      type: 'foreign key',
      name: 'fk_posts_location',
      references: {
        table: 'locations',
        field: 'id'
      },
      onDelete: 'SET NULL',
      onUpdate: 'CASCADE'
    });

    // post_media.post_id -> posts.id
    await queryInterface.addConstraint('post_media', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_post_media_post',
      references: {
        table: 'posts',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // comments.post_id -> posts.id
    await queryInterface.addConstraint('comments', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_comments_post',
      references: {
        table: 'posts',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // comments.user_id -> users.id
    await queryInterface.addConstraint('comments', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_comments_user',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // likes.post_id -> posts.id
    await queryInterface.addConstraint('likes', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_likes_post',
      references: {
        table: 'posts',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // likes.user_id -> users.id
    await queryInterface.addConstraint('likes', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_likes_user',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // follows.follower_id -> users.id
    await queryInterface.addConstraint('follows', {
      fields: ['follower_id'],
      type: 'foreign key',
      name: 'fk_follows_follower',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // follows.following_id -> users.id
    await queryInterface.addConstraint('follows', {
      fields: ['following_id'],
      type: 'foreign key',
      name: 'fk_follows_following',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // friendships.user_id -> users.id
    await queryInterface.addConstraint('friendships', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_friendships_user',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // friendships.friend_id -> users.id
    await queryInterface.addConstraint('friendships', {
      fields: ['friend_id'],
      type: 'foreign key',
      name: 'fk_friendships_friend',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // saved_posts.post_id -> posts.id
    await queryInterface.addConstraint('saved_posts', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_saved_posts_post',
      references: {
        table: 'posts',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // saved_posts.user_id -> users.id
    await queryInterface.addConstraint('saved_posts', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_saved_posts_user',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // shares.post_id -> posts.id
    await queryInterface.addConstraint('shares', {
      fields: ['post_id'],
      type: 'foreign key',
      name: 'fk_shares_post',
      references: {
        table: 'posts',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // shares.user_id -> users.id
    await queryInterface.addConstraint('shares', {
      fields: ['user_id'],
      type: 'foreign key',
      name: 'fk_shares_user',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // reports.reporter_id -> users.id
    await queryInterface.addConstraint('reports', {
      fields: ['reporter_id'],
      type: 'foreign key',
      name: 'fk_reports_user',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // report_actions.report_id -> reports.id
    await queryInterface.addConstraint('report_actions', {
      fields: ['report_id'],
      type: 'foreign key',
      name: 'fk_report_actions_report',
      references: {
        table: 'reports',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

    // report_actions.admin_id -> users.id
    await queryInterface.addConstraint('report_actions', {
      fields: ['admin_id'],
      type: 'foreign key',
      name: 'fk_report_actions_admin',
      references: {
        table: 'users',
        field: 'id'
      },
      onDelete: 'CASCADE'
    });

  },

  async down(queryInterface) {
    await queryInterface.removeConstraint('posts', 'fk_posts_user');
    await queryInterface.removeConstraint('posts', 'fk_posts_location');
    await queryInterface.removeConstraint('post_media', 'fk_post_media_post');
    await queryInterface.removeConstraint('comments', 'fk_comments_post');
    await queryInterface.removeConstraint('comments', 'fk_comments_user');
    await queryInterface.removeConstraint('likes', 'fk_likes_post');
    await queryInterface.removeConstraint('likes', 'fk_likes_user');
    await queryInterface.removeConstraint('follows', 'fk_follows_follower');
    await queryInterface.removeConstraint('follows', 'fk_follows_following');
    await queryInterface.removeConstraint('friendships', 'fk_friendships_user');
    await queryInterface.removeConstraint('friendships', 'fk_friendships_friend');
    await queryInterface.removeConstraint('saved_posts', 'fk_saved_posts_post');
    await queryInterface.removeConstraint('saved_posts', 'fk_saved_posts_user');
    await queryInterface.removeConstraint('shares', 'fk_shares_post');
    await queryInterface.removeConstraint('shares', 'fk_shares_user');
    await queryInterface.removeConstraint('reports', 'fk_reports_user');
    await queryInterface.removeConstraint('report_actions', 'fk_report_actions_report');
    await queryInterface.removeConstraint('report_actions', 'fk_report_actions_admin');
  }
};
