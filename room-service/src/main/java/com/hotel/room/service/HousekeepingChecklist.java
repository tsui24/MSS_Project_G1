package com.hotel.room.service;

import java.util.List;

public final class HousekeepingChecklist {
    private static final List<String> CLEANING = List.of(
            "Mở cửa và thông gió phòng; kiểm tra đồ khách bỏ quên",
            "Thu gom rác, đồ vải bẩn và vật dụng đã sử dụng",
            "Tháo ga, vỏ gối; thay toàn bộ đồ vải sạch",
            "Làm sạch và khử khuẩn phòng tắm, bổ sung amenities",
            "Lau bụi đồ nội thất, thiết bị và các bề mặt tiếp xúc",
            "Kiểm tra, bổ sung minibar và nước uống",
            "Hút bụi/lau sàn từ trong ra ngoài",
            "Kiểm tra đèn, điều hòa, TV, khóa cửa và báo hư hỏng",
            "Setup phòng theo tiêu chuẩn và kiểm tra lần cuối"
    );
    private static final List<String> GENERAL = List.of(
            "Kiểm tra hiện trạng phòng",
            "Thực hiện công việc được giao",
            "Kiểm tra kết quả và báo cáo bất thường"
    );

    private HousekeepingChecklist() {}

    public static List<String> steps(String taskType) {
        return "CLEANING".equalsIgnoreCase(taskType) ? CLEANING : GENERAL;
    }
}
