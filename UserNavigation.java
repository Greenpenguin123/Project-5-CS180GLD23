import java.util.ArrayList;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class UserNavigation {

    private static final String mainOptions = ("[1] Select a product\n[2] Sort MarketPlace\n[3] Search MarketPlace\n[4] View Sellers\n[5] Shopping Cart\n[6] Sign-out");
    private static final String productOptions = ("[1] View Product Detailed Info\n[2] Add Product to Cart \n[3] Return to Main Menu");
    private static final String shoppingCartWelcome = ("Welcome to the Shopping Cart! Here are the items in your cart currently:");
    private static final String shoppingCartOptions = ("[1] Purchase Order\n[2] Remove an item\n[3] Clear Cart\n[4] Purchase history\n[5] Return to Main Menu");
    private static final String selectingProduct = ("Which product would you like to view? Please enter the number only.");
    private static final String invalidInput = ("Please enter a valid input.");

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean signedIn = false;

        //Select a product declarations
        int productChoice = 0;
        String productChoiceString = "";
        boolean productChoiceOngoing = false;

        //Welcome Message/Sign in

        //Main Options Declaration
        int mainOptionsChoice = 0;
        String mainOptionsChoiceString = "";
        boolean mainOptionsChoiceOngoing = false;

        //Product Choice declaration
        int selectedProduct = 0;
        String selectedproductString = "";
        boolean selectedProductOngoing = false;

        //Shopping cart declarations
        boolean inShoppingCart = false;
        int cartOptions = 0;

        do {
            UserAccountManager u = new UserAccountManager();
            signedIn = true;
            mainOptionsChoiceOngoing = true;
            System.out.println("Signed in! How can we help you?");
            Consumer c = new Consumer(UserAccountManager.userName);
            do {
                System.out.println(mainOptions);
                mainOptionsChoiceString = scanner.nextLine();
                try {
                    mainOptionsChoice = Integer.parseInt(mainOptionsChoiceString);
                    switch (mainOptionsChoice) {
                        case 1:
                            //Select a product
                            Consumer.viewMarketPlace();

                            productChoiceOngoing = true;

                            do {
                                System.out.println();
                                System.out.println(productOptions);
                                productChoiceString = scanner.nextLine();

                                try {
                                    productChoice = Integer.parseInt(productChoiceString);

                                    switch (productChoice) {
                                        case 1:
                                            //View Detailed Info
                                            selectedProduct = selectingProduct();
                                            Consumer.showDescription(selectedProduct, scanner);
                                            break;
                                        case 2:
                                            //Add Product to Cart
                                            boolean isValidInput = false;
                                            do {
                                                System.out.println("Please enter the product number to add to the cart:");
                                                selectedproductString = scanner.nextLine();

                                                try {
                                                    selectedProduct = Integer.parseInt(selectedproductString);
                                                    if (selectedProduct > 0 && selectedProduct <= Consumer.productList.size()) {
                                                        c.addProduct(Consumer.productList.get(selectedProduct - 1));
                                                        System.out.println("Product Added to Cart!");
                                                        isValidInput = true;
                                                    } else {
                                                        System.out.println("Invalid product number. Please enter a number between 1 and " + Consumer.productList.size());
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Invalid input. Please enter a valid number.");
                                                }
                                            } while (!isValidInput);
                                            break;
                                        case 3:
                                            //Return to Main Menu
                                            productChoiceOngoing = false;
                                            break;
                                        default:
                                            System.out.println(invalidInput);
                                            break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println(invalidInput);
                                }
                            } while (productChoiceOngoing);
                            signedIn = true;
                            break;
                        case 2:
                            //Sort List
                            Consumer.sortMarketPlace(Consumer.productList, scanner);
                            break;
                        case 3:
                            //Search Marketplace
                            Consumer.searchProducts(Consumer.productList, scanner);
                            break;
                        case 4:
                            //View sellers
                            while (true) {
                                System.out.println("Would you like to sort (S) or View (V) DashBoard");
                                String inputSelection1 = scanner.nextLine().trim().toUpperCase();

                                if (inputSelection1.equals("S")) {
                                    c.sortDashboard(scanner);
                                    break;
                                } else if (inputSelection1.equals("V")) {
                                    c.viewDashboard(Consumer.productList, scanner);
                                    break;
                                } else {
                                    System.out.println("Invalid input. Please enter 'S' to sort or 'V' to view The Seller Dashboard.");
                                }
                            }
                            break;
                        case 5:
                            //Shopping Cart
                            System.out.println(shoppingCartWelcome + "\n");
                            ArrayList<String> cartList =  c.printCart();
                            do {
                                System.out.println("\n" + shoppingCartOptions);
                                try {
                                    cartOptions = Integer.parseInt(scanner.nextLine().trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a valid number.");
                                    continue;
                                }
                                switch (cartOptions) {
                                    case 1:
                                        c.purchaseProduct();
                                        inShoppingCart = false;
                                        break;
                                    case 2:
                                        //Remove an item
                                        while (true) {
                                            System.out.println("Select a product to Remove");

                                            try {
                                                String selection = scanner.nextLine().trim();
                                                int select = Integer.parseInt(selection);

                                                if (cartList.isEmpty()) {
                                                    System.out.println("There is nothing in the cart.");
                                                    break;
                                                } else {
                                                    String selectionString = cartList.get(select - 1);
                                                    String name = getName(selectionString);
                                                    c.removeProduct(name);
                                                    System.out.println("Successfully Removed Product!");
                                                    break;
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Invalid input: please enter a valid number.");
                                            } catch (IndexOutOfBoundsException e) {
                                                System.out.println("Invalid selection: no such product in the cart.");
                                            }
                                        }
                                        break;
                                    case 3:
                                        //Clear Cart
                                        c.clearCart();
                                        System.out.println("Cart Cleared!");
                                        inShoppingCart = true;
                                        break;
                                    case 4:
                                        System.out.println("Would you like to export (E) or View (V) your Purchase History");
                                        String inputSelection = scanner.nextLine().trim().toUpperCase();
                                        if (inputSelection.equals("E")) {
                                            c.exportPurchaseHistory();
                                            System.out.println("Exporting Purchase History...");
                                            break;
                                        } else if (inputSelection.equals("V")) {
                                            c.printPurchaseHistory();
                                            System.out.println("Viewing Purchase History...");
                                            break;
                                        } else {
                                            System.out.println("Invalid input. Please enter 'E' to export or 'V' to view your Purchase History.");
                                        }
                                        inShoppingCart = true;
                                    case 5:
                                        //Return to Main Menu
                                        inShoppingCart = false;
                                        mainOptionsChoiceOngoing = true;
                                        break;
                                    default:
                                        //Invalid Input
                                        System.out.println(invalidInput);
                                        inShoppingCart = true;
                                        mainOptionsChoiceOngoing = false;
                                        break;
                                }
                            } while (inShoppingCart);
                            signedIn = true;
                            break;
                        case 6:
                            //Return to sign-in/Sign-out
                            System.out.println("Thank you for Using P4 MarketPlace!");
                            mainOptionsChoiceOngoing = false;
                            signedIn = false;
                            break;
                        default:
                            System.out.println(invalidInput);
                            break;
                    }
                } catch (NumberFormatException e) {
                    //System.out.println(invalidInput);
                }
            } while (mainOptionsChoiceOngoing);
        } while (signedIn);
    }
    public static int selectingProduct() {
        int selectedProduct;

        System.out.println(selectingProduct);
        selectedProduct = scanner.nextInt();
        scanner.nextLine();

        return selectedProduct;
    }
    public static String getName(String input) {
        String[] parts = input.split(",");

        if (parts.length > 0) {
            return parts[0];
            } else {
                System.out.println("Invalid input format.");
            }
        return null;
    }
}
