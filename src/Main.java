import java.util.Scanner;
import java.lang.RuntimeException;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Выражение: ");
            String expression = scanner.nextLine();
            if (expression.isEmpty()) {
                throw new RuntimeException("Пусто!");
            }
            if (expression.matches("quit")) {
                System.out.println("Завершение работы");
                System.out.println("------------------");
                System.exit(0);
            }
            String result = SolExpression(expression);
            System.out.println("Ответ: " + result);
            System.out.println("------------------");
        }
    }
    public static boolean mathSights(String token) { // мат. знаки
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals(":");
    }
    public static int mathPriority(String token) { // мат. приоритет знаков
        return switch (token) {
            case "+", "-" -> 1;
            case "*", "/", ":" -> 2;
            default -> 0;
        };
    }
    public static String[] ReversePolishNotation(String[] tokens) { // обратная польская запись
        StringBuffer output = new StringBuffer();
        String[] operationList = new String[tokens.length];
        int level = -1;
        for (String token : tokens) {
            if (mathSights(token)) {
                while (level >= 0 && mathPriority(token) <= mathPriority(operationList[level]))
                    output.append(operationList[level--]).append(" ");
                operationList[++level] = token;
            } else if (token.equals("(")) operationList[++level] = token;
            else if (token.equals(")")) {
                while (!operationList[level].equals("(")) {
                    output.append(operationList[level--]).append(" ");
                }
                level--;
            } else output.append(token).append(" ");
        }
        while (level >= 0)
            output.append(operationList[level--]).append(" ");
        return output.toString().trim().split(" ");
    }
    public static String SolExpression(String expression) { // solution
        String[] tokens = ReversePolishNotation(expression.split(" "));
        Fraction[] operationList = new Fraction[tokens.length];
        int level = -1;
        for (String token : tokens) {
            if (mathSights(token)) {
                Fraction secondfrac = operationList[level--];
                Fraction firstfrac = operationList[level--];
                switch (token) {
                    case "+" -> operationList[++level] = firstfrac.add(secondfrac);
                    case "-" -> operationList[++level] = firstfrac.subtract(secondfrac);
                    case "*" -> operationList[++level] = firstfrac.multiply(secondfrac);
                    case ":", "/" -> operationList[++level] = firstfrac.divide(secondfrac);
                }
            }  else operationList[++level] = new Fraction(token);
        }
        return operationList[level].toMixedNumber();
    }
public static class Fraction {
    private int numerator;
    private int denominator;
    public Fraction(String fraction) {
        String[] fracparts = fraction.split("/");
        this.numerator = Integer.parseInt(fracparts[0]);
        this.denominator = Integer.parseInt(fracparts[1]);
    }
    public Fraction(int nu, int de) {
        if (de == 0) {
            throw new RuntimeException("Знаменатель не может быть равен 0!");
        }
        this.numerator = nu;
        this.denominator = de;
        if (de < 0)
            this.minusrep(); // -числитель/-знаменатель (и для перевода знака в числитель при отрицательном знаменателе)
    }
    void minusrep() {
        this.numerator *= -1;
        this.denominator *= -1;
    }
    public Fraction add(Fraction other) { // +
        int newNumerator = this.numerator * other.denominator + this.denominator * other.numerator;
        int newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator).simplify();
    }
    public Fraction subtract(Fraction other) { // -
        int newNumerator = this.numerator * other.denominator - this.denominator * other.numerator;
        int newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator).simplify();
    }
    public Fraction multiply(Fraction other) { // *
        int newNumerator = this.numerator * other.numerator;
        int newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator).simplify();
    }
    public Fraction divide(Fraction other) { // : (/)
        int newNumerator = this.numerator * other.denominator;
        int newDenominator = this.denominator * other.numerator;
        return new Fraction(newNumerator, newDenominator).simplify();
    }
    public Fraction simplify() { // Сокращение дроби
        int GreatestCommonMultiple = (int) Fraction.GreatestCommonMultiple(this.numerator, this.denominator);
        return new Fraction(this.numerator / GreatestCommonMultiple, this.denominator / GreatestCommonMultiple);
    }
    private static Object GreatestCommonMultiple(int a, int b) { // MOD
        return b == 0 ? a : GreatestCommonMultiple(b, a % b);
    }
    public String toString() {
        return this.numerator + "/" + this.denominator;
    }
    public String toMixedNumber() { // Смешанная дробь
        if (this.denominator == 1) return Integer.toString(this.numerator);
        
        if (Math.abs(this.numerator) > this.denominator) { // Math.abs для отрицательой смешанной дроби  (-3/2)
            String whole = Integer.toString((int) Math.floor(this.numerator / this.denominator)); //целая часть
            String frac = new Fraction(Math.abs(this.numerator % this.denominator), this.denominator).toString(); // дробь
            if (!(frac.equals("")))  whole += " " + frac; // разделение на целую часть и дробь
            return whole;
        }
        return this.toString();
    }
    }
}
// 1/2 + 1/3 = 5/6
// 1/2 + 1/3 * 3/4 = 3/4
// -1/-3 * 3/4 - 1/2 = -1/4
// -1/-3 * 3/4 - 1/2 + 2/4 = 1/4
// -1/3 * 3/4 - 1/2 * 2/1 = -1 1/4 (-5/4)
// ( -1/3 * 3/4 - 1/2 ) * 2/1 = -1 1/2 (-3/2)
// ( ( -1/3 * 3/4 - 1/2 ) * 2/1 ) = -1 1/2 (-3/2)
// ( ( ( -1/3 * 3/4 - 1/2 ) * 2/1 ) ) = -1 1/2 (-3/2)
// -1/0 * 3/4 - 1/2 = Exception in thread "main" java.lang.RuntimeException: Знаменатель не может быть равен 0!
// quit = Завершение работы