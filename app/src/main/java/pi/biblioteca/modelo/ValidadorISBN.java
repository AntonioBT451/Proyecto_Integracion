package pi.biblioteca.modelo;

public class ValidadorISBN {
    public static boolean validarISBN(String isbn) {
        // Eliminar guiones y espacios
        isbn = isbn.replaceAll("[\\s-]", "");

        // Validar ISBN-10
        if (isbn.length() == 10) {
            return comprobarISBN10(isbn);
        }

        // Validar ISBN-13
        if (isbn.length() == 13) {
            return comprobarISBN13(isbn);
        }

        return false;
    }

    private static boolean comprobarISBN10(String isbn) {
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Character.getNumericValue(isbn.charAt(i));
                sum += (10 - i) * digito;
            }

            // Último carácter puede ser X (equivalente a 10)
            char ultimoCaracter = isbn.charAt(9);
            if (ultimoCaracter == 'X') {
                sum += 10;
            } else {
                sum += Character.getNumericValue(ultimoCaracter);
            }

            return (sum % 11 == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean comprobarISBN13(String isbn) {
        try {
            int sum = 0;
            for (int i = 0; i < 13; i++) {
                int digito = Character.getNumericValue(isbn.charAt(i));
                sum += (i % 2 == 0) ? digito : digito * 3;
            }

            return (sum % 10 == 0);
        } catch (Exception e) {
            return false;
        }
    }
}
