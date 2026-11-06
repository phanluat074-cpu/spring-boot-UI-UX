# Báo cáo kiểm thử tích hợp (Integration Test Report)

**Dự án:** `demo` (Spring Boot 4.0.6)  
**Ngày:** 2026-05-14  
**Cách chạy:** từ thư mục `demo`, thực thi `.\mvnw.cmd test` (JDK 17). Các kiểm thử nghiệp vụ dùng profile Spring **`test`** với H2 in-memory; không cần SQL Server.

---

## 1. Tổng quan kết quả

| Hạng mục | Kết quả |
|----------|---------|
| Số kịch bản tích hợp (nghiệp vụ + smoke) | **8** (≥ 5 theo yêu cầu) |
| Trạng thái lần chạy gần nhất | **Đạt** (BUILD SUCCESS) |
| Thời gian chạy test (tham khảo) | ~10 giây |

---

## 2. Danh sách kịch bản

| Mã | Mô tả luồng | Cách kiểm tra |
|----|-------------|----------------|
| IT-00 | Khởi tạo context đầy đủ (JPA, bean, cấu hình profile `test`) | `@SpringBootTest` + `@ActiveProfiles("test")` — context không lỗi khi start |
| IT-01 | Trang chủ | `GET /` → 200, view `home` |
| IT-02 | Bảo vệ khu vực admin | `GET /admin` không session → 302 tới `/login` |
| IT-03 | Đăng nhập admin và vào dashboard | `POST /login` đúng `app.admin.*` (profile test) → 302 `/admin`; `GET /admin` cùng session → 200, view `admin/index` |
| IT-04 | Danh sách bài theo danh mục | `GET /danhmuc/1` → 200, view `baihoc-list` |
| IT-05 | Bài học không tồn tại | `GET /baihoc/999999` → 302 về `/` |
| IT-06 | API chấm trắc nghiệm (trường hợp lỗi nghiệp vụ) | `POST /api/quiz/check` với `maDA` không tồn tại → JSON `ok: false` |
| IT-07 | API chấm lab (trường hợp lỗi nghiệp vụ) | `POST /api/lab/check` với `maTH` không tồn tại → JSON `ok: false` |

**Mã nguồn:** `src/test/java/com/example/demo/ApplicationBusinessIntegrationTest.java`

---

## 3. Lỗi đã gặp trong quá trình triển khai và cách khắc phục

### 3.1. `AutoConfigureMockMvc` không biên dịch được (Spring Boot 4)

- **Hiện tượng:** `package org.springframework.boot.test.autoconfigure.web.servlet does not exist`, không tìm thấy `@AutoConfigureMockMvc`.
- **Nguyên nhân:** Spring Boot 4 tách/đổi cấu trúc module test; annotation cũ không còn trong classpath như kỳ vọng ban đầu.
- **Khắc phục:** Bỏ `@AutoConfigureMockMvc`, dùng `MockMvcBuilders.webAppContextSetup(webApplicationContext).build()` trong `@BeforeEach` (vẫn kiểm thử đầy đủ luồng MVC trên context thật).

### 3.2. `GET /admin` trả 200 thay vì chuyển hướng tới `/login`

- **Hiện tượng:** Kiểm thử mong đợi 302 nhưng nhận 200 và view admin.
- **Nguyên nhân:** `MockMvc` dựng bằng `webAppContextSetup` **không tự gắn** `Filter` servlet (`AdminAuthFilter`) vào chuỗi xử lý giống server thật trong cấu hình mặc định này.
- **Khắc phục:** Gọi `.addFilters(adminAuthFilter)` khi build `MockMvc`, inject `AdminAuthFilter` từ context.

### 3.3. Kiểm thử cần DB nhưng môi trường không có SQL Server

- **Hiện tượng:** `@SpringBootTest` không profile sẽ trỏ JDBC SQL Server và có thể fail ngoài máy dev.
- **Khắc phục:** Thêm dependency **H2** (scope `test`), file `src/test/resources/application-test.properties` ghi đè datasource + JPA (`ddl-auto=create-drop`), bật `@ActiveProfiles("test")`. Gắn `@Profile("!test")` lên `DatabaseConnectionCheck` để runner `SELECT 1` không chạy trên profile test (tránh phụ thuộc kiểu server cụ thể).

---

## 4. Gợi ý mở rộng (không bắt buộc)

- Bổ sung seed dữ liệu trong `@Sql` hoặc `@BeforeEach` để kiểm thử `GET /baihoc/{id}` với bài học có quiz/lab thật và assert JSON `correct: true/false`.
- Nếu dùng lại `@AutoConfigureMockMvc` trong tương lai, cần đối chiếu tài liệu Spring Boot 4 cho đúng artifact/import.
