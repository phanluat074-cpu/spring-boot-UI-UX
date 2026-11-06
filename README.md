# Hướng dẫn chạy dự án

Ứng dụng web Spring Boot (Maven) — nền tảng học tập UI/UX: danh mục, bài học, trắc nghiệm, tài nguyên, lab tương tác và trang quản trị.

## Yêu cầu môi trường

| Thành phần | Phiên bản |
| --- | --- |
| JDK | **17.0.9** |
| Apache Tomcat | **10.1.17** (Jakarta Servlet 6 / Tomcat 10) |
| IDE | **Apache NetBeans 17** |
| Cơ sở dữ liệu | **Microsoft SQL Server** |
| Build | Maven (đã có Maven Wrapper) |

File SQL có sẵn trong source: [`SQLUIUX.sql`](SQLUIUX.sql)

---

## 1. Cài JDK 17.0.9

1. Cài **Eclipse Temurin / Oracle JDK 17.0.9**.
2. Đặt biến môi trường:
   - `JAVA_HOME` = thư mục JDK 17.0.9 (ví dụ `C:\Program Files\Java\jdk-17.0.9`)
   - Thêm `%JAVA_HOME%\bin` vào `PATH`
3. Kiểm tra:

```bat
java -version
```

Kết quả mong đợi có `17.0.9`.

---

## 2. Cài SQL Server và tạo database

1. Cài **SQL Server** (Express hoặc Developer) và bật **SQL Server Authentication** (Mixed Mode) nếu dùng tài khoản `sa`.
2. Bật giao thức **TCP/IP**, cổng mặc định **1433**.
3. Mở **SQL Server Management Studio (SSMS)** hoặc `sqlcmd`, đăng nhập bằng tài khoản có quyền tạo database.
4. Tạo database **QLHV** (script `SQLUIUX.sql` bắt đầu bằng `USE QLHV`, nên database phải tồn tại trước):

```sql
CREATE DATABASE QLHV;
GO
```

5. Chạy toàn bộ file **`SQLUIUX.sql`** trong thư mục gốc repo:
   - SSMS: File → Open → chọn `SQLUIUX.sql` → Execute (`F5`)
   - Hoặc:

```bat
sqlcmd -S localhost -U sa -P sa -i SQLUIUX.sql
```

Script tạo các bảng: `DANHMUC`, `BAIHOC`, `INTERACTIVE_LAB`, `TAINGUYEN`, `TRACNGHIEM`, `DAPAN`.

Hibernate **không** tự tạo schema (`spring.jpa.hibernate.ddl-auto=none`). Bắt buộc chạy file SQL trước khi start ứng dụng.

### Cấu hình kết nối mặc định

File: `src/main/resources/application.properties`

| Tham số | Giá trị mặc định |
| --- | --- |
| Host | `localhost` |
| Port | `1433` |
| Database | `QLHV` |
| User | `sa` |
| Password | `sa` |

Nếu máy bạn khác thông tin trên, sửa file properties hoặc đặt biến môi trường: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.

---

## 3. Cài Tomcat 10.1.17 và NetBeans 17

### Tomcat 10.1.17

1. Tải và giải nén **Apache Tomcat 10.1.17**.
2. Trong NetBeans: **Tools → Servers → Add Server** → chọn **Apache Tomcat or TomEE** → trỏ tới thư mục Tomcat 10.1.17.
3. Gán server này dùng **JDK 17.0.9**.

Dự án hiện đóng gói **JAR** và chạy **Tomcat nhúng** của Spring Boot (cổng **8115**). Đăng ký Tomcat 10.1.17 trong NetBeans để đồng bộ môi trường (compile/Jakarta EE) và chạy/debug như mục 4.

### NetBeans 17

1. Cài **Apache NetBeans 17**.
2. **Tools → Java Platforms → Add Platform** → chọn JDK **17.0.9**, đặt làm platform mặc định nếu cần.
3. **Tools → Options → Java → Maven**: có thể dùng Maven đi kèm NetBeans hoặc Maven Wrapper (`mvnw.cmd`) trong thư mục này.

---

## 4. Mở và chạy dự án trên NetBeans 17

1. **File → Open Project** → chọn thư mục Maven chứa `pom.xml` (thư mục gốc repo này).

2. Chuột phải project → **Properties**:
   - **Sources / Build**: Java Platform = **JDK 17.0.9**
   - **Source/Binary Format**: **17**

3. Đảm bảo SQL Server đang chạy và database `QLHV` đã có bảng.

4. Chuột phải project → **Run**  
   (hoặc mở `com.example.demo.DemoApplication` → Run File)

   NetBeans chạy `main` của Spring Boot, khởi động Tomcat nhúng.

5. Mở trình duyệt:

| Trang | URL |
| --- | --- |
| Trang chủ | http://localhost:8115/ |
| Đăng nhập admin | http://localhost:8115/login |
| Quản trị | http://localhost:8115/admin |

### Tài khoản quản trị (mặc định)

- Username: `admin`
- Password: `admin123`

Có thể đổi bằng biến môi trường `ADMIN_USERNAME` / `ADMIN_PASSWORD` hoặc sửa `application.properties`.

Cổng mặc định **8115**. Đổi bằng `SERVER_PORT` hoặc `server.port` trong `application.properties`.

---

## 5. Chạy bằng Maven (không cần NetBeans)

Trong thư mục gốc repo (chứa `pom.xml`):

```bat
mvnw.cmd spring-boot:run
```

Hoặc build rồi chạy JAR:

```bat
mvnw.cmd clean package -DskipTests
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

---

## 6. Deploy lên Tomcat 10.1.17 (tùy chọn)

Cách mặc định là **Tomcat nhúng** (mục 4–5). Nếu cần deploy **external Tomcat 10.1.17**:

1. Đổi `pom.xml`: `<packaging>war</packaging>`, thêm `spring-boot-starter-tomcat` với `scope` `provided`, và class kế thừa `SpringBootServletInitializer`.
2. Build WAR: `mvnw.cmd clean package`
3. Copy file WAR vào `TOMCAT_10.1.17\webapps\`
4. Start Tomcat (`startup.bat`). URL phụ thuộc `server.servlet.context-path` và cổng Tomcat (thường 8080).

Không làm bước này nếu chỉ chạy từ NetBeans như mục 4.

---

## 7. Kiểm tra nhanh khi lỗi

| Hiện tượng | Việc cần kiểm tra |
| --- | --- |
| Không kết nối được SQL Server | SQL Server đã start, TCP 1433, user/password, database `QLHV` |
| Lỗi bảng không tồn tại | Đã `CREATE DATABASE QLHV` rồi chạy `SQLUIUX.sql` |
| Sai phiên bản Java | `java -version` phải là 17.0.9; NetBeans dùng đúng Java Platform |
| Cổng bị chiếm | Đổi `server.port` hoặc tắt process đang giữ cổng 8115 |
| Trang admin yêu cầu login | Vào `/login` với `admin` / `admin123` |

---

## Cấu trúc thư mục (rút gọn)

```
.
├── SQLUIUX.sql              # Script tạo schema SQL Server (QLHV)
├── README.md
├── pom.xml
├── mvnw / mvnw.cmd
└── src/main/
    ├── java/com/example/demo/
    └── resources/
        ├── application.properties
        ├── static/
        └── templates/
```
