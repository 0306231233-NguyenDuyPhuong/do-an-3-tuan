import { Server } from "socket.io";
import db, { Sequelize } from "../models";
import { Op, where } from "sequelize";
import jwt from "jsonwebtoken";
import dotenv from "dotenv";
dotenv.config();
const initSocket = (server) => {
  const io = new Server(server, {
    cors: {
      origin: "*",
      methods: ["GET", "POST"],
      credentials: true,
    },
  });

  io.use((socket, next) => {
  try {
    let token =
      socket.handshake.auth?.token || socket.handshake.query?.token;

    if (!token) return next(new Error("No token"));

    // ✅ FIX QUAN TRỌNG
    if (token.startsWith("Bearer ")) {
      token = token.split(" ")[1];
    }

    const decoded = jwt.verify(token, process.env.ACCESS_TOKEN_SECRET);
    socket.user = decoded;
    next();
  } catch (err) {
    console.log("AUTH ERROR:", err.message);
    next(new Error("Unauthorized"));
  }
});


  const userSockets = new Map();

  //auth va connect thanh cong
  io.on("connection", (socket) => {
    // get id from auth socket
    const senderId = socket.user.userId;
    console.log("Client connected:", socket.id);
    userSockets.set(senderId, socket.id);
    socket.on("send_message_to_user", async (data) => {
      try {
        const { receiverId, content } = data;
        if (!receiverId || !content) {
          socket.emit("error", { message: "Missing required fields" });
          return;
        }
        if (senderId === receiverId) {
          socket.emit("error", { message: "Cannot send message to yourself" });
          return;
        }
        const recerver = await db.User.findByPk(receiverId);
        if (!recerver) return;
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
                  [Op.in]: [senderId, receiverId],
                },
              },
              required: true,
            },
          ],
          group: ["Conversation.id"],
          having: Sequelize.literal("COUNT(DISTINCT `members`.`user_id`) = 2"),
          subQuery: false,
        });
        let conversationId;

        if (!existingConversion) {
          const newConverstion = await db.Conversation.create({
            type: 0,
            name: null,
          });
          conversationId = newConverstion.id;
          await db.ConversationMember.bulkCreate([
            {
              conversation_id: conversationId,
              user_id: senderId,
            },
            {
              conversation_id: conversationId,
              user_id: receiverId,
            },
          ]);
          console.log(`Created new conversation: ${conversationId}`);
        } else {
          conversationId = existingConversion.id;
          console.log(`Found existing conversation: ${existingConversion.id}`);
        }

        const message = await db.Message.create({
          conversation_id: conversationId,
          sender_id: senderId,
          message_type: 0,
          content,
        });

        const sender = await db.User.findByPk(senderId, {
          attributes: ["id", "full_name", "avatar"],
        });

        const messageData = {
          id: message.id,
          conversationId: conversationId,
          senderId: senderId,
          content: message.content,
          messageType: message.message_type,
          createdAt: message.created_at,
          sender: sender.toJSON(),
        };
        console.log(messageData);
        // giu cho receiverId neu ho online
        const receiverSocketId = userSockets.get(receiverId);

        if (receiverSocketId) {
          // Gửi đến receiver
          io.to(receiverSocketId).emit("receive_message", messageData);
          console.log(
            `Sent message to receiver ${receiverId} (socket: ${receiverSocketId})`
          );
        } else {
          console.log(`Receiver ${receiverId} is offline`);
        }
        //thong cho sender giu thanh cong
        socket.emit("message_sent", {
          success: true,
          message: messageData,
          conversationId: conversationId,
        });
      } catch (error) {
        console.error("Send message error:", error);
        socket.emit("error", { message: "Failed to send message" });
      }
    });

    socket.on("disconnect", () => {
      userSockets.delete(senderId);
      console.log(`User ${senderId} disconnected`);
    });
  });

  return io;
};

export default initSocket;
