import java.util.Scanner;

public class PlaneManagement {

    private static final Ticket[] tickets = new Ticket[50];

    public static void main(String[] args) {

        // Initializing the seating rows
        int[] row1 = new int[14];
        int[] row2 = new int[12];
        int[] row3 = new int[12];
        int[] row4 = new int[14];

        int[][] seatingPlan = {
                row1,
                row2,
                row3,
                row4
        };

        System.out.print("Welcome to the Plane Management application");

        Scanner scanner = new Scanner(System.in);
        String menu =
                """

                        *********************************************
                        *                   Menu                   *
                        1) Buy a seat
                        2) Cancel a seat
                        3) Find first available seat
                        4) Show seating plan
                        5) Print tickets information and total sales
                        6) Search ticket
                        0) Quit
                         *********************************************""";

        int choice;
        do {
            System.out.println(menu);
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> buy_seat(seatingPlan, scanner);
                case 2 -> cancel_seat(seatingPlan, scanner);
                case 3 -> find_first_available(seatingPlan);
                case 4 -> show_seating_plan(seatingPlan);
                case 5 -> print_tickets_info();
                case 6 -> search_ticket(scanner);
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

        scanner.close();
    }

    public static void buy_seat(int[][] seatingPlan, Scanner scanner) {
        System.out.print("Enter row letter (A-D): ");
        char row;
        while (true) {
            String input = scanner.next();
            if (input.length() == 1 && input.charAt(0) >= 'A' && input.charAt(0) <= 'D') {
                row = input.toUpperCase().charAt(0);
                break;
            } else {
                System.out.println("Invalid row. Please enter a single letter between A and D.");
            }
        }
        int rowIndex = row - 'A';

        System.out.print("Enter seat number (1-" + seatingPlan[rowIndex].length + "): ");
        int seatNumber;
        while (true) {
            if (scanner.hasNextInt()) {
                seatNumber = scanner.nextInt();
                if (seatNumber >= 1 && seatNumber <= seatingPlan[rowIndex].length) {
                    break;
                } else {
                    System.out.println("Invalid seat number. Please enter a number between 1 and " + seatingPlan[rowIndex].length + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
        }

        if (seatingPlan[rowIndex][seatNumber - 1] == 1) {
            System.out.println("Seat is already taken. Please select another seat.");
            return;
        }

        scanner.nextLine(); // Consume newline character
        System.out.print("Enter name: ");
        String name = validateName(scanner);

        System.out.print("Enter surname: ");
        String surname = validateName(scanner);

        System.out.print("Enter email: ");
        String email = validateEmail(scanner);

        // Calculate ticket price based on seat number
        double price;
        if (seatNumber <= 5) {
            price = 200;
        } else if (seatNumber <= 9) {
            price = 150;
        } else {
            price = 180;
        }

        Person person = new Person(name, surname, email);
        Ticket ticket = new Ticket(String.valueOf(row), seatNumber, price, person);

        // Add ticket to the array
        for (int i = 0; i < tickets.length; i++) {
            if (tickets[i] == null) {
                tickets[i] = ticket;
                seatingPlan[rowIndex][seatNumber - 1] = 1;
                System.out.println("Seat " + row + seatNumber + " purchased successfully.");
                ticket.save(); // Save ticket information to a file
                return;
            }
        }
        System.out.println("No more tickets can be sold.");
    }

    public static void cancel_seat(int[][] seatingPlan, Scanner scanner) {
        System.out.print("Enter row letter (A-D) of the seat to cancel: ");
        char row = scanner.next().toUpperCase().charAt(0);

        System.out.print("Enter seat number (1-14) of the seat to cancel: ");
        int seatNumber = scanner.nextInt();

        for (int i = 0; i < tickets.length; i++) {
            Ticket ticket = tickets[i];
            if (ticket != null && ticket.getRow().equals(String.valueOf(row)) && ticket.getSeat() == seatNumber) {
                tickets[i] = null; // Remove the ticket
                System.out.println("Seat " + row + seatNumber + " canceled successfully.");
                int rowIndex = row - 'A';
                seatingPlan[rowIndex][seatNumber - 1] = 0; // Update seating plan to make seat available again
                return;
            }
        }
        System.out.println("Ticket not found for seat " + row + seatNumber);
    }

    public static void find_first_available(int[][] seatingPlan) {
        for (int i = 0; i < seatingPlan.length; i++) {
            for (int j = 0; j < seatingPlan[i].length; j++) {
                if (seatingPlan[i][j] == 0) {
                    char row = (char) ('A' + i);
                    int seatNumber = j + 1;
                    System.out.println("First available seat is in row " + row + ", seat " + seatNumber + ".");
                    return;
                }
            }
        }
        System.out.println("All seats are taken.");
    }

    public static void show_seating_plan(int[][] seatingPlan) {
        System.out.println("Seating Plan:");
        for (int i = 0; i < seatingPlan.length; i++) {
            char row = (char) ('A' + i);
            System.out.print(row + ": ");
            for (int j = 0; j < seatingPlan[i].length; j++) {
                if (seatingPlan[i][j] == 0) {
                    System.out.print("O ");
                } else {
                    System.out.print("X ");
                }
            }
            System.out.println();
        }
    }

    public static void print_tickets_info() {
        System.out.println("Tickets sold during the session:");
        for (Ticket ticket : tickets) {
            if (ticket != null) {
                ticket.printTicketInfo();
                System.out.println();
            }
        }

        System.out.println("Total amount: £" + Ticket.getTotalAmountSold());

    }

    public static void search_ticket(Scanner scanner) {
        System.out.print("Enter row letter (A-D) of the seat to search: ");
        char row = scanner.next().toUpperCase().charAt(0);

        System.out.print("Enter seat number (1-14) of the seat to search: ");
        int seatNumber = scanner.nextInt();

        for (Ticket ticket : tickets) {
            if (ticket != null && ticket.getRow().equals(String.valueOf(row)) && ticket.getSeat() == seatNumber) {
                System.out.println("Ticket information:");
                ticket.printTicketInfo();
                return;
            }
        }
        System.out.println("This seat is available.");
    }

    public static String validateName(Scanner scanner) {
        String name;
        while (true) {
            name = scanner.nextLine();
            if (name.matches("[a-zA-Z]+")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid name.");
            }
        }
        return name;
    }

    public static String validateEmail(Scanner scanner) {
        String email;
        while (true) {
            email = scanner.nextLine();
            if (email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid email address.");
            }
        }
        return email;
    }
}

