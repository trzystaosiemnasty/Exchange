package pl.javageek.exchangeproject.user;

class UsernameExistsException extends Exception {

    UsernameExistsException() {
        super("There is an account with that username");
    }
}
