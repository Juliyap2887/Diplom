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

public class BuyCreditPageTest {

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
    void shouldBuyOnCreditAllFieldsValidApprovedCard() { //Успешная оплата тура в кредит по валидной карте
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.notificationSuccess();
        assertEquals("APPROVED", SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditWithValidHolderWithDoubleSurname() { // Заявка на оплату тура в кредит, поле Владелец с двойной фамилией
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolderWithDoubleSurname(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.notificationSuccess();
        assertEquals("APPROVED", SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditWithYearFiveYearsLongerCurrentOne() { // Заявка на оплату тура в кредит, год, который наступит через 5 лет
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getYearFiveYearsLongerCurrentOne(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.notificationSuccess();
        assertEquals("APPROVED", SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditAllFieldValidDeclinedCard() { // Заявка на оплату тура в кредит, заполненная данными карты со статусом Declined
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardDeclined(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.notificationError();
        assertEquals("DECLINED", SQLHelper.getCreditStatus());  //BUG
    }

    @Test
    void shouldBuyOnCreditWithEmptyFieldCardNumber() { // Поле Номер карты не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardNumberNotFilled(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditWithCardNumber15Digits() { // Поле номер карты из 15 цифр
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardNumber15Digits(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditWithCardNumber16Digits() { // Поле номер карты из 16 цифр
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardNumber16Digits(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.notificationError();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditMonthNotFilled() { // Поле Месяц не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getMonthNotFilled(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditPreviousMonth() { // Предыдущий месяц текущего года
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getPreviousMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getCreditStatus());
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
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditNoExistentMonth() { //Несуществующий месяц
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getNoExistentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditYearNotFilled() { // Поле Год не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getYearNotFilled(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditYearSixYearsLongerCurrentOne() { // год, который наступит через 6 лет
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getYearSixYearsLongerCurrentOne(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongCardExpirationMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditPreviousYear() { //предыдущий год
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getPreviousYear(),
                DataHelper.getValidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForCardExpiredMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditHolderNotFilled() { // поле Владелец не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getHolderNotFilled(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForValidationMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditHolderCyrillicName() { // владелец на кириллице
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getInvalidHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());  // BUG
    }

    @Test
    void shouldBuyOnCreditFilledWithSpecialCharactersHolder() { // поле Владелец заполнено спецсимволами
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getFilledWithSpecialCharactersHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());  // BUG
    }

    @Test
    void shouldBuyOnCreditFilledWithNumbersHolder() { // поле Владелец заполнено цифрами
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getFilledWithNumbersHolder(),
                DataHelper.getValidCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());  // BUG
    }

    @Test
    void shouldBuyOnCreditCvcNotFilled() { // CVC не заполнено
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getCvcNotFilled());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditCvcTwoDigits() { // CVC из 2-х цифр
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getCvcTwoDigits());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditCvcOneDigit() { // CVC из 1 цифры
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getCvcOneDigit());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }

    @Test
    void shouldBuyOnCreditFilledWithSpecialCharactersCvc() { // CVC заполнено спецсимволами
        CardInfo cardInfo = new CardInfo(
                DataHelper.getCardApproved(),
                DataHelper.getCurrentMonth(),
                DataHelper.getCurrentYear(),
                DataHelper.getValidHolder(),
                DataHelper.getFilledWithSpecialCharactersCvc());
        var mainPage = new MainPage();
        var payment = mainPage.goToPurchaseCreditPage();
        payment.completedForm(cardInfo);
        payment.waitForWrongFormatMassage();
        assertNull(SQLHelper.getCreditStatus());
    }
}
