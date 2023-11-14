import java.util.ArrayList;
import java.util.Scanner;

public class UserNavigation {

    private static final String welcomeMessage = ("Hello, welcome to the P4 Marketplace! Please Sign-in or Create an Account to continue.");
    private static final String mainOptions = ("1. Select a product\n2. View Sellers\n3. Shopping Cart\n4. Sign-out");
    private static final String productOptions = ("1. View Product Detailed Info\n2. Add Product to Cart \n3. Return to Main Menu");
    private static final String shoppingCartWelcome = ("Welcome to the Shopping Cart! Here are the items in your cart currently:");
    private static final String shoppingCartOptions = ("1. Purchase Order\n2. Remove an item\n3. Clear Cart\n4. Return to Main Menu");
    private static final String selectingProduct = ("Which product would you like to view? Please enter the number only.");
    private static final String invalidInput = ("Please enter a valid input.");

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean signedIn = false;

        boolean choiceOngoing = true;
        int mainOptionsChoice = 0;

        //Select a product declarations
        int productChoice = 0;
        boolean productChoiceOngoing = false;

        //Welcome Message/Sign in
        System.out.println(welcomeMessage);

        //Shopping cart declarations
        boolean inShoppingCart = false;
        int cartOptions = 0;

        do {
            //TODO run sign-in method
            signedIn = true;
            System.out.println("Signed in! How can we help you?");

            do {
                System.out.println(mainOptions);

                mainOptionsChoice = scanner.nextInt();
                scanner.nextLine();

                switch (mainOptionsChoice) {
                    case 1:
                        //Select a product
                        //TODO Print out products

                        //junk code TODO REMOVE THIS LATER
                        System.out.println("1. Product A ----- SellingStore ----- $1.00");
                        System.out.println("2. Product B ----- SellingNewStore ----- $3.00\n");

                        //Printing out all options for Products
                        System.out.println(productOptions);

                        productChoice = scanner.nextInt();
                        scanner.nextLine();

                        //declarations for product decision
                        int selectedProduct = 0;

                        do {
                            switch (productChoice) {
                                case 1:
                                    //View Detailed Info
                                    selectedProduct = selectingProduct();

                                    //TODO View Detailed information using selectedProduct
                                    System.out.println(selectedProduct);

                                    productChoiceOngoing = false;
                                    choiceOngoing = false;
                                    break;
                                case 2:
                                    //Add Product to Cart
                                    selectedProduct = selectingProduct();

                                    //TODO Add corresponding product to cart useing selectedProduct
                                    System.out.println(selectedProduct);

                                    productChoiceOngoing = false;
                                    choiceOngoing = false;
                                    break;
                                case 3:
                                    //Return to Main Menu
                                    choiceOngoing = true;
                                    productChoiceOngoing = false;
                                    break;
                                default:
                                    //Invalid Input Handling
                                    System.out.println(invalidInput);
                                    productChoiceOngoing = true;
                                    choiceOngoing = false;
                                    break;
                            }
                        } while (productChoiceOngoing);
                        signedIn = true;
                        break;
                    case 2:
                        //View sellers
                        //TODO List of Stores and sorting methods
                        choiceOngoing = false;
                        signedIn = true;
                        break;
                    case 3:
                        //Shopping Cart
                        System.out.println(shoppingCartWelcome);
                        //TODO Print Shopping Cart
                        System.out.println(shoppingCartOptions);

                        cartOptions = scanner.nextInt();
                        scanner.nextLine();

                        do {
                            switch (cartOptions) {
                                case 1:
                                    //Purchase Order
                                    //TODO Run Purchasing Method
                                    //TODO Run Purchase History Recording Method
                                    inShoppingCart = false;
                                    choiceOngoing = false;
                                    break;
                                case 2:
                                    //Remove an item
                                    //TODO Remove an Item method
                                    inShoppingCart = true;
                                    choiceOngoing = false;
                                    break;
                                case 3:
                                    //Clear Cart
                                    //TODO Remove an Item method (for all items in cart)
                                    inShoppingCart = true;
                                    choiceOngoing = false;
                                    break;
                                case 4:
                                    //Return to Main Menu
                                    inShoppingCart = false;
                                    choiceOngoing = true;
                                    break;
                                default:
                                    //Invalid Input
                                    System.out.println(invalidInput);
                                    inShoppingCart = true;
                                    choiceOngoing = false;
                                    break;
                            }
                        } while (inShoppingCart);
                        signedIn = true;
                        break;
                    case 4:
                        //Return to sign-in/Sign-out
                        choiceOngoing = false;
                        signedIn = false;
                        break;
                }
            } while (choiceOngoing);

        } while (signedIn);
    }

    public void productPrinting() {
        //TODO printing products
    }

    public static int selectingProduct() {
        int selectedProduct;

        System.out.println(selectingProduct);
        selectedProduct = scanner.nextInt();
        scanner.nextLine();

        return selectedProduct;
    }
}
