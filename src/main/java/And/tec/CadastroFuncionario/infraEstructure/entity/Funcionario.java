package And.tec.CadastroFuncionario.infraEstructure.entity;

import And.tec.CadastroFuncionario.exceptions.FuncionarioException;
import And.tec.CadastroFuncionario.infraEstructure.vo.Cpf;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="tb_funcionarios")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Embedded
    private Cpf cpf;

    private String cargo;
    private BigDecimal salario;
    private LocalDate dataDeAdmissao;
    private LocalDate dataDeDemissao;
    private FuncionarioStatusEnum status;

    public  void ajustarSalario (BigDecimal porcentagem) {
      salario= salario.add(salario.multiply(porcentagem).divide(BigDecimal.valueOf(100)));

    }
    public void validarSalarioEPorcentagem(BigDecimal salario, BigDecimal porcentagem) {
        if (salario == null || porcentagem == null)
            throw new IllegalArgumentException("Valores não podem ser nulos");

        if (salario.signum() <= 0 || porcentagem.signum() <= 0)
            throw new IllegalArgumentException("Valores devem ser maiores que zero");

    }

    public void alterarStatus(FuncionarioStatusEnum novoStatus) {
        if (novoStatus == null)
            throw new IllegalArgumentException("Status não pode ser nulo");

        if (this.status == novoStatus )
            throw new IllegalStateException("Funcionário já está com o status " + novoStatus);
        if (this.status == FuncionarioStatusEnum.ATIVO && novoStatus == FuncionarioStatusEnum.INATIVO) {
            this.status = novoStatus;
            return;
        }

        if (this.status == FuncionarioStatusEnum.ATIVO && novoStatus == FuncionarioStatusEnum.SUSPENSO) {
            this.status = novoStatus;
            return;
        }

        if (this.status == FuncionarioStatusEnum.INATIVO && novoStatus == FuncionarioStatusEnum.ATIVO) {
            this.status = novoStatus;
            return;
        }

        if (this.status.equals(FuncionarioStatusEnum.SUSPENSO)  && novoStatus.equals(FuncionarioStatusEnum.ATIVO) ) {
            this.status = novoStatus;
            return;
        }
        else throw new FuncionarioException("Transição de status inválida: " + this.status + " para " + novoStatus);
    }


}