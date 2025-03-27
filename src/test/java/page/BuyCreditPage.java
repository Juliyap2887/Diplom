package page;

import com.codeborne.selenide.SelenideElement;
import data.CardInfo;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class BuyCreditPage {
    // заголовок
    private final SelenideElement paymentOnCredit = $$("h3").find(exactText("Кредит по данным карты"));
    // поля формы
    private final SelenideElement cardNumberField = $("[placeholder='0000 0000 0000 0000']");
    private final SelenideElement monthField = $("[placeholder='08']");
    private final SelenideElement yearField = $("[placeholder='22']");
    private final SelenideElement holderField = $(byText("Владелец")).parent().$(".input__control");
    private final SelenideElement cvcField = $("[placeholder='999']");
    private final SelenideElement continueButton = $$(".button").find(exactText("Продолжить"));

    // успешная операция
    private final SelenideElement notificationSuccess = $(".notification_status_ok");
    // отказ в операции
    private final SelenideElement notificationError = $(".notification_status_error");
    // проверка полей
    private final SelenideElement validatorFieldMessage = $(byText("Поле обязательно для заполнения"));
    private final SelenideElement wrongFormatMessage = $(byText("Неверный формат"));
    private final SelenideElement cardExpireMessage = $(byText("Истёк срок действия карты"));
    private final SelenideElement wrongExpirationMassage = $(byText("Неверно указан срок действия карты"));

    public BuyCreditPage() {
        paymentOnCredit.shouldBe(visible);
    }

    // заполнение формы
    public BuyCreditPage completedForm(CardInfo info) {
        cardNumberField.setValue(info.getNumber());
        monthField.setValue(info.getMonth());
        yearField.setValue(info.getYear());
        holderField.setValue(info.getHolder());
        cvcField.setValue(info.getCvc());
        continueButton.click();
        return new BuyCreditPage();
    }

    public void notificationSuccess() {
        notificationSuccess.shouldBe(visible, Duration.ofSeconds(15)); // успешная операция
    }

    public void notificationError() {
        notificationError.shouldBe(visible, Duration.ofSeconds(15)); // отказ в операции
    }

    // Поле обязательно для заполнения
    public void waitForValidationMassage() {
        validatorFieldMessage.shouldBe(visible);
    }

    // Неверный формат
    public void waitForWrongFormatMassage() {
        wrongFormatMessage.shouldBe(visible);
    }

    // Истек срок действия карты
    public void waitForCardExpiredMassage() {
        cardExpireMessage.shouldBe(visible);
    }

    // Неверно указан срок действия карты
    public void waitForWrongCardExpirationMassage() {
        wrongExpirationMassage.shouldBe(visible);
    }
}


