import { createClient } from "redis";

const redisClient = createClient({
  socket: {
    host: "redis-13203.c278.us-east-1-4.ec2.cloud.redislabs.com", // ví dụ từ Redis Cloud
    port: 13203, // ví dụ: 12345
  },
  username: "default", // hoặc user bạn tạo
  password: "Ym4Kf0Fsec0cXlqgujqGfzIslgxJfmN3",
});

redisClient.on("connect", () => {
  console.log("Redis connected");
});

redisClient.on("end", () => {
  console.log("Redis disconnected");
});

redisClient.on("error", (err) => {
  console.error("Redis error:", err.message);
});

const connectRedis = async () => {
  if (!redisClient.isOpen) {
    await redisClient.connect();
  }
};

const disconnectRedis = async () => {
  if (redisClient.isOpen) {
    await redisClient.quit();
  }
};

export { redisClient, connectRedis, disconnectRedis };
