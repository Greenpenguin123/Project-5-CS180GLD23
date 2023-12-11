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
To run this code, start MarketServer.java, and then start MarketClient.java which start the client's side code.

##### Sumbission
Andrew Lin is our Project Manager and submitted the Vocareum workspace.
Noelle Medrano sumbitted the Presenation on Brightspace with a link to a youtube Video.
Sameer Murthy submitted the Project Report on Brightspace

### Documentation for code: https://docs.google.com/document/d/1VwJPnu6EoOC6sgGOipcFLa_z__7mVPm_PjU88PE-jEw/edit?usp=sharing
  
#### Class Description:
-Auth: Manages UserAccountManager functionality and validation.

-CmdIO: Translates client-side requests into JSON format for server comprehension.

-Loginsession: Processes user requirements, parses JSON requests, and invokes MarketData interface.

-UserManager: Manages user data stored in a CSV file, ensuring multi-thread safety through read and write locks.

-MarketClient: Initiates the client perspective of the code.

-MarketData: Oversees storage of stores, products, sales records, and buyer shopping carts, providing interfaces for buyer and seller operations. All data operations are thread-safe.

-MarketServer: Facilitates server-side operations.

-ProductBrowseResult: Focuses on translating search requests into JSON format for server transmission.

-BuyerRecordWnd/StoreSaleRecordsWnd: Interfaces to display data for customers/sellers based on buyer purchases.

-Request: Determines the type of request sent and dictates the server's action.

-SalesRecord: Records each purchase and translates it into JSON format for storage on the server.

-LoginWnd: Interface for displaying login page, account creation, or account deletion.

-BuyerWnd/SellerWnd: Interface for buyers/sellers.

-LogUtility: Logs actions taken by users.
