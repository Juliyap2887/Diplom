package test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.CardInfo;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static data.RestApiHelper.sendPaymentRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:8080");
    }

    @BeforeEach
    public void cleanBase() {
        SQLHelper.cleanDatabase();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    void sendPaymentRequestAllFieldsValidApprovedCard(){ //Заявка Оплата по карте, заполненная валидными данными, карта со статусом Approved
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        sendPaymentRequest(cardInfo, "/api/v1/pay", 200);
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void sendPaymentRequestAllFieldsValidDeclinedCard(){ //Заявка Оплата по карте, заполненная валидными данными, карта со статусом Declined
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardDeclined(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        sendPaymentRequest(cardInfo, "/api/v1/pay", 400);
        assertEquals("DECLINED", SQLHelper.getPaymentStatus()); //BUG
    }

    @Test
    void sendPaymentRequestOnCreditAllFieldsValidApprovedCard(){ //Заявка на оплату в кредит, заполненная валидными данными, карта со статусом Approved
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        sendPaymentRequest(cardInfo, "/api/v1/credit", 200);
        assertEquals("APPROVED", SQLHelper.getCreditStatus());
    }

    @Test
    void sendPaymentRequestOnCreditAllFieldsValidDeclinedCard(){ //Заявка на оплату в кредит, заполненная валидными данными, карта со статусом Declined
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardDeclined(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        sendPaymentRequest(cardInfo, "/api/v1/credit", 400);
        assertEquals("DECLINED", SQLHelper.getCreditStatus()); // BUG
    }
}
