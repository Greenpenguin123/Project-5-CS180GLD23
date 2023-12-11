# Project-5-CS180GLD23
## Project 5 Code
https://docs.google.com/document/d/e/2PACX-1vT_nb7r7Cz7eYHJ4xIoTHpqXsXvKFtZnckbf0b0GM83LvOojs2s5FFe3kHAl39BvYEwBd1wKauY1ovj/pub

https://docs.google.com/document/d/1jqfIAe-INKSBZyMT5KQkJru8bEljpDZqcHi9Gz7YRf8/edit

Project Report: https://docs.google.com/document/d/1VwJPnu6EoOC6sgGOipcFLa_z__7mVPm_PjU88PE-jEw/edit?usp=sharing

Server code github: https://github.com/Greenpenguin123/ServerCode


## Project 5 Code
# Hello, Welcome to Project-5-CS180GLD23, AKA P4 Marketplace!
### This project was completed by Andrew Lin, Sameer Murthy, Noelle Medrano

##### How to Compile/Run P4 Marketplace
Our main User Interface code is contained in UserAccountManager, so simply "running" that class will start the marketplace correctly.

##### Sumbission
Andrew Lin is our Project Manager and submitted the Vocareum workspace.
Noelle Medrano sumbitted the Presenation on Brightspace with a link to a youtube Video.
Sameer Murthy submitted the Project Report on Brightspace

### Buyer/Customer User Interface
###### All classes and methods described below are used/functional primarily in the buyer/customer section of the program

##### Consumer.java
All methods and functions related to the Consumer's actions in the program.
- **Consumer**
  Constructor that takes in the parameters of a String username and creates a CSV Shopping Cart file to contain the user's currents items in the Shopping Cart and a CSV file of that User's purchase history.
- **main**
- **viewMarketPlace**
  Prints an ArrayList of Products currently available in the Marketplace in the format of (index i + ". " + product)
- **getProductList**
  Returns an ArrayList of Products that is read from the Marketplace CSV file. Each line in the Marketplace CSV file is turned into a Product and is added to the ArrayList.
- **searchProducts**
  Searches current listings of Products using a User-entered keyword.
- **addProduct**
  Adds a User-selected Product to the CSV Shopping Cart file.
- **removeProduct**
  Removes an item from the User's Shopping Cart by removing all instances of that Product from the Shopping Cart CSV file.
- **printPurchaseHistory**
  Prints out for the User a list of that User's purchase history using the Purchase History CVS file that was created upon their account creation and added to upon purchasing items in the past.
- **purchaseProduct**
  A function of the Shopping Cart mechanism that purchases the User-selected product or products and verifies the quantity before doing so. This method also adds to the User's Purchase History CSV file and decreases the quantity of the product.
- **clearCart**
  Empties the Shopping Cart of the current User by erasing the Shopping Cart CSV file that was created upon account creation.
- **exportPurchaseHistory**
  Creates a file called User_PurchaseHistoryExport.CSV of all of the User's past purchases.
- **viewDashboard**
  Prints out a list of all stores, prompts User to sort dashboard by Number of Products Sold or by Products Purchased by User
- **sortDashboard**
  Sorts the dashboard by either Number of Products Sold or by Products Purchased by User depending on User-entered choice
- **showDescription**
  Prints out description of product by User-entered choice from CSV file of Marketplace Products

##### UserNavigation.java
Contains all Buyer UI options and redirects functionality to other methods
- **main**
  Contains all switch cases and main options avaiable for Buyer UI and the calls to other methods to implement functionality. Handles incorrect inputs and errors.

### Seller User Interface
###### All classes and methods described below are used/functional primarily in the seller section of the program

##### StoreplaceManager.java
Allows a user to acess all functions of a store once created.
- **StoreplaceManager**
  Constructs a store using a User-entered name of the store and the User's name to create a store file that contains data related to the store and it's products.
- **displayDashboard**
  Displays all options available to the User.
  - **createProduct**
    Accepts a User-entered Product name and creates a new Product with User-entered descriptors to a CSV file
  - **editProduct**
    Accepts a User-entered Product name and allows them to change information about the Product by directly changing the text in the CSV file realted to the product
  - **deleteProduct**
    Accepts a User-entered Product name and allows them to change information about the Product by directly changing the text in the CSV file realted to the product
  - **exportProduct**
    Exports a CSV file with all User-entered products and information
- **printItemsPurchasedStatistics**
  Calculates number of products purchased by a searchable Username, using that User's purchase history file
- **calculateStoreSales**
  For each store, calculates the number of sales the store has
- **calculateProductSales**
  For each product, calculates the number of sales of that product

##### SellerDashboard.java
Shows/Displayes all Seller UI and creates a file for data storage for that specific User.
- **SellerDashboard**
  Constructs the file that will contain a list of all stores owned/created by the User.
- **displayDashboard**
  Displays all options for the Seller User Interface. Additionally, it creates a file of all stores owned by the User.

### Market User Interface
###### All classes and methods described below are used/functional throughout the full market function (used/accessed by both buyers and sellers)

##### Product.java
Creates a new product with various descriptors.
- **Product**
  Constructs a new Product object using name, description, quantity available, price, and store selling the item.
- **getName**
  Returns the name of the product.
- **setName**
  Sets the name of the product.
- **getDescription**
  Returns the description of the product.
- **setDescription**
  Returns the description of the product.
- **getStore**
  Returns the name of the store.
- **setStore**
  Sets the name of the store.
- **getPrice**
  Returns the price of the product.
- **setPrice**
  Returns the price of the product.
- **getItemsSold**
  Returns the number of items sold of a specific store.
- **setItemsSold**
  Sets the number of items sold of a specific store.
- **getQuantityAvailable**
  Returns the quantity of the product available.
- **setQuantityAvailable**
  Sets the quantity of the product available.
- **incrementItemsSoldBy**
  Changes the number of products sold using the quantity.
- **toString**
  Prints in User-viewable format: name + " ----- " + store + " ----- " + price.
- **printForSort**
  Prints in User-viewable format: name + " ----- " + store + " ----- " + price + "-----" + quantityAvailable.
  
##### UserAccountManager.java
Main UI for all options both Seller and Buyer.
- **main**
  Displays and runs the first set of options which mostly relate to User sign-in, therefore redirecting the User to Buyer or Seller UI depending on their chosen role
- **gatherCreate**
  Gathers all User information in order to create an account. Puts all information into a UserData CSV File. 
- **gatherSignIn**
  Gathers all User information in order to sign-in to a preexisting account. Verifies entered data matches stored User Data.
- **logout**
  Logs current User's sign-in information out and takes the User back to the main menu.
- **deleteUser**
  Deletes a User's data on the UserData CSV file according to matched User input.
  
