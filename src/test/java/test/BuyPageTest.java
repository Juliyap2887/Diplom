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
import page.MainPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BuyPageTest {

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
    void shouldBuyAllFieldsValidApprovedCard() { // Заявка Оплата по карте, заполненная валидными данными карты со статусом Approved успешно одобрена банком
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.notificationSuccess();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyWithValidHolderWithDoubleSurname() { // Заявка Оплата по карте, поле Владелец с двойной фамилией
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolderWithDoubleSurname(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.notificationSuccess();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyWithYearFiveYearsLongerCurrentOne() { // Заявка Оплата по карте, год, который наступит через 5 лет
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getYearXYearsLongerCurrentOne(5),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.notificationSuccess();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyAllFieldValidDeclinedCard() { // Заявка Оплата по карте, заполненная данными карты со статусом Declined отклонена банком
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardDeclined(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.notificationError();
        assertEquals("DECLINED", SQLHelper.getPaymentStatus()); // BUG
    }

    @Test
    void shouldBuyWithEmptyFieldCardNumber() { // Поле Номер карты не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardNumberNotFilled(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyWithCardNumber15Digits() { // Поле номер карты из 15 цифр
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardNumber15Digits(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyWithCardNumber16Digits() { // Поле номер карты из 15 цифр
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardNumber16Digits(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.notificationError();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyMonthNotFilled() { // Поле Месяц не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getMonthNotFilled(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyPreviousMonth() { // Предыдущий месяц текущего года
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getPreviousMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyMonthConsistingZeros() { //Месяц состоит из 00
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getMonthConsistingZeros(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyNoExistentMonth() { // Несуществующий месяц
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getNoExistentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyYearNotFilled() { // Поле Год не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getYearNotFilled(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyYearSixYearsLongerCurrentOne() { // год, который наступит через 6 лет
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getYearXYearsLongerCurrentOne(6),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyPreviousYear() { //предыдущий год
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getPreviousYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForCardExpiredMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyHolderNotFilled() { // поле Владелец не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getHolderNotFilled(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForValidationMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyHolderCyrillicName() { // владелец на кириллице
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getInvalidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus()); // BUG
    }

    @Test
    void shouldBuyFilledWithSpecialCharactersHolder() { // поле Владелец заполнено спецсимволами
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getFilledWithSpecialCharactersHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus()); // BUG
    }

    @Test
    void shouldBuyFilledWithNumbersHolder() { // поле Владелец заполнено цифрами
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getFilledWithNumbersHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus()); // BUG
    }

    @Test
    void shouldBuyCvcNotFilled() { // CVC не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getCvcNotFilled());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyCvcTwoDigits() { // CVC из 2-х цифр
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getCvcTwoDigits());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyCvcOneDigit() { // CVC из 1 цифры
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getCvcOneDigit());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldBuyFilledWithSpecialCharactersCvc() { // CVC заполнено спецсимволами
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getFilledWithSpecialCharactersCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchasePage();
        payment.fillData(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getPaymentStatus());
    }
}
