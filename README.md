# Hotel Managerment System

BTL môn KTPM - IT4082 - 156724, 2024.2, SoICT

# Nhóm 25

- Phạm Mạnh Quyết - 20225663 **(Team Leader)**
- Đặng Hải Anh - 20225688
- Vũ Ngọc Đức - 20225816
- Nguyễn Thanh Hiếu - 20225716
- Lê Thị Quỳnh - 20225917

# Cấu trúc thư mục

```
src/main/java/com/app/
├── controllers/ # chứa các file controller
├── models/ # chứa các mô hình dữ liệu
├── utils/ # chứa các hàm chức năng tái sử dụng
├── views/ # chứa các file chạy riêng từng màn hình ứng dụng
└── Main.java # file khởi chạy dự án

src/main/resources/
├── fxml/ # chứa các file UI
├── database/ # chứa dữ liệu của dự án (import vào 3306)
├── input_reports/ # chứa các file báo cáo thu phí
├── output_reports/ # chứa các file báo cáo được xuất ra
├── images/ # chứa các file ảnh
└── styles/ # chứa các file CSS và thư mục chứa fonts sử dụng
```

# Khởi chạy dự án

## 1. Tạo database

Import file `bluemoon_sys.sql` trong `src/main/resources/database` vào app
database của bạn

## 2. Khởi chạy dự án

Truy cập `src/main/java/com/app` vào Run file `Main.java`

# Các tài khoản sẵn có

### Quản trị viên

```
TK: admin@gmail.com
MK: admin
```

### Kế toán

```
TK: test@gmail.com
MK: 111
```

