import nodemailer from "nodemailer";

// Tạo transporter để gửi email
const createTransporter = () => {
  // Sử dụng Gmail SMTP (cần cấu hình trong .env)
  // Hoặc có thể dùng các service khác như SendGrid, Mailgun, etc.
  return nodemailer.createTransport({
    service: "gmail",
    auth: {
      user: process.env.EMAIL_USER, // Email gửi
      pass: process.env.EMAIL_APP_PASSWORD, // App Password của Gmail
    },
  });
};

// Hàm gửi email chứa mật khẩu mới
export const sendPasswordResetEmail = async (email, newPassword) => {
  try {
    const transporter = createTransporter();

    const mailOptions = {
      from: process.env.EMAIL_USER,
      to: email,
      subject: "Mật khẩu mới của bạn",
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2 style="color: #333;">Yêu cầu đặt lại mật khẩu</h2>
          <p>Xin chào,</p>
          <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu của bạn.</p>
          <p>Mật khẩu mới của bạn là:</p>
          <div style="background-color: #f4f4f4; padding: 15px; border-radius: 5px; margin: 20px 0;">
            <p style="font-size: 18px; font-weight: bold; color: #333; margin: 0; font-family: monospace;">
              ${newPassword}
            </p>
          </div>
          <p>Vui lòng sử dụng mật khẩu này để đăng nhập. Sau khi đăng nhập, bạn có thể thay đổi mật khẩu trong phần cài đặt tài khoản.</p>
          <p style="color: #ff0000; font-weight: bold;">⚠️ Lưu ý: Vui lòng giữ bảo mật thông tin này và không chia sẻ với người khác.</p>
          <p>Trân trọng,<br>Đội ngũ hỗ trợ</p>
        </div>
      `,
    };

    const info = await transporter.sendMail(mailOptions);
    console.log("Email sent successfully:", info.messageId);
    return { success: true, messageId: info.messageId };
  } catch (error) {
    console.error("Error sending email:", error);
    throw new Error(`Failed to send email: ${error.message}`);
  }
};
