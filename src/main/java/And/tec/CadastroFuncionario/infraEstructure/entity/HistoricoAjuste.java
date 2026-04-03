package And.tec.CadastroFuncionario.infraEstructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_ajustes")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoAjuste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long funcionarioId;

    private BigDecimal percentual;

    private BigDecimal valorAtualSalario;

    private BigDecimal valorAposAjuste;

    @Column(length = 1024)
    private String motivo;

    private LocalDateTime dataAjuste;
}

