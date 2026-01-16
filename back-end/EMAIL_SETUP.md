# Cấu hình Email Service

## Hướng dẫn cấu hình

Để tính năng quên mật khẩu hoạt động, bạn cần cấu hình email service trong file `.env`:

### 1. Sử dụng Gmail SMTP (Khuyến nghị cho development)

1. Tạo App Password cho Gmail:
   - Vào: https://myaccount.google.com/apppasswords
   - Chọn "Mail" và "Other (Custom name)"
   - Nhập tên: "Social Travel App"
   - Copy mã 16 ký tự được tạo

2. Thêm vào file `.env`:
```env
EMAIL_USER=your-email@gmail.com
EMAIL_APP_PASSWORD=your-16-char-app-password
```

### 2. Hoặc sử dụng SMTP khác

Bạn có thể chỉnh sửa file `helpers/emailService.js` để sử dụng SMTP service khác như:
- SendGrid
- Mailgun
- AWS SES
- Outlook/Hotmail

Ví dụ với SMTP tùy chỉnh:
```javascript
const transporter = nodemailer.createTransport({
  host: "smtp.example.com",
  port: 587,
  secure: false, // true for 465, false for other ports
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASSWORD,
  },
});
```

### 3. Cài đặt dependencies

Chạy lệnh sau để cài đặt nodemailer:
```bash
npm install
```

hoặc nếu chưa có nodemailer:
```bash
npm install nodemailer
```

## Lưu ý

- **Development**: Sử dụng Gmail App Password là cách đơn giản nhất
- **Production**: Nên sử dụng email service chuyên nghiệp như SendGrid hoặc AWS SES để đảm bảo deliverability cao
