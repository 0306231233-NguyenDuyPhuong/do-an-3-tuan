import { Op, where } from "sequelize";
import db, { Sequelize } from "../models";

const getConversation = async (req, res) => {
  try {
   // const currentUserId = req.user.userId;
    const currentUserId = 4;
    const conversations = await db.Conversation.findAll({
      where: {
        type: 0, // private
      },
      include: [
        {
          model: db.ConversationMember,
          as: "members",
          required: true,
          include: [
            {
              model: db.User,
              as: "User",
              attributes: ["id", "full_name", "avatar"],
            },
          ],
        },
        {
          model: db.Message,
          as: "messages",
          limit: 1,
          order: [["created_at", "DESC"]],
        },
      ],
      order: [["created_at", "DESC"]],
    });

    // chỉ giữ conversation có currentUser
    const filtered = conversations.filter((conv) =>
      conv.members.some((m) => m.user_id === currentUserId)
    );

    if (!filtered.length) {
      return res.status(200).json({
        message: "No conversations",
        data: [],
      });
    }

    return res.status(200).json({
      message: "Get conversation successfully",
      data: filtered,
    });
  } catch (error) {
    console.error("getConversation error:", error);
    return res.status(500).json({ message: "Server error" });
  }
};

const getMessages = async (req, res) => {
  try {
    const currentUserId = req.user.userId;
    const { receiverId } = req.params;
    const receiver = await db.User.findByPk(receiverId);
    if (Number(currentUserId) === Number(receiverId))
      return res
        .status(400)
        .json({
          message: "Don't have message by yourself"
         });
    if (!receiver) return res.status(404).json({ message: "User not found" });

    const existingConversion = await db.Conversation.findOne({
      where: {
        type: 0,
      },
      include: [
        {
          model: db.ConversationMember,
          as: "members",
          where: {
            user_id: {
              [Op.in]: [currentUserId, receiverId],
            },
          },
          required: true,
        },
      ],
      group: ["Conversation.id"],
      having: Sequelize.literal("COUNT(DISTINCT `members`.`user_id`) = 2"),
      subQuery: false,
    });
    if (!existingConversion)
      return res.status(200).json({
        message: "No messages yet",
        data: [],
      });

    const messages = await db.Message.findAll({
      where: {
        conversation_id: existingConversion.id,
      },
      include: [
        {
          model: db.User,
          as: "User",
          attributes: ["id", "full_name", "avatar"],
        },
      ],
      order: [["created_at", "ASC"]],
    });
    return res.status(200).json({
      message: "Get messages successfully",
      data: messages,
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: "Server error" });
  }
};

const postMessage = async(req,res)=>{
  try {
      const {conversation_id, sender_id} = req.body;
  const [conversationCheck, senderCheck] = await Promise.all([
    db.Conversation.findOne({id:conversation_id}),
    db.User.findOne({id:sender_id})
  ])
  if(!conversationCheck && !senderCheck){
    return res.status(404).json({
      message:"Conversation or user not found"
    })
  } 
  await db.Message.create(req.body)
  return res.status(201).json({
    message: "Insert message success"
  })
  } catch (error) {
    return res.status(500).json({
      error: error
    })
  }
}
export default {
  getConversation,
  getMessages,
  postMessage
};
