package Data;

import android.content.Context;

public class DataUtil {
    private static DataUtil instance;

    public final Repository<User> users;
    public final Repository<Notification> notifications;
    public final Repository<Group> groups;
    public final Repository<Equipment> equipments;
    public final Repository<DutySchedule> dutySchedules;
    public final Repository<BorrowRequest> borrowRequests;

    private DataUtil(Context context) {
        users = new Repository<>(new FileDataSource<>(context,"users.json"));
        notifications = new Repository<>(new FileDataSource<>(context,"notifications.json"));
        groups = new Repository<>(new FileDataSource<>(context,"groups.json"));
        equipments = new Repository<>(new FileDataSource<>(context,"equipments.json"));
        dutySchedules = new Repository<>(new FileDataSource<>(context,"dutyschedules.json"));
        borrowRequests = new Repository<>(new FileDataSource<>(context,"borrowrequests.json"));
        fakeData();
    }

    public static DataUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DataUtil(context.getApplicationContext());
        }
        return instance;
    }
    private void fakeData(){
        fakeUserData();
        fakeNotificationData();
    }
    private void fakeUserData(){
        users.add(new User("2022603255", "Nguyễn Thế Anh", "theanh@gmail.com", "0123456789", "Abc123!@#"));
        users.add(new User("2022603256", "Trần Minh Tuấn", "minhtuan@gmail.com", "0987654321", "Pass1234"));
        users.add(new User("2022603257", "Lê Thị Hồng", "hongle@gmail.com", "0911222333", "H0ngPass!"));
        users.add(new User("2022603258", "Phạm Văn An", "vanan@gmail.com", "0909876543", "An123!@#"));
        users.add(new User("2022603259", "Ngô Quang Vinh", "quangvinh@gmail.com", "0922334455", "Vinh@2025"));

        users.add(new User("0000123456", "Admin", "admin@gmail.com", "0124356789", "Abc123!@#"));
    }
    private void fakeNotificationData(){
        notifications.add(new Notification("Tiêu đề: đã duyệt mượn", "Nội dung: yêu cầu duyệt máy chiếu của bạn đã được duyệt"));
        notifications.add(new Notification("Tiêu đề: lịch mới", "Nội dung: Bạn có lịch mới được thêm"));
    }

}
