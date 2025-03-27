package data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DataHelper {

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Faker fakerCyrillic = new Faker(new Locale("ru"));

    public static String getCardApproved() {  // валидная карта
        return "4444 4444 4444 4441";
    }

    public static String getCardDeclined() { // невалидная карта
        return "4444 4444 4444 4442";
    }

    public static String getCardNumberNotFilled() { // поле каты пустое
        return "";
    }

    public static String getCardNumber15Digits() { // номер карты из 15 цифр
        return faker.numerify("#### #### #### ###");
    }

    public static String getCardNumber16Digits() { // номер карты из 16 цифр
        return faker.numerify("#### #### #### ####");
    }

    public static String getCurrentMonth() { //текущий месяц
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getMonthNotFilled() { //поле месяц пустое
        return "";
    }

    public static String getPreviousMonth() { //предыдущий месяц
        return LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getMonthConsistingZeros() { //месяц состоит из 00

        return "00";
    }

    public static String getNoExistentMonth() { //месяц несуществующий
        return "13";
    }

    public static String getCurrentYear() { //текущий год

        return LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String getPreviousYear() { //предыдущий год
        return LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String getYearNotFilled() { //поле год пустое
        return "";
    }

    public static String getYearSixYearsLongerCurrentOne() { // год, который наступит через 6 лет
        return LocalDate.now().plusYears(6).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String getYearFiveYearsLongerCurrentOne() { // год, который наступит через 5 лет
        return LocalDate.now().plusYears(5).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String getValidHolder() { // валидный владелец
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String holder = firstName + " " + lastName;
        return holder;
    }

    public static String getValidHolderWithDoubleSurname() { // валидный владелец с двойной фамилией
        String firstName = faker.name().firstName();
        String lastNamePart1 = faker.name().lastName();
        String lastNamePart2 = faker.name().lastName();
        String holder = firstName + " " + lastNamePart1 + " " + lastNamePart2;
        return holder;
    }

    public static String getInvalidHolder() { // владелец на кириллице
        String firstName = fakerCyrillic.name().firstName();
        String lastName = fakerCyrillic.name().lastName();
        String holder = firstName + " " + lastName;
        return holder;
    }

    public static String getHolderNotFilled() { // поле Владелец не заполнено
        return "";
    }

    public static String getFilledWithSpecialCharactersHolder() { // поле Владелец заполнено спецсимволами
        return "!№;%:?*";
    }

    public static String getFilledWithNumbersHolder() { //Владелец цифрами
        String firstName = faker.numerify("######");
        String lastName = faker.numerify("#######");
        String holder = firstName + " " + lastName;
        return holder;
    }

    public static String getValidCvc() { // Валидный CVC
        return faker.numerify("###");
    }

    public static String getCvcTwoDigits() { // CVC из 2-х цифр
        return faker.numerify("##");
    }

    public static String getCvcOneDigit() { // CVC из 1 цифры
        return faker.numerify("#");
    }

    public static String getFilledWithSpecialCharactersCvc() { // CVC заполнено спецсимволами
        return "!#$";
    }

    public static String getCvcNotFilled() { // CVC не заполнено
        return "";
    }
}
