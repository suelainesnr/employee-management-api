package And.tec.CadastroFuncionario.infraEstructure.entity;

import And.tec.CadastroFuncionario.infraEstructure.vo.Cpf;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="tb_dependentes")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dependente {
    @ManyToOne
    private Funcionario funcionario;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Embedded
    private Cpf cpf;
    private LocalDate dataDeNascimento;
    private DependenteTipoEnum grauDeParentesco;


}
