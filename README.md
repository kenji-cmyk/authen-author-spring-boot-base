# Authen Author - Spring Boot + React

Hệ thống authentication & authorization với Spring Boot backend và React frontend.

## Cấu trúc project

```
.
├── backend/          # Spring Boot application
│   ├── src/
│   ├── pom.xml
│   └── mvnw.cmd
│
└── frontend/         # React + TypeScript + Tailwind CSS
    ├── src/
    ├── package.json
    └── vite.config.ts
```

## Backend (Spring Boot)

### Chạy backend

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

### API Endpoints

- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register
- `POST /api/auth/verify-2fa` - Verify 2FA
- `POST /api/auth/enable-2fa` - Enable 2FA
- `POST /api/auth/disable-2fa` - Disable 2FA
- `GET /api/users/me` - Get current user
- `GET /api/users` - Get all users (Admin only)

## Frontend (React)

### Cài đặt dependencies

```bash
cd frontend
npm install
```

### Chạy development server

```bash
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:3000`

### Build production

```bash
npm run build
```

## Features

### Backend
- ✅ Local authentication (username/password)
- ✅ OAuth2 (Google, GitHub)
- ✅ JWT tokens (access + refresh)
- ✅ Two-Factor Authentication (2FA/MFA)
- ✅ Role-based access control (USER, ADMIN)
- ✅ Password validation
- ✅ H2 in-memory database

### Frontend
- ✅ Animated login page với cartoon characters
- ✅ Characters theo dõi chuột
- ✅ Characters nhìn nhau khi typing
- ✅ Characters nhìn trộm khi show password
- ✅ Responsive design
- ✅ Tailwind CSS + shadcn/ui components
- ✅ TypeScript
- ✅ API integration với backend

## Cấu hình

### Backend Configuration

File: `backend/src/main/resources/application.properties`

```properties
# JWT
jwt.secret=your-secret-key
jwt.expiration=900000
jwt.refresh-expiration=86400000

# OAuth2 Google
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET

# OAuth2 GitHub
spring.security.oauth2.client.registration.github.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_CLIENT_SECRET
```

### Frontend Proxy

Vite tự động proxy các request `/api` và `/oauth2` đến backend (localhost:8080).

## Tech Stack

### Backend
- Java 17+
- Spring Boot 4.0.5
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (jjwt)
- Google Authenticator (2FA)

### Frontend
- React 18
- TypeScript
- Vite
- Tailwind CSS
- shadcn/ui
- Radix UI
- Lucide Icons

## Development

1. Start backend:
```bash
cd backend
.\mvnw.cmd spring-boot:run
```

2. Start frontend (terminal mới):
```bash
cd frontend
npm run dev
```

3. Truy cập: `http://localhost:3000`

## Demo Credentials

```
Email: erik@gmail.com
Password: 1234
```

(Hoặc tạo tài khoản mới qua Register)
