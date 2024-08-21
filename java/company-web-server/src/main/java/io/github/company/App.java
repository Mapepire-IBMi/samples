package io.github.company;

public final class App {
    public static void main(String[] args) throws Exception {
        CompanyServer companyServer = new CompanyServer();
        companyServer.start();
    }
}
