// used by UserAccountManagement

public class User {
    private String password;
    private String email;
    private char buyer;
    private char seller;
    public User ( String password, String email, char buyer, char seller) {
        this.password = password;
        this.email = email;
        this.buyer = buyer;
        this.seller = seller;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBuyer (char buyer) {
        this.buyer = buyer;
    }

    public void setSeller (char seller) {
        this.seller = seller;
    }
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public char getBuyer () {
        return buyer;
    }

    public char getSeller () {
        return seller;
    }


}

