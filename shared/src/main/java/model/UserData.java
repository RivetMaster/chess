package model;

public record UserData(String username, String password, String email) {

    public boolean verifyFields(){
        return username != null && password != null && email != null && !username.isBlank() && !password.isBlank() && !email.isBlank();
    }
}
