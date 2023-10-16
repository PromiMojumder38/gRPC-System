import com.demo.grpc.User;
import com.demo.grpc.userGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;
import java.util.logging.Logger;

public class Consumer {
    private static final Logger logger = Logger.getLogger(Consumer.class.getName());

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8082)
                .usePlaintext()
                .build();

        userGrpc.userBlockingStub userStub = userGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the User Management System.");
        System.out.println("Please select an option:");
        System.out.println("1. Log in");
        System.out.println("2. Register");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            // User chose to log in
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            User.LoginReq request = User.LoginReq.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .build();

            User.APIRes loginResponse = userStub.login(request);
            logger.info("Login response code: " + loginResponse.getResCode());
            logger.info("Login message: " + loginResponse.getMessage());

            boolean loggedIn = "SUCCESS".equals(loginResponse.getMessage());

            while (loggedIn) {
                System.out.println("What would you like to do?");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. Log Out");
                System.out.print("Enter your choice (1, 2, or 3): ");
                int option = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (option == 1) {
                    User.ProfileReq profileRequest = User.ProfileReq.newBuilder()
                            .setUsername(username)
                            .build();
                    User.ProfileRes profileResponse = userStub.getProfile(profileRequest);
                    System.out.println("User Profile:");
                    System.out.println("Username: " + profileResponse.getUsername());
                    System.out.println("Email: " + profileResponse.getEmail());
                } else if (option == 2) {
                    System.out.print("Enter your new email: ");
                    String newEmail = scanner.nextLine();

                    System.out.print("Enter your new password: ");
                    String newPassword = scanner.nextLine();

                    User.UpdateProfileReq updateProfileRequest = User.UpdateProfileReq.newBuilder()
                            .setUsername(username)
                            .setEmail(newEmail)
                            .setPassword(newPassword)
                            .build();

                    User.APIRes updateProfileResponse = userStub.updateProfile(updateProfileRequest);
                    System.out.println("Profile update response: " + updateProfileResponse.getMessage());
                } else if (option == 3) {
                    System.out.println("Logging out...");
                    loggedIn = false;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        } else if (choice == 2) {
            System.out.print("Enter your desired username: ");
            String username = scanner.nextLine();

            System.out.print("Enter your email address: ");
            String email = scanner.nextLine();

            System.out.print("Set a password: ");
            String password = scanner.nextLine();

            User.RegisterReq registrationRequest = User.RegisterReq.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setEmail(email)
                    .build();

            User.APIRes registrationResponse = userStub.register(registrationRequest);
            logger.info("Registration response code: " + registrationResponse.getResCode());
            logger.info("Registration message: " + registrationResponse.getMessage());
        } else {
            System.out.println("Invalid choice. Please select 1 or 2.");
        }

        System.out.println("Thank you for using the User Management System.");
        scanner.close();
        channel.shutdown();
    }
}
