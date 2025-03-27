package page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private final SelenideElement journeyDay = $("h2");
    private final SelenideElement buyButton = $$(".button").find(exactText("Купить"));
    private final SelenideElement buyCreditButton = $$(".button").find(exactText("Купить в кредит"));

    public MainPage() {
        journeyDay.shouldBe(visible);
    }

    public BuyPage goToPurchasePage() {
        buyButton.click();
        return new BuyPage();
    }

    public BuyCreditPage goToPurchaseCreditPage() {
        buyCreditButton.click();
        return new BuyCreditPage();
    }
}
