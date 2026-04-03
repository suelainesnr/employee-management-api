package And.tec.CadastroFuncionario.infraEstructure.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@Getter
@EqualsAndHashCode
public class Cpf {

    @Column(name = "cpf", nullable = false, unique = true)
    private String valor;

    protected Cpf() {
        // JPA precisa
    }

   private Cpf(String valor) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException("CPF não pode ser vazio");

        String cpfLimpo = valor.replaceAll("\\D", "");

        if (!cpfValido(cpfLimpo))
            throw new IllegalArgumentException("CPF inválido");

        this.valor = cpfLimpo;
    }

    private boolean cpfValido(String cpf) {
        if (cpf.length() != 11) return false;

        // evita 11111111111 etc
        if (cpf.chars().distinct().count() == 1) return false;

        return true;
        // 👉 depois você pode implementar algoritmo oficial
    }
    public static Cpf of(String valor) {
        return new Cpf(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}